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

import org.ensor.data.atom.DictionaryAtom;
import org.ensor.data.atom.ImmutableDict;
import org.ensor.threads.biote.Biote;
import org.ensor.threads.biote.BioteManager;
import org.ensor.threads.biote.Event;
import org.ensor.threads.biote.IEventHandler;

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
        this.subscribe("Timer-Expire", new IEventHandler() {
            public void process(Event msg) throws Exception {
                onTimerExpire(msg);
            }
        });
        
    }

    @Override
    protected void onFinalize(Event message) throws Exception {
    }

    private static final int DIFFERENTIAL_DRIVE_BIOTE = 3;
    
    // Handle a specific network event.
    private void onNetIn(Event msg) {
        String name = msg.getData().getString("eventName");
        
        if (name.equals("all-stop")) {
            Event allStop = new  Event("Mover-AllStop");
            sendStimulus(DIFFERENTIAL_DRIVE_BIOTE, allStop);
        }
        else if (name.equals("differentialDrive")) {
            Event forward = new Event("Mover-MoveRequest", msg.getData());
            sendStimulus(DIFFERENTIAL_DRIVE_BIOTE, forward);
        }
        else if (name.equals("driveMotor")) {
            Event forward = new Event("Mover-DriveMotor", msg.getData());
            sendStimulus(DIFFERENTIAL_DRIVE_BIOTE, forward);
        }
        else if (name.equals("subscribe")) {
            DictionaryAtom dict = DictionaryAtom.newAtom();
            dict.setInt("bioteId", getBioteId());
            Event subscribe = new Event("Mover-Subscribe", dict);
            sendStimulus(DIFFERENTIAL_DRIVE_BIOTE, subscribe);
        }
        else if (name.equals("move")) {
            DictionaryAtom dict = DictionaryAtom.newAtom();
            dict.setReal("x", 1);
            dict.setReal("y", 0);
            dict.setReal("theta", Math.PI / 2);
            Event forward = new Event("Mover-SetDestinationPoint", dict);
            sendStimulus(DIFFERENTIAL_DRIVE_BIOTE, forward);
            startTimer(1000, new Event("Timer-Expire"), false);
        }
        else if (name.equals("reset")) {
            DictionaryAtom dict = DictionaryAtom.newAtom();
            dict.setReal("x", 0);
            dict.setReal("y", 0);
            dict.setReal("theta", 0);
            Event forward = new Event("Mover-Reset", dict);
            sendStimulus(DIFFERENTIAL_DRIVE_BIOTE, forward);
        }
        else if (name.equals("updateConfiguration")) {
            ImmutableDict updateConfig = msg.getData().getDictionary("data");
            Event updateConfigEvent = new Event("Mover-UpdateConfig", updateConfig);
            sendStimulus(DIFFERENTIAL_DRIVE_BIOTE, updateConfigEvent);
        }
    }
    
    private void onTimerExpire(Event msg) throws Exception {
        Event allStop = new Event("Mover-AllStop");
        sendStimulus(DIFFERENTIAL_DRIVE_BIOTE, allStop);
    }
    
    // Handle events from other biotes
    // which should be queued to the network system.
    private void onNetOut(Event msg) throws Exception {
        mBioteSocket.sendMessage(msg);
    }
    
}
