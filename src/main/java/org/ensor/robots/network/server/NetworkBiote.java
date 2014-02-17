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

package org.ensor.robots.network.server;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ensor.robots.scheduler.Biote;
import org.ensor.robots.scheduler.BioteManager;
import org.ensor.robots.scheduler.Event;
import org.ensor.robots.scheduler.IEventHandler;

/**
 *
 * @author jona
 */
public class NetworkBiote extends Biote {

    private final BioteSocket mBioteSocket;
    
    public NetworkBiote(BioteManager
            aBioteManager,
            BioteSocket aBioteSocket
    ) {
        super(aBioteManager, false);
        mBioteSocket = aBioteSocket;
    }
    
    @Override
    protected void onInit(Event message) throws Exception {
        this.subscribe("Net-In", new IEventHandler() {
            public void process(Event msg) throws Exception {
                onNetIn(msg);
            }
        });
        this.subscribe("Net-Out", new IEventHandler() {
            public void process(Event msg) throws Exception {
                onNetOut(msg);
            }
        });
    }

    @Override
    protected void onFinalize(Event message) throws Exception {
    }

    // Handle a specific network event.
    private void onNetIn(Event msg) {
    }
    
    // Handle events from other biotes
    // which should be queued to the network system.
    private void onNetOut(Event msg) throws Exception {
        mBioteSocket.sendMessage(msg);
    }
    
}
