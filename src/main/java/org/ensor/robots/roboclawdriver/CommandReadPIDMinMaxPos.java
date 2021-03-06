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
class CommandReadPIDMinMaxPos extends CommandResponseFixed {

    private final RoboClawMotor mMotor;
    
    protected CommandReadPIDMinMaxPos(RoboClawMotor aMotor) {
        mMotor = aMotor;
    }

    @Override
    protected byte[] getCommand(byte aAddress) {
        byte []b = new byte[2];
        b[0] = aAddress;
        b[1] = (byte) ((mMotor.getMotorId() == 0) ? 63 : 64);
        
        return b;
    }
    
    @Override
    protected int getResponseLength() { return 21; }

    @Override
    protected void onResponse(byte[] aResponseBuffer, int aResponseLength) {
        int p = aResponseBuffer[0] << 24 | 
                aResponseBuffer[1] << 16 |
                aResponseBuffer[2] << 8 | 
                aResponseBuffer[3];
        
        int i = aResponseBuffer[4] << 24 | 
                aResponseBuffer[5] << 16 |
                aResponseBuffer[6] << 8 | 
                aResponseBuffer[7];
        
        int d = aResponseBuffer[8] << 24 | 
                aResponseBuffer[9] << 16 |
                aResponseBuffer[10] << 8 | 
                aResponseBuffer[11];
        
        int minPos = aResponseBuffer[12] << 24 | 
                aResponseBuffer[13] << 16 |
                aResponseBuffer[14] << 8 | 
                aResponseBuffer[15];
        
        int maxPos = aResponseBuffer[16] << 24 | 
                aResponseBuffer[17] << 16 |
                aResponseBuffer[18] << 8 | 
                aResponseBuffer[19];
        
        mMotor.setPIDConstantsInternal(p, i, d);
        mMotor.setMinMaxPosInternal(minPos, maxPos);
        
    }
    
}
