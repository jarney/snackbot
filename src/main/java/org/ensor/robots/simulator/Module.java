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

package org.ensor.robots.simulator;

import org.ensor.robots.os.api.ComponentManager;
import org.ensor.robots.os.IModule;
import org.ensor.robots.os.IModuleManager;

/**
 * This class is a robot simulator which registers
 * a pair of speed-servo motors for the purpose of locomotion
 * and a pair of encoders for the purpose of sensing motor movement.
 * @author jona
 */
public class Module implements IModule {

    public Class[] getDependencies() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void start(IModuleManager aManager) throws Exception {
        
        SimulatedMotor mLeftMotor = new SimulatedMotor(Math.PI / 180, 2000);
        SimulatedMotor mRightMotor = new SimulatedMotor(Math.PI / 180, 2000);
        
        ComponentManager.
                registerComponent(
                        "simulator-speed-left",
                        mLeftMotor);
    }

    public void shutdown(IModuleManager aManager) throws Exception {

    }
    
}
