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

import org.ensor.data.atom.Atom;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import java.nio.channels.SocketChannel;
/**
 * The Biote is the primitive implementation element in this system.  A Biote
 * has the following properties:
 * <ul>
 * <li> Code executed on a Biote or its derived classes is guaranteed by the
 *      system to be called from only one thread at a time.
 *      Therefore, no synchronization code is necessary in order to ensure
 *      safe operation of the Biote provided that the Biote's methods do not
 *      cross thread boundaries themselves.
 * <li> Communication to a client can be performed through various forms of
 *      the sendTerm() function without worrying about how to route
 *      that message to the client.
 * <li> Biote event handlers are presumed to be short-lived operations involving
 *      computation and dispatch.  Methods causing direct synchronous I/O
 *      are discouraged in order to avoid blocking the Biote's other activities
 *      and ability to handle events in a timely fashion.
 * </ul>
 * Code is expected to derive from the Biote class or one of its subclasses.
 * @author Jon
 */
public abstract class Biote {
    private final BioteManager mBioteManager;
	private final HashMap<String,LinkedList<IEventHandler> >                 mEventSubscriptions;
	private int								mBioteId;
        protected final boolean                                                 mUseBlockingQueue;

        private static final int                                                BIOTE_STATE_IDLE = 0;
        private static final int                                                BIOTE_STATE_QUEUED = 1;
        private static final int                                                BIOTE_STATE_PROCESSING = 2;
        private static final int                                                BIOTE_STATE_TERMINATING = 3;

        // The state lock must always be accquired prior to manipulating the event queue and processing state.
        // both members should be manipulated together in an atomic manner
        private final Object                                                    mStateLock;
        private final HashMap<String,Integer>                                   mEventRouting;
	private final Queue<Event>                                       	mRequest;
        private int                                                             mProcessingState;

        /**
         * The constructor for a biote should never be called explicitly.  To create a new biote, register
         * the biote type using {@link org.ensor.robots.scheduler.BioteManager#registerBioteType registerBioteType}
         * and then create the biote using {@link org.ensor.robots.scheduler.BioteManager#createBiote createBiote}.
         *
         * @param bioteId
         */
	protected Biote(
                final BioteManager aBioteManager,
                final boolean useBlockingQueue) {
                mBioteManager = aBioteManager;
		mBioteId = 0;
                mStateLock = new Object();
		mRequest = new LinkedList<Event>();
                mEventRouting = new HashMap<String,Integer>();
                mProcessingState = BIOTE_STATE_IDLE;
                mEventSubscriptions = new HashMap<String,LinkedList<IEventHandler> >();
                mUseBlockingQueue = useBlockingQueue;

                // Listen for the init event
                subscribe("Event-Init", new IEventHandler() {
                    public void process(Event msg) throws Exception {
                        onInit(msg);
                    }
                });

                // Listen for the init event
                subscribe("Event-Finalize", new IEventHandler() {
                    public void process(Event msg) throws Exception {
                        onFini(msg);
                    }
                });
	};

        //=====================================================================
        private void onFini(Event message) throws Exception {
            log(Constants.LOG_BIOTE_CORE, "Biote is being destroyed now...");

            synchronized(mStateLock) {
		mProcessingState = BIOTE_STATE_TERMINATING;
            }
            
            onFinalize(message);
        }
        
        /**
         * This callback is invoked when the biote is being instantiated.
         */
        protected abstract void onInit(Event message) throws Exception;
        
        /**
         * This callback is invoked just as the biote is being destroyed.  This
         * is the biote's chance to send any final messages or release resources.
         */
        protected abstract void onFinalize(Event message) throws Exception;

        /**
         * Subscribe this biote to the given event.  When that event is stimulated, the given handler's process
         * function will be called.
         * 
         * @param event Name of event to subscribe to.
         * @param h Event handler to be called when the event is received.
         */
	public void subscribe(String event, IEventHandler h) {
            LinkedList<IEventHandler> subscribers = mEventSubscriptions.get(event);
            if (subscribers == null) {
                    subscribers = new LinkedList<IEventHandler>();
                    mEventSubscriptions.put(event, subscribers);
            }
            subscribers.add(h);
	}

