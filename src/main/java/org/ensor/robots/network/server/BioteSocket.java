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
import org.eclipse.jetty.websocket.WebSocket;
import org.ensor.data.atom.json.JSONStringSerializer;
import org.ensor.robots.scheduler.Biote;
import org.ensor.robots.scheduler.BioteManager;
import org.ensor.robots.scheduler.Event;

/**
 *
 * @author jona
 */
public class BioteSocket implements
        WebSocket.OnTextMessage {

    private Connection mConnection;
    private BioteManager mBioteManager;
    private Biote mBiote;
    
    public BioteSocket(BioteManager aBioteManager) {
        mBiote = null;
        mBioteManager = aBioteManager;
    }
    
    @Override
    public void onClose(int closeCode, String message) {
        if (mBiote != null) {
            mBiote.shutdown(false);
        }
    }

    public void sendMessage(Event aEvent) throws Exception {
        String jsonData = JSONStringSerializer.instance().serializeTo(
                aEvent.serialize());
        mConnection.sendMessage(jsonData);
    }

    @Override
    public void onMessage(String data) {
        try {
            Event e = new Event("Net-In",
                    JSONStringSerializer.instance().serializeFrom(data));
            mBiote.stimulate(e);
        } catch (Exception ex) {
            Logger.getLogger(BioteSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isOpen() {
        return mConnection.isOpen();
    }

    @Override
    public void onOpen(Connection connection) {
        try {
            mBiote = new NetworkBiote(mBioteManager, this);
            mConnection = connection;
            mBioteManager.createBiote(mBiote);
            connection.sendMessage("Server received Web Socket upgrade and added it to Receiver List.");
        } catch (Exception ex) {
            Logger.getLogger(BioteSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
