/*
 * The MIT License
 *
 * Copyright 2014 Jon Arney, Ensor Robotics.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.ensor.robots.scheduler;

import org.ensor.data.atom.DictionaryAtom;
import org.ensor.data.atom.ListAtom;
import org.ensor.data.atom.Atom;
import java.util.*;
import java.util.concurrent.*;

import java.io.PrintWriter;
import java.io.StringWriter;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import org.ensor.data.atom.log.AtomLogger;

public class BioteManager {
        protected final static Logger mLogger = Logger.getLogger(BioteManager.class .getName()); 
        private final static float                                      SECONDS_PER_NANOSECOND = 1f / 1000000000f;
    
        private final AtomicBoolean                                     mRunning;

        /*
         * This is the list of all biotes indexed by biote ID.
         */
	private final ConcurrentHashMap<Integer,Biote>			mBiotes;
        private final AtomicInteger                                     mBioteIdGenerator;

        /* This is the scheduler data structure.  Biotes in this
         * data structure are ready to be processed because they
         * have pending events.  They are removed from this structure
         * when their events have been processed and are added to this
         * structure when their events are queued.
         */
        private final ConcurrentMap<Long, Long>                         mThreadTimes;
	private final ConcurrentLinkedQueue<Biote>                      mReadyBiotes;
	private final int                                               mThreadPoolSize;
        private final ExecutorService                                   mThreadPool;
        
        /* This is the scheduler data structure.  Biotes in this
         * data structure are ready to be processed because they
         * have pending events.  They are removed from this structure
         * when their events have been processed and are added to this
         * structure when their events are queued.
         */
        private final ConcurrentLinkedQueue<Biote>                      mReadyBlockingBiotes;
        private final int                                               mBlockingThreadPoolSize;
        private final ExecutorService                                   mBlockingThreadPool;

        /*
         * This is the list of timer tasks.  Timer tasks are executed on a single
         * timer thread.  Adding or removing a scheduled task is 'lightweight'
         * and does not start or stop a thread.  Timer tasks should do nothing but
         * stimulate events.  They should not do heavyweight processing themselves.
         */
	private final ConcurrentHashMap<Integer,TimerTask>		mTimers;
	private final AtomicInteger					mTimerIds;
	private final Timer                                             mTimer;

	private final ConcurrentHashMap<Integer,Boolean>                mPendingBiotes;
        private ConcurrentHashMap<String, SystemStat> mStats;

        private static final ConcurrentHashMap<String, BioteManager>    mInstances = new ConcurrentHashMap<String, BioteManager>();

        protected final String mInstanceId;
        
        private final Properties mProperties;
        
        public BioteManager(String aInstanceId, Properties aProperties) {
                mInstanceId = aInstanceId;
                mProperties = aProperties;
                
                mInstances.put(mInstanceId, this);
		mTimerIds = new AtomicInteger(0);
		mTimer = new Timer();
                mBiotes = new ConcurrentHashMap<Integer,Biote>();
                mBioteIdGenerator = new AtomicInteger(Constants.BIOTE_FIRST_GENERATED_ID);
                mRunning = new AtomicBoolean(true);
                mTimers = new ConcurrentHashMap<Integer, TimerTask>();
                mStats = new ConcurrentHashMap<String, SystemStat>();
                // Initialize the normal event handling threads
                mThreadTimes = new ConcurrentHashMap<Long, Long>();
		mReadyBiotes = new ConcurrentLinkedQueue<Biote>();
                mThreadPoolSize = Math.max(Math.min(getShardVariable("threadPoolSize", 4), 40), 1);
                mLogger.warning(mInstanceId + ":Normal thread pool is " + mThreadPoolSize);

                mThreadPool = Executors.newFixedThreadPool(mThreadPoolSize);
		for (int i = 0; i < mThreadPoolSize; i++) {
                    mThreadPool.submit(new BioteThread(mReadyBiotes, mThreadTimes, mRunning, this));
                }
                
                // Initialize the event handling threads for events that block, and use low cpu
		mReadyBlockingBiotes = new ConcurrentLinkedQueue<Biote>();
                mBlockingThreadPoolSize = Math.max(Math.min(getShardVariable("blockingThreadPoolSize", 4), 40), 1);
                mLogger.warning(mInstanceId + ":Blocking thread pool is " + mBlockingThreadPoolSize);

                mBlockingThreadPool = Executors.newFixedThreadPool(mBlockingThreadPoolSize);
		for (int i = 0; i < mBlockingThreadPoolSize; i++)
                    mBlockingThreadPool.submit(new BioteThread(mReadyBlockingBiotes, null, mRunning, this));

                mPendingBiotes = new ConcurrentHashMap<Integer,Boolean>();
	}
        @Override
        public Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException();
        }
	public void shutdown() {
            logString(true, 0, "Beginning BioteManager shutdown sequence...");
            mRunning.set(false);
            try {
                mThreadPool.shutdown();
                mThreadPool.awaitTermination(30, TimeUnit.SECONDS);
            }
            catch( InterruptedException error ) {
            }
            try {
                mBlockingThreadPool.shutdown();
                mBlockingThreadPool.awaitTermination(30, TimeUnit.SECONDS);
            }
            catch( InterruptedException error ) {
            }
            
            mTimer.cancel();
            
            logString(true, 0, "Biote manager is no longer running.  Ther server is effectively down.");
	}
        protected void logString(boolean system, int bioteId, String message) {
            if (!system) return;
            long threadId = Thread.currentThread().getId();
            mLogger.info( mInstanceId + ":" + "[" + threadId + "]" + "[" + bioteId + "]" + message);
        }
        protected void logString(boolean system, int bioteId, String message, Atom atom) {
            if (!system) return;
            long threadId = Thread.currentThread().getId();
            if (atom == null) {
                logString(system, bioteId, message + " : (null)");
            } else {
                AtomLogger.dump(mLogger,
                        mInstanceId + ":" + "[" + threadId + "]" +
                                "[" + bioteId + "]" + message,
                        atom);
            }
        }

        //=============================================================================================
        /**
         * Create a new biote with the given biote type and return its biote ID .  The biote will not be associated with
         * a terminal.
         * @param aBioteType Type of biote to create.  This type ID must have previously been registered using the {@link org.ensor.robots.scheduler.BioteManager#registerBioteType registerBioteType} call.
         * @throws Exception
         */
        public int createBiote( Biote aBiote ) throws Exception {
            int newBioteId = mBioteIdGenerator.addAndGet(1);
            createBiote(aBiote, newBioteId);
            return newBioteId;
        }

        //=============================================================================================
        /**
         * Create a new biote with the given biote ID and the given biote type.  The biote will not be associated with
         * a terminal.
         * @param aBioteType Type of biote to create.  This type ID must have previously been registered using the {@link org.ensor.robots.scheduler.BioteManager#registerBioteType registerBioteType} call.
         * @param aBioteId The ID of the biote to create.  Note that biote IDs must be unique on a given server.  It is up to the caller to ensure that the ID specified refers to a new, unique biote ID.
         * @throws Exception
         */
        protected void createBiote( Biote aBiote, int aBioteId ) throws Exception {
            // We need to ensure that two biotes are never created with the same biote id.
            // Using putIfAbsent atomically replaces the value in the pending biotes table with true, and
            // returns the old value.  If we get back true, then someone is already creating the biote, and
            // this creation attempt is an error.
            Boolean pendingCreation = mPendingBiotes.putIfAbsent(aBioteId, Boolean.TRUE);
            if( pendingCreation != null )
                throw new IllegalArgumentException("The is already a biote being created with id: " + aBioteId);
            
            // We now know that noone else can attempt to create the biote
            // The very last thing we do is remove the entry from the pending biotes map; ensure that happens
            try {
                // If the biote already exists, this creation attempt is a failure.
                // We don't need to worry about the biote being added to the map by others during this function
                // since it is impossible for anyone else to create the biote right now
                if( mBiotes.containsKey(aBioteId) )
                    throw new IllegalArgumentException("The is already an existing biote with id: " + aBioteId);

                aBiote.setBioteId(aBioteId);
                
                // Create the biote and add it to the biote table
                addBiote(aBiote);
            }
            finally {
                mPendingBiotes.remove(aBioteId);
            }

            // If there were no errors, then the biote should have been created.
            // We no need to tell it to inialize itself
            Event initEvent = new Event("Event-Init");
            sendStimulus(aBioteId, initEvent, aBioteId);
        }

        //=============================================================================================
        public void checkThreadActivity() {
            long now = System.nanoTime();

            for( Map.Entry<Long, Long> entry : mThreadTimes.entrySet() ) {
                float elapsedSeconds = (now - entry.getValue()) * SECONDS_PER_NANOSECOND;
                if( elapsedSeconds >= Constants.LONG_EVENT_SECONDS )
                    logString(true, 0, String.format("[%d] This thread has been processing a biote's events for %.2f seconds!", entry.getKey(), elapsedSeconds));;
            }
        }

	private void addBiote(Biote b) {
		mBiotes.put(b.getBioteId(), b);
	}
        protected void __protected_friend_Biote__removeBiote(int bioteId) {
            Biote biote = mBiotes.remove(bioteId);
            logString(true, bioteId, "Biote is now destroyed...");
        }
        public int getBioteCount() {
            return mBiotes.size();
        }
        /**
         * This function sends a message to a biote given by the biote ID specified.  The message is placed on the target
         * biote's message queue and is processed at its earliest opportunity.
         * @param bioteId Biote ID of the biote that should receive the message.
         * @param msg Message to be sent.
         * @param sourceBioteId Biote ID of the biote that is sending the message.
         * @return Returns false if the biote does not exist and returns true if the biote existed so that the event could be sent.
         */
	public boolean sendStimulus(int bioteId, Event msg, int sourceBioteId) {
                Biote b = mBiotes.get(bioteId);
                if (b == null) {
                    logString(true, sourceBioteId, "Dropping event: '" + msg.getEventName() + "' to non-existent biote: " + bioteId);
                    return false;
                }
                sampleStat("BioteManager.java:sendStimulus");
                int targetBioteId = b.getTargetBioteId(msg.getEventName());
                Biote targetBiote = mBiotes.get(targetBioteId);
                if (targetBiote == null) {
                    logString(true, sourceBioteId, "Event: '" + msg.getEventName() + "' should have been routed to biote: " + targetBioteId + " which did not exist.  Sending to biote " + bioteId + " instead.");
                    targetBiote = b;
                }
                targetBiote.__protected_friend_BioteManager__enqueueStimulus(this, msg);
                logString(Constants.LOG_BIOTE_MANAGER, targetBiote.getBioteId(), "BioteManager.sendStimulus:" + msg.getEventName());
                logString(Constants.LOG_BIOTE_MANAGER, targetBiote.getBioteId(), "BioteManager.sendStimulus", msg.serialize());
                return true;
        }
        protected void __protected_friend_Biote__scheduleBiote(Biote b, boolean useBlockingQueue) {
            if( useBlockingQueue )
                mReadyBlockingBiotes.add(b);
            else
                mReadyBiotes.add(b);
        }
        /**
         * Starts a timer for the given Biote ID.  When the timer expires,
         * send the given message to that Biote ID.  The timer
         * can be a repeating timer, in which case, the timer will continue to
         * fire that event over and over again.  This function
         * returns the ID of the timer created so that it can be stopped using
         * {@link org.ensor.robots.scheduler.BioteManager#cancelTimer cancelTimer}.
         *
         * If the Biote is destroyed before the timer expires, the timer will
         * still go off and attempt to send its message to the
         * Biote.  In that case, the timer will simply fail to send the
         * message (because the Biote is not there anymore) and will cancel
         * the timer.
         * 
         * @param bioteId Biote ID of the Biote that the timer belongs to.
         * @param delay Delay (in ms) before the timer expires.
         * @param msg Message to send to Biote when the timer expires.
         * @param repeating Boolean value to indicate whether this is a
         *                  repeating timer or not.
         * @return Returns a unique timer ID for the timer that was just
         *                 created.
         */
	public int startTimer(int bioteId, int delay, Event msg, boolean repeating) {
            int timerId = mTimerIds.addAndGet(1);
            TimerTask task = new StimulateEventTask(bioteId, msg, repeating, timerId, this);
            mTimers.put(timerId, task);
            sampleStat("BioteManager.java:startTimer");
            if (repeating) {
                    mTimer.schedule(task, delay, delay);
            }
            else {
                    mTimer.schedule(task, delay);
            }
            return timerId;
	}
        /**
         * Cancels a timer based on the timer's ID as returned by {@link org.ensor.robots.scheduler.BioteManager#startTimer startTimer}.
         * 
         * @param timerId
         */
	public void cancelTimer(int timerId) {
            sampleStat("BioteManager.java:cancelTimer");
            TimerTask task = mTimers.remove(timerId);
            if (task == null) {
                    return;
            }
            task.cancel();
	}
        /**
         * This function is a static utility method for returning a stack trace for a given exception.
         * @param t Throwable exception to examine to find a stack trace.
         * @return Returns a string representation of a stack trace for the given event.
         */
        public static String getStackTrace(Throwable t)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw, true);
            t.printStackTrace(pw);
            pw.flush();
            sw.flush();
            return sw.toString();
        }
        public int getShardVariable(String variableName, int defaultValue) {
            String strValue = getShardVariable(variableName, "" + defaultValue);
            try {
                return Integer.parseInt(strValue);
            }
            catch (Exception ex) {
                return defaultValue;
            }
        }
        public String getShardVariable(String variableName, String defaultValue)
        {
            return mProperties.getProperty(variableName, defaultValue);
        }
        public void sampleStat(String statName) {
            sampleStat(statName, 1);
        }
        public void sampleStat(String statName, int value) {
            SystemStat newStat = new SystemStat(statName);
            SystemStat stat = mStats.putIfAbsent(statName, newStat);
            if (stat == null) stat = newStat;
            synchronized (stat) {
                stat.sample(value);
            }
        }
        public ListAtom flushStats() throws Exception {
            ListAtom statsList = ListAtom.newAtom();
            synchronized (mStats) {
                for (SystemStat stat : mStats.values()) {
                    DictionaryAtom statDict = statsList.newDictionary();
                    statDict.setString("name", stat.mName);
                    statDict.setInt("samples", stat.mSamples);
                    statDict.setInt("min", stat.mMin);
                    statDict.setInt("max", stat.mMax);
                    statDict.setInt("total", stat.mTotal);
                }
                mStats.clear();
            }
            return statsList;
        }
};

