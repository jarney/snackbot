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

package org.ensor.threads.biote;

import org.ensor.data.atom.Atom;
import org.ensor.data.atom.DictionaryAtom;
import org.ensor.data.atom.ListAtom;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jona
 */
public class TestBioteManager {
    
    /**
     * This method creates a Biote manager and schedules a Biote
     * to execute a timer and shut down the Biote manager when the
     * timer has iterated 10 times.
     *
     * @throws Exception 
     */
    @Test
    public void testBioteManager() throws Exception {
        final BioteManager bioteManager = new BioteManager("one");
        
        Biote newBiote = new Biote(bioteManager, false) {

            int nIterations = 10;
            int mTimer;
            
            @Override
            protected void onInit(final Event message) throws Exception {
                this.subscribe("on-timer", new IEventHandler() {
                    public void process(final Event msg) throws Exception {
                        nIterations--;
                        if (nIterations == 0) {
                            bioteManager.shutdown();
                        }
                    }
                });
                
                Event onTimer = new Event("on-timer");
                mTimer = startTimer(100, onTimer, true);
            }
            

            @Override
            protected void onFinalize(Event message) throws Exception {
                cancelTimer(mTimer);
            }
        };
        
        bioteManager.createBiote(newBiote);
        
        bioteManager.waitForShutdown();
        // Dump activity on the threads.
        bioteManager.checkThreadActivity();
        Assert.assertFalse(bioteManager.isRunning());
        
    }
    
    @Test
    public void testCloneNotSupported() {
        BioteManager bm = new BioteManager("one");
        boolean threw = false;
        try {
            bm.clone();
        }
        catch (CloneNotSupportedException ex) {
            threw = true;
        }
        Assert.assertTrue(threw);
    }
    
    class BioteTwo extends Biote {
            int mTimer;
            int mBioteOneId;

        public BioteTwo(BioteManager aBioteManager, int aBioteTwoId) {
            super(aBioteManager, false);
            mBioteOneId = aBioteTwoId;
        }
            
        @Override
        protected void onInit(final Event message) throws Exception {
            Assert.assertEquals(2, getBioteManager().getBioteCount());
            
            this.subscribe("on-timer", new IEventHandler() {
                public void process(final Event msg) throws Exception {
                    onTimer(msg);
                }
            });
            this.subscribe("event-from-one", new IEventHandler() {
                public void process(Event msg) throws Exception {
                    onEventFromOne(msg);
                }
            });
            Event onTimer = new Event("on-timer");
            mTimer = startTimer(100, onTimer, true);
        }
        private void onEventFromOne(Event msg) {
            getBioteManager().shutdown();
        }

        private void onTimer(final Event message) {
            DictionaryAtom dict = DictionaryAtom.newAtom();
            dict.setInt("biote-one-id", this.getBioteId());
            Event eventFromOne = new Event("event-from-one", dict);
            sendStimulus(mBioteOneId, eventFromOne);
            
            // While we're at it, let's try starting a timer for a biote
            // which doesn't exist to see how it gets handled.
            getBioteManager().startTimer(99, 10, message, false);
        }


        @Override
        protected void onFinalize(Event message) throws Exception {
            cancelTimer(mTimer);
        }
    };
    class BioteOne extends Biote {
        
        int mTimer;
        int mBioteTwoId;

        public BioteOne(BioteManager aBioteManager) {
            super(aBioteManager, false);
        }
        @Override
        protected void onInit(final Event message) throws Exception {
            this.subscribe("event-from-one", new IEventHandler() {
                public void process(Event msg) throws Exception {
                    onEventFromOne(msg);
                }
            });
        }
        private void onEventFromOne(Event msg) {
            mBioteTwoId = msg.getData().getInt("biote-one-id");
            sendStimulus(mBioteTwoId, msg);
            shutdown(true);
        }


        @Override
        protected void onFinalize(Event message) throws Exception {
            cancelTimer(mTimer);
        }
    };

    
    /**
     * This method creates 2 Biotes.  Second Biote sends a message
     * to the first.  First Biote sends the same message back and shuts down.
     * Second shuts down Biote manager.
     * @throws Exception 
     */
    @Test
    public void testBioteBioteEvents() throws Exception {
        final BioteManager bioteManager = new BioteManager("one");
        
        BioteOne bioteOne = new BioteOne(bioteManager);
        
        int bioteOneId = bioteManager.createBiote(bioteOne);
        
        BioteTwo bioteTwo = new BioteTwo(bioteManager, bioteOneId);
        
        int bioteTwoId = bioteManager.createBiote(bioteTwo);
        
        bioteManager.waitForShutdown();
        // Dump activity on the threads.
        bioteManager.checkThreadActivity();
        Assert.assertFalse(bioteManager.isRunning());
        
        ListAtom listStats = bioteManager.flushStats();
        bioteManager.logString(true, 0, "Stats:", listStats);
        
        DictionaryAtom stimulate = null;
        DictionaryAtom startTimer = null;
        DictionaryAtom sendStimulus = null;
        
        for (Atom a : listStats) {
            DictionaryAtom d = (DictionaryAtom) a;
            if (d.getString("name").equals("Biote.java:stimulate")) {
                stimulate = d;
            }
            if (d.getString("name").equals("BioteManager.java:startTimer")) {
                startTimer = d;
            }
            if (d.getString("name").equals("BioteManager.java:sendStimulus")) {
                sendStimulus = d;
            }
        }

        Assert.assertNotNull(stimulate);
        Assert.assertNotNull(startTimer);
        Assert.assertNotNull(sendStimulus);
        
        
    }
    
}
