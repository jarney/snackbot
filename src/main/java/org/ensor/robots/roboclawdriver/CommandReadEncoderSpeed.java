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
class CommandReadEncoderSpeed extends CommandResponseFixed {
    private final RoboClawMotor mMotor;

    protected CommandReadEncoderSpeed(
            final RoboClawMotor aMotor) {
        mMotor = aMotor;
    }
    
    @Override
    protected byte[] getCommand(final byte aAddress) {
        byte[] b = new byte[2];
        
        byte cmd = (byte) ((mMotor.getMotorId() == 0) ? 30 : 31);
        
        int offset = 0;
        offset = setByte(b, offset, aAddress);
        setByte(b, offset, cmd);
        return b;
    }

    @Override
    protected void onResponse(
            final byte[] aResponseBuffer,
            final int aResponseLength) {
        
        long speed = aResponseBuffer[0] << 24 | 
                aResponseBuffer[1] << 16 | 
                aResponseBuffer[2] << 8 | 
                aResponseBuffer[3];
        
        long s = speed * 125;
        if (aResponseBuffer[4] != 0) {
            s = -s;
        }
        
        mMotor.setEncoderSpeed(s);
    }

    @Override
    protected int getResponseLength() {
        return 6;
    }
    
}
