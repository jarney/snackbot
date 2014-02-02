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
class CommandDriveMotorsWithSignedSpeedAndAccel extends CommandResponseNone {
    private final long mPulsesPerSecondPerSecond;
    private final long mPulsesPerSecond1;
    private final long mPulsesPerSecond2;

    protected CommandDriveMotorsWithSignedSpeedAndAccel(
            long aPulsesPerSecondPerSecond,
            long aPulsesPerSecond1,
            long aPulsesPerSecond2) {
        mPulsesPerSecondPerSecond = aPulsesPerSecondPerSecond;
        mPulsesPerSecond1 = aPulsesPerSecond1;
        mPulsesPerSecond2 = aPulsesPerSecond2;
    }

    @Override
    protected byte[] getCommand(byte aAddress) {
        byte[] b = new byte[15];
        
        b[0] = aAddress;
        b[1] = (byte) (40);
        
        setLong(b, 2, mPulsesPerSecondPerSecond);
        setLong(b, 6, mPulsesPerSecond1);
        setLong(b, 10, mPulsesPerSecond2);
        
        b[14] = calculateChecksum(b);
        
        return b;
    }
    
}
