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
package org.ensor.robots.roboclawdriver;

/**
 *
 * @author jona
 */
class CommandReadMotorCurrents extends CommandResponseFixed {

    RoboClawMotor mMotor1;
    RoboClawMotor mMotor2;
    
    protected CommandReadMotorCurrents(RoboClawMotor m1, RoboClawMotor m2) {
        mMotor1 = m1;
        mMotor2 = m2;
    }

    @Override
    protected int getResponseLength() { return 5;}
    
    @Override
    protected byte[] getCommand(byte aAddress) {
        byte[] b = new byte[2];
        
        b[0] = (byte)0x80;
        b[1] = 49;
        
        return b;
    }

    @Override
    protected void onResponse(byte[] aResponseBuffer, int aResponseLength) {
            int currentMotor1 = (aResponseBuffer[0] << 8) | (aResponseBuffer[1]);
            int currentMotor2 = (aResponseBuffer[2] << 8) | (aResponseBuffer[3]);
            
            double current1 = (double)currentMotor1 * 10.0 / 1000.0;
            double current2 = (double)currentMotor2 * 10.0 / 1000.0;
            
            mMotor1.setCurrentDraw(current1);
            mMotor2.setCurrentDraw(current2);
        
    }
    
}
