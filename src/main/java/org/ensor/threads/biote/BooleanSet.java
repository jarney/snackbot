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

import java.util.HashMap;

/**
 * When systems are based on asynchronous message passing as this one is,
 * occasionally, one must wait for a series of things to happen before the
 * next step in a process can proceed.  The BooleanSet class provides a
 * mechanism for doing this.  A boolean set is an object that waits for
 * all added items to be completed and once they are, sends an event
 * indicating that everything that it was waiting for is now ready.
 * @author Jon
 */
public class BooleanSet {
    private HashMap<String, Boolean>        mSet;
    private Event                       mMessage;
    private IEventHandler                    mHandler;
    private boolean                         mSilentLogs;

    /**
     * The constructor creates a new boolean set which, when all items
     * have been completed, calls the event handler with the given event.
     * @param biote
     * @param msg
     */
    public BooleanSet(Event msg, IEventHandler handler) {
        mHandler = handler;
        mMessage = msg;
        mSet = new HashMap<String,Boolean>();
        mSilentLogs = false;
    }
    public BooleanSet(Event msg, IEventHandler handler, boolean silenceLogs) {
        mSilentLogs = silenceLogs;
        mHandler = handler;
        mMessage = msg;
        mSet = new HashMap<String, Boolean>();
    }
    public BooleanSet(String msg, IEventHandler handler) {
        mHandler = handler;
        mMessage = new Event(msg);
        mSet = new HashMap<String, Boolean>();
        mSilentLogs = false;
    }
    /**
     * Adds the given itemName to the list of things that this boolean set is waiting for.
     * @param itemName
     */
    public void addItem(String itemName) {
        mSet.put(itemName, false);
    }
    public boolean isComplete() {
        return mSet.isEmpty();
    }
    /**
     * Indicates to the boolean set that the given item is now complete.  If
     * that item was the last incomplete item in the boolean set, it raises
     * the event for the boolean set on this biote.
     * @param itemName
     * @throws Exception
     */
    public void completeItem(String itemName) throws Exception {
        mSet.remove(itemName);
        if (mSet.isEmpty()) {
            mHandler.process(mMessage);
        }
    }
}