class BioteThread implements Runnable {
        private volatile AtomicBoolean                  mRunning;
	private final ConcurrentLinkedQueue<Biote>      mReadyBiotes;
        private final ConcurrentMap<Long, Long>         mTimeMap;
        private final BioteManager                      mBioteManager;
	BioteThread(ConcurrentLinkedQueue<Biote> readyBiotes, ConcurrentMap<Long, Long> aTimeMap, AtomicBoolean running, BioteManager aBioteManager) {
		mReadyBiotes = readyBiotes;
                mTimeMap = aTimeMap;
                mRunning = running;
                mBioteManager = aBioteManager;
	}
	public void run() {
                mBioteManager.logString(Constants.LOG_BIOTE_MANAGER, 0, "Starting event handling thread...");
		while (mRunning.get()) {
			checkBiotes();
		}
                mBioteManager.logString(Constants.LOG_BIOTE_MANAGER, 0, "Terminating event handling thread...");
	}
	public void checkBiotes() {
            Biote b = mReadyBiotes.poll();
            boolean idle = true;
            if (b != null) {
                idle = false;
                try {
                    markStart();
                    b.__protected_friend_BioteThread__processEvents();
                    markStop();
                }
                catch (Exception ex) {
                    mBioteManager.mLogger.severe("BioteManager:Exception -> " + ex.toString() + "\n\r" + BioteManager.getStackTrace(ex));
                }
                catch( Error ex ) {
                    mBioteManager.mLogger.severe("BioteManager:Error -> " + ex.toString() + "\n\r" + BioteManager.getStackTrace(ex));
                    mBioteManager.shutdown();
                }
            }
            // If we did not have any messages for any biotes to process
            // then we will sleep.
            if (idle) {
                try {
                    Thread.sleep(10);
                }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
	}

        private void markStart() {
            if( mTimeMap != null )
                mTimeMap.put(Thread.currentThread().getId(), System.nanoTime());
        }

        private void markStop() {
            if( mTimeMap != null )
                mTimeMap.remove(Thread.currentThread().getId());
        }
};

class StimulateEventTask extends TimerTask {
	private int			mBioteId;
	private Event               mEPropTree;
        private boolean                 mRepeating;
        private int                     mTimerId;
        private BioteManager            mBioteManager;
        
	StimulateEventTask(int bioteId, Event msg, boolean repeating, int timerId, BioteManager aBioteManager) {
		mBioteId = bioteId;
		mEPropTree = msg;
                mRepeating = repeating;
                mTimerId = timerId;
                mBioteManager = aBioteManager;
	}
	public void run() {
		try {
			boolean rc = mBioteManager.sendStimulus(mBioteId, mEPropTree, mBioteId);
                        if (!rc) {
                            mRepeating = false;
                        }
		}
                catch (Exception ex) {
                    mBioteManager.logString(true, mBioteId, "StimulateEventTask:Exception -> " + ex.toString() + "\n\r" + BioteManager.getStackTrace(ex));
                }
                catch( AssertionError ex ) {
                    mBioteManager.logString(true, mBioteId, "BioteManager:Error -> " + ex.toString() + "\n\r" + BioteManager.getStackTrace(ex));
                    mBioteManager.shutdown();
                }

                if( !mRepeating ) {
                    mBioteManager.cancelTimer(mTimerId);
                }
	}
};
