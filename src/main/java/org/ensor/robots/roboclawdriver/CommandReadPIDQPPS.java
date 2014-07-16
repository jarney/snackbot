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
class CommandReadPIDQPPS extends CommandResponseFixed {

    private final RoboClawMotor mMotor;
    
    protected CommandReadPIDQPPS(RoboClawMotor aMotor) {
        mMotor = aMotor;
    }

    @Override
    protected byte[] getCommand(byte aAddress) {
        byte []b = new byte[2];
        b[0] = aAddress;
        b[1] = (byte) ((mMotor.getMotorId() == 0) ? 55 : 56);
        
        return b;
    }
    
    @Override
    protected int getResponseLength() { return 17; }

    @Override
    protected void onResponse(byte[] aResponseBuffer, int aResponseLength) {
        int p = (byteAsInt(aResponseBuffer[0]) << 24) |
                (byteAsInt(aResponseBuffer[1]) << 16) |
                (byteAsInt(aResponseBuffer[2]) << 8) |
                byteAsInt(aResponseBuffer[3]);
        
        int i = (byteAsInt(aResponseBuffer[4]) << 24) |
                (byteAsInt(aResponseBuffer[5]) << 16) |
                (byteAsInt(aResponseBuffer[6]) << 8) |
                byteAsInt(aResponseBuffer[7]);
        
        int d = (byteAsInt(aResponseBuffer[8]) << 24) |
                (byteAsInt(aResponseBuffer[9]) << 16) |
                (byteAsInt(aResponseBuffer[10]) << 8) |
                byteAsInt(aResponseBuffer[11]);
        
        int qpps = (byteAsInt(aResponseBuffer[12]) << 24) |
                (byteAsInt(aResponseBuffer[13]) << 16) |
                (byteAsInt(aResponseBuffer[14]) << 8) |
                byteAsInt(aResponseBuffer[15]);
        
        mMotor.setPIDConstantsInternal(p, i, d);
        mMotor.setQPPSInternal(qpps);
    }
    
}
