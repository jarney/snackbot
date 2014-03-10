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
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.ensor.robots.os.IModule;
import org.ensor.robots.os.IModuleManager;
import org.ensor.threads.biote.BioteManager;

/**
 *
 * @author jona
 */
public class Module implements IModule {
    private Server mServer;
    private final org.ensor.threads.biote.Module mBioteModule;
    private static final int ROBOT_DEFAULT_HTTP_PORT = 8080;

    public Module(
            final org.ensor.threads.biote.Module aBioteModule
    ) {
        mBioteModule = aBioteModule;
    }
    
    
    public Class[] getDependencies() {
        Class [] deps = {
            org.ensor.threads.biote.Module.class
        };
        return deps;
    }

    public void start(IModuleManager aManager) {
        Thread networkThread = new Thread(new StartupRunnable(aManager));
        networkThread.start();
    }

    public void shutdown(IModuleManager aManager) {
            Thread shutdownThread = new Thread(new ShutdownRunnable());
            shutdownThread.start();
    }
    
    class StartupRunnable implements Runnable {
        
        IModuleManager mManager;
        
        StartupRunnable(IModuleManager aManager) {
            mManager = aManager;
        }
        
        public void run() {
            try {
                runIt();
            } catch (Exception ex) {
                Logger.getLogger(Module.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void runIt() throws Exception {
            mServer = new Server(ROBOT_DEFAULT_HTTP_PORT);
            mServer.setGracefulShutdown(2000);

            BioteManager bioteManager = mBioteModule.getBioteManager();

            ServletContextHandler servletHandler =
                    new ServletContextHandler(
                            ServletContextHandler.NO_SESSIONS);
            servletHandler.setContextPath("/");

            BioteSocketServlet bss = new BioteSocketServlet(bioteManager);
            servletHandler.addServlet(new ServletHolder(bss), "/v1/biote/*");

            UpgradeReceiverSocketServlet uss =
                    new UpgradeReceiverSocketServlet(mManager);
            servletHandler.addServlet(new ServletHolder(uss), "/v1/upgrade/*");

            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setResourceBase("target/site");
            resourceHandler.setWelcomeFiles(new String[] {"index.html"});
            ContextHandler siteHandler = new ContextHandler("/site");
            siteHandler.setHandler(resourceHandler);

            HandlerList handlers = new HandlerList();
            handlers.addHandler(servletHandler);
            handlers.addHandler(siteHandler);

            mServer.setHandler(handlers);
            System.out.println("Start");
            mServer.start();
            System.out.println("join");
            mServer.join();
            System.out.println("server exit");
        }

    }
    
    class ShutdownRunnable implements Runnable {

        public void run() {
            try {
                mServer.stop();
            }
            catch (Exception ex) {
                throw new RuntimeException("Shutting down server", ex);
            }
        }
        
    }

    
    
}
