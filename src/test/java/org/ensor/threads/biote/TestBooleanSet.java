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

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author jona
 */
public class TestBooleanSet {
    @Test
    public void testBooleanSet() throws Exception {
        
        final boolean[] finished = new boolean[1];
        finished[0] = false;
        
        // Create a handler for when all elements are completed.
        IEventHandler eventHandler = new IEventHandler() {
            public void process(Event msg) throws Exception {
                finished[0] = true;
            }
        };
        
        BooleanSet bs = new BooleanSet("my-event", eventHandler);
        bs.addItem("one");
        bs.addItem("two");
        bs.addItem("three");
        
        Assert.assertFalse(finished[0]);
        bs.completeItem("one");
        Assert.assertFalse(finished[0]);
        bs.completeItem("two");
        Assert.assertFalse(finished[0]);
        bs.completeItem("three");
        Assert.assertTrue(finished[0]);
        
        finished[0] = false;
        Event myEvent = new Event("my-event");
        
        BooleanSet bs2 = new BooleanSet(myEvent, eventHandler);
        bs2.addItem("one");
        bs2.addItem("two");
        bs2.addItem("three");
        
        Assert.assertFalse(finished[0]);
        bs2.completeItem("one");
        Assert.assertFalse(finished[0]);
        bs2.completeItem("two");
        Assert.assertFalse(finished[0]);
        bs2.completeItem("three");
        Assert.assertTrue(finished[0]);
        
        
    }
}
