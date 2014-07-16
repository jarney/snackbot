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

package org.ensor.robots.network.client;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketClient;
import org.eclipse.jetty.websocket.WebSocketClientFactory;
import org.json.JSONObject;

/**
 *
 * @author jona
 */
public class UpgradeClient implements WebSocket.OnBinaryMessage {
    
    private final String mHostname;
    private final int mPort;
    private final WebSocketClient mChannelManager;
    private WebSocket.Connection mConnection;
    
    UpgradeClient(final String aHostname, final int aPort) throws Exception {
        mHostname = aHostname;
        mPort = aPort;
        System.out.println("New factory");
        WebSocketClientFactory fact = new WebSocketClientFactory();
        System.out.println("starting factory");
        fact.start();
        System.out.println("New socket client");
        mChannelManager = fact.newWebSocketClient();
    }
    
    public void connect() throws Exception {
        System.out.println("open");
        Future<WebSocket.Connection> fc = mChannelManager.open(
                new URI(
                    "ws://" + mHostname + ":" + mPort + "/v1/upgrade/"),
                this);
        mConnection = fc.get(10, TimeUnit.SECONDS);
        System.out.println("open done");
    }

    public boolean isConnected() {
        return mConnection != null;
    }
    
    public void uploadFile(File aFile) throws Exception {
        
        long fileLength = aFile.length();
        long fileRead = 0;
                
        JSONObject header = new JSONObject();
        header.put("length", fileLength);
        
        FileInputStream fis = new FileInputStream(aFile);
        
        byte[] buffer = new byte[1024];
        
        while (fileRead < fileLength) {
            int bytesRead = fis.read(buffer);
            if (bytesRead > 0) {
                mConnection.sendMessage(buffer, 0, bytesRead);
                fileRead += bytesRead;
            }
            System.out.println("at " + fileRead + "/" + fileLength);
        }
        System.out.println("done writing");
        
    }
    
    public void onMessage(byte[] arg0, int arg1, int arg2) {
    }

    public void onOpen(Connection cnctn) {
        try {
            System.out.println("client.onOpen");
            mConnection = cnctn;
        } catch (Exception ex) {
            Logger.getLogger(UpgradeClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void onClose(int i, String string) {
    }
    
    public void disconnect() {
        if (mConnection != null) {
            mConnection.close();
        }
    }
    
    
    public static void main(String [] args) {
        try {
            
            if (args.length < 2) {
                System.out.println("Usage: UpgradeClient hostname port");
                System.exit(1);
            }

            File fileToUpload = new File("target/snackbot-1.0-SNAPSHOT-bin.zip");
            
            if (!fileToUpload.exists()) {
                System.out.println("File " + fileToUpload.getAbsolutePath() + " does not exist");
                System.exit(1);
            }
            
            String hostname = args[0];
            short port = 8080;
            if (args.length >= 2) {
                port = Short.parseShort(args[1]);
            }
            System.out.println("Upgrading server at " + hostname + ":" + port);
            UpgradeClient uc = new UpgradeClient(hostname, port);
            uc.connect();
            if (uc.isConnected()) {
                uc.uploadFile(fileToUpload);
            }
            uc.disconnect();
            System.exit(0);
        } catch (Exception ex) {
            Logger.getLogger(UpgradeClient.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
    
}
