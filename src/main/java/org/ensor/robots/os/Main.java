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

package org.ensor.robots.os;

/**
 *
 * @author jona
 */
public class Main {

    public static void main(String []args) throws Exception {
        ModuleManager moduleManager = new ModuleManager();
        
        org.ensor.robots.roboclawdriver.Module roboClawModule =
                new org.ensor.robots.roboclawdriver.Module();
        moduleManager.register(roboClawModule);
        
        org.ensor.threads.biote.Module bioteModule =
                new org.ensor.threads.biote.Module();
        moduleManager.register(bioteModule);

        org.ensor.robots.pathfollower.Module pathModule =
                new org.ensor.robots.pathfollower.Module(bioteModule);
        moduleManager.register(pathModule);
        
        org.ensor.robots.network.server.Module networkModule =
            new org.ensor.robots.network.server.Module(bioteModule);
        moduleManager.register(networkModule);
        
        moduleManager.startAll();
    }
    
}