        /**
         * Unsubscribe _all_ of this Biote's handlers for the given event.
         */
        public void unsubscribeHandlers( String event ) {
            mEventSubscriptions.remove(event);
        }

        /**
         * Cause the given event to be handled immediately.  This does not 
         * send the event to any other party, but instead causes all registered
         * handlers for the event to be executed immediately.
         *
         * @param msg
         */
        public void stimulate(Event msg) {
            mBioteManager.sampleStat("Biote.java:stimulate");
            try {
                String eventName = msg.getEventName();
                LinkedList<IEventHandler> subscribers = mEventSubscriptions.get(eventName);
                if (subscribers == null) {
                    log(true, "Biote.stimulate():" + eventName + " has no subscribers.");
                    return;
                }
                for( IEventHandler h : subscribers ) {
                    h.process(msg);
                    log(Constants.LOG_BIOTE_CORE, "Biote.processEvents():" + eventName);
                    log(Constants.LOG_BIOTE_CORE, "Biote.processEvent:", msg.serialize());
                }
            }
            catch (Exception ex) {
                log(true, "Biote.stimulate():" + ex.toString());
                log(true, BioteManager.getStackTrace(ex));
            }
        }
        /**
         * Returns the ID of the biote.
         *
         * @return
         */
	public int getBioteId() {return mBioteId;}
	protected void setBioteId(int bioteId) {mBioteId = bioteId;}
	public int startTimer(int delayMS, Event msg, boolean repeating) {
		return mBioteManager.startTimer(getBioteId(), delayMS, msg, repeating);
	}
        public void cancelTimer(int timerId) {
            mBioteManager.cancelTimer(timerId);
        }
        /**
         * Get the biote ID that the message should be sent to.
         * If we have made a routing entry for this biote then
         * route the message to that biote.  Otherwise, the message
         * should be routed to us.
         * @param eventName
         * @return
         */
        public int getTargetBioteId(String eventName) {
            Integer targetBioteId = null;
            synchronized(mStateLock) {
                targetBioteId = mEventRouting.get(eventName);
            }
            if (targetBioteId != null) {
                return targetBioteId.intValue();
            }
            return mBioteId;
        }
        /**
         * For the given event name, route any messages to us
         * directly to the given biote ID.
         * 
         * @param eventName
         * @param bioteId
         */
        public void setMessageRoute(String eventName, Integer bioteId) {
            synchronized(mStateLock) {
                if (bioteId == null) {
                    mEventRouting.remove(eventName);
                }
                else {
                    mEventRouting.put(eventName, bioteId);
                }
            }
        }
        /**
         * Causes the biote to enter the terminating state.  The biote will be destroyed
         * the next time that biote gets a time slice in the event processing queue.  Any events pending
         * for the biote at the time this function is called will be lost and no opportunity will be given
         * to handle them in the future.
         */
	public void shutdown(final boolean immediate) {
            Event shutdownMessage = new Event("Event-Finalize");
            if (immediate) {
                stimulate(shutdownMessage);
            } else {
                startTimer(5000, shutdownMessage, false);
            }
            log(Constants.LOG_BIOTE_CORE,
                    "Biote is now scheduled for destruction.");
	}
        /**
         * This function is used to stimulate events to other biotes.  The event is sent through
         * the biote manager and the receiving biote places it on its event queue.  The event is
         * handled when the biote's next opportunity to handle events comes up, subject to the
         * event processing fairness rules.
         * @param bioteId
         * @param msg
         * @throws Exception
         */
	public void sendStimulus(int bioteId, Event msg) {
            mBioteManager.sendStimulus(bioteId, msg, getBioteId());
	}
        /**
         * This function logs a message to the smartfox log.
         * @param system
         * @param message
         */
        public void log(boolean system, String message) {
            mBioteManager.logString(system, getBioteId(), message);
        }
        /**
         * This function logs a message to the smartfox log along with a dump of the given atom.
         * The message is a prefix to every line of the atom's dump.
         * @param system
         * @param message
         * @param atom
         */
        public void log(boolean system, String message, Atom atom) {
            mBioteManager.logString(system, getBioteId(), message, atom);
        }

