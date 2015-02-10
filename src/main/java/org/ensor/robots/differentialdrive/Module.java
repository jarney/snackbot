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

package org.ensor.robots.differentialdrive;

import org.ensor.robots.os.IModule;
import org.ensor.robots.os.IModuleManager;
import org.ensor.threads.biote.BioteManager;

/**
 *
 * @author jona
 */
public class Module implements IModule {
    
    private final org.ensor.threads.biote.Module mBioteModule;
    private final org.ensor.robots.os.configuration.Module mConfigModule;

    
    public Module(
            final org.ensor.threads.biote.Module aBioteModule,
            final org.ensor.robots.os.configuration.Module aConfigModule
    ) {
        mBioteModule = aBioteModule;
        mConfigModule = aConfigModule;
    }                                                                

    public Class[] getDependencies() {
        Class [] deps = {
            org.ensor.robots.os.configuration.Module.class,
            org.ensor.threads.biote.Module.class,
            org.ensor.robots.roboclawdriver.Module.class
        };
        return deps;
    }

    public void start(IModuleManager aManager) throws Exception {
        
        BioteManager mgr = mBioteModule.getBioteManager();
        DifferentialDriveBiote ddBiote = new DifferentialDriveBiote(
                mgr, 
                mConfigModule.getConfiguration()
        );
        mgr.createBiote(ddBiote);
    }

    public void shutdown(IModuleManager aManager) throws Exception {
    }
    

}
