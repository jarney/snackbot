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
class CommandDriveMotorWithSignedDutyAccel extends CommandResponseNone {
    private final int mMotorId;
    private final double mDutyCycle;
    private final long mAccel;

    protected CommandDriveMotorWithSignedDutyAccel(
            final int aMotorId,
            final double aDutyCycle,
            final long aAccel) {
        mMotorId = aMotorId;
        mDutyCycle = aDutyCycle;
        mAccel = aAccel;
    }

    @Override
    protected byte[] getCommand(byte aAddress) {
        byte[] b = new byte[7];
        
        b[0] = aAddress;
        b[1] = (byte) ((mMotorId == 0) ? 52: 53);
        
        long dutyCycle;
        
        dutyCycle = (long) (mDutyCycle * 1500);
        
        if (dutyCycle < -1500) {
            dutyCycle = 1500;
        }
        else if (dutyCycle > 1500) {
            dutyCycle = 1500;
        }
        
        setShort(b, 2, (int)dutyCycle);
        setShort(b, 4, (int)mAccel);
        
        b[6] = calculateChecksum(b);
        
        return b;
    }
    
}
