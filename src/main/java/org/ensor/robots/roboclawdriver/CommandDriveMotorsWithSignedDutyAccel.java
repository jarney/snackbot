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
class CommandDriveMotorsWithSignedDutyAccel extends CommandResponseNone {
    private final double mDutyCycle1;
    private final long mAccel1;
    private final double mDutyCycle2;
    private final long mAccel2;

    protected CommandDriveMotorsWithSignedDutyAccel(
            final double aDutyCycle1,
            final long aAccel1,
            final double aDutyCycle2,
            final long aAccel2) {
        mDutyCycle1 = aDutyCycle1;
        mAccel1 = aAccel1;
        mDutyCycle2 = aDutyCycle2;
        mAccel2 = aAccel2;
    }

    private long calcDutyCycle(double d) {
        long dutyCycle;
        dutyCycle = (long) (mDutyCycle1 * 1500);
        
        if (dutyCycle < -1500) {
            dutyCycle = 1500;
        }
        else if (dutyCycle > 1500) {
            dutyCycle = 1500;
        }
        return dutyCycle;
    }
    
    @Override
    protected byte[] getCommand(byte aAddress) {
        byte[] b = new byte[11];
        
        b[0] = aAddress;
        b[1] = (byte) (54);
        
        long dutyCycle1 = calcDutyCycle(mDutyCycle1);
        long dutyCycle2 = calcDutyCycle(mDutyCycle2);
        
        setShort(b, 2, (int)dutyCycle1);
        setShort(b, 4, (int)mAccel1);
        setShort(b, 6, (int)dutyCycle1);
        setShort(b, 8, (int)mAccel2);
        
        b[10] = calculateChecksum(b);
        
        return b;
    }
    
}
