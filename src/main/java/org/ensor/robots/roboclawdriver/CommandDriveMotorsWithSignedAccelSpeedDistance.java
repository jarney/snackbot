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
class CommandDriveMotorsWithSignedAccelSpeedDistance 
        extends CommandResponseNone {
    private final int mMotorId;
    private final long mPulsesPerSecondPerSecond;
    private final long mPulsesPerSecond1;
    private final long mPulsesDistance1;
    private final long mPulsesPerSecond2;
    private final long mPulsesDistance2;
    private final boolean mStopOtherCommands;

    protected CommandDriveMotorsWithSignedAccelSpeedDistance(int aMotorId, 
            long aPulsesPerSecondPerSecond,
            long aPulsesPerSecond1,
            long aPulsesDistance1,
            long aPulsesPerSecond2,
            long aPulsesDistance2,
            boolean aStopOtherCommands) {
        mMotorId = aMotorId;
        mPulsesPerSecondPerSecond = aPulsesPerSecondPerSecond;
        mPulsesPerSecond1 = aPulsesPerSecond1;
        mPulsesDistance1 = aPulsesDistance1;
        mPulsesPerSecond2 = aPulsesPerSecond2;
        mPulsesDistance2 = aPulsesDistance2;
        mStopOtherCommands = aStopOtherCommands;
    }
    

    @Override
    protected byte[] getCommand(byte aAddress) {
        byte[] b = new byte[24];
        
        b[0] = aAddress;
        b[1] = (byte) ((mMotorId == 0) ? 44 : 45);
        
        setLong(b, 2, mPulsesPerSecondPerSecond);
        setLong(b, 6, mPulsesPerSecond1);
        setLong(b, 10, mPulsesDistance1);
        setLong(b, 14, mPulsesPerSecond2);
        setLong(b, 18, mPulsesDistance2);
        
        b[22] = (byte)(mStopOtherCommands ? 1 : 0);
        
        b[23] = calculateChecksum(b);
        
        return b;
    }
    
}
