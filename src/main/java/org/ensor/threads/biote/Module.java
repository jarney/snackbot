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

import java.util.Properties;
import org.ensor.robots.os.IModule;
import org.ensor.robots.os.IModuleManager;

/**
 *
 * @author jona
 */
public class Module implements IModule {

    private BioteManager mBioteManager;
    
    public Module() {
        mBioteManager = null;
    }
    
    public Class[] getDependencies() {
        Class [] deps = {
            org.ensor.robots.logging.Module.class
        };
        return deps;
    }

    public void start(IModuleManager aManager) {
        mBioteManager = new BioteManager("rootInstance");
    }

    public void shutdown(IModuleManager aManager) {
        mBioteManager.shutdown();
    }
    
    public BioteManager getBioteManager() {
        return mBioteManager;
    }

}