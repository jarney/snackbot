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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.WebSocket;
import org.ensor.robots.os.IModuleManager;

/**
 *
 * @author jona
 */
public class UpgradeReceiverSocket implements WebSocket.OnBinaryMessage {
    private final IModuleManager mManager;

    UpgradeReceiverSocket(IModuleManager aManager) {
        mManager = aManager;
    }
    
    private Connection mConnection;
    private FileOutputStream mOutputStream;
    
    public void onMessage(byte[] readBuffer, int offset, int length) {
        try {
            System.out.println("UpgradeServerSocket.onMessage");
            mOutputStream.write(readBuffer, offset, length);
        } catch (IOException ex) {
            Logger.getLogger(UpgradeReceiverSocket.class.getName()).log(Level.SEVERE, null, ex);
            mConnection.close();
        }
    }

    public void onOpen(Connection cnctn) {
        System.out.println("UpgradeServerSocket.onOpen");
        mConnection = cnctn;
        try {
            mOutputStream = new FileOutputStream(new File("server-update.zip"));
        } catch (FileNotFoundException ex) {
            cnctn.close();
        }
    }

    class ShutdownRunnable implements Runnable {

        public void run() {
            try {
                mManager.shutdownAll();
            }
            catch (Exception ex) {
                throw new RuntimeException("Shutting down server", ex);
            }
        }
        
    }
    
    
    public void onClose(int i, String string) {
        try {
            System.out.println("closing");
            mOutputStream.close();
            
            Thread shutdownThread = new Thread(new ShutdownRunnable());
            shutdownThread.start();
            
        } catch (Exception ex) {
            Logger.getLogger(UpgradeReceiverSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