        /**
         * This call should only be made from a biote thread.
         * This function processes any events queued by __protected_friend_BioteManager__enqueueStimulus()
         * This function implements the 'fairness' rules for processing biote events
         * and processes some number of events on a biote before yielding
         * control back to the thread so that it can process other biotes.
         */
	public void __protected_friend_BioteThread__processEvents() {
            try {
                // Process up to n events for this biote
                int eventsLeftToProcess = 10;
                while ( --eventsLeftToProcess >= 0 ) {
                    // If the biote is terminating then we should not handle any events
                    // Otherwise retrieve the next event and mark the biote as handling it
                    Event msg = null;
                    synchronized (mStateLock) {
                        if( BIOTE_STATE_TERMINATING == mProcessingState )
                            break;
                        mProcessingState = BIOTE_STATE_PROCESSING;
                        msg = mRequest.poll();
                    }

                    // Handle the event (if any).  Ensure that failure to process an event
                    // does not prevent this biote from processing future events
                    if (msg == null)
                        break;
                    stimulate(msg);
                }
            }

            // We must ensure that the state of the biote is valid regardless of how the function terminates
            // If the biote is terminating, then we shutdown now
            // If there are more events pending, then we must place ourself back in the ready queue
            // Otherwise this biote is now idle
            finally {
                boolean shutdown = false;
                synchronized (mStateLock) {
                    if( BIOTE_STATE_TERMINATING == mProcessingState ) {
                        shutdown = true;
                    }
                    else if (mRequest.isEmpty()) {
                        mProcessingState = BIOTE_STATE_IDLE;
                    }
                    else {
                        mProcessingState = BIOTE_STATE_QUEUED;
                        mBioteManager.__protected_friend_Biote__scheduleBiote(this, mUseBlockingQueue);
                    }
                }

                if( shutdown ) {
                    mBioteManager.__protected_friend_Biote__removeBiote(mBioteId);
                }
            }
	}
        /**
         * This function should only be called by the biote manager during the course
         * of a sendStimulus call.  In C++, this function would be marked as protected and
         * with a friend class of the biote manager.  This function appends the
         * given message to the biote's queue of events and schedules the biote's execution.
         * @param msg
         * @throws Exception
         */
        public void __protected_friend_BioteManager__enqueueStimulus(BioteManager bioteManager, Event msg) {
            boolean dropped = false;
            boolean schedule = false;
            
            synchronized (mStateLock) {
                // We should not enqueue any events to a terminating biote
                if( BIOTE_STATE_TERMINATING == mProcessingState ) {
                    dropped = true;
                }
                // Otherwise we can enqueue the event now
                else {
                    // If the biote's state is idle then we can put it in the ready queue.
                    // If it is not idle it means the biote is currently processing an event,
                    // and so cannot be put in the ready queue
                    mRequest.add(msg);
                    if (mProcessingState == BIOTE_STATE_IDLE) {
                        mProcessingState = BIOTE_STATE_QUEUED;
                        schedule = true;
                    }
                }
            }

            // If the event is to be scheduled, do so now; if it was dropped, log it
            if( schedule )
                bioteManager.__protected_friend_Biote__scheduleBiote(this, mUseBlockingQueue);
            else if( dropped )
                log(true, "Event '" + msg.getEventName() + "' was dropped because this biote is terminating...");
        }
        @Override
        public void finalize() throws Throwable {
            try {
                mEventRouting.clear();
                mRequest.clear();
                mEventSubscriptions.clear();
            }
            finally {
                super.finalize();
            }
        }
};
