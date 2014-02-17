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
class CommandDriveMotorWithSignedSpeedAndDistance extends CommandResponseNone {
    private final int mMotorId;
    private final long mPulsesPerSecond;
    private final long mPulsesDistance;
    private final boolean mStopOtherCommands;

    protected CommandDriveMotorWithSignedSpeedAndDistance(
            final int aMotorId,
            final long aPulsesPerSecond,
            final long aPulsesDistance,
            final boolean aStopOtherCommands) {
        mMotorId = aMotorId;
        mPulsesPerSecond = aPulsesPerSecond;
        mPulsesDistance = aPulsesDistance;
        mStopOtherCommands = aStopOtherCommands;
    }
    

    @Override
    protected byte[] getCommand(byte aAddress) {
        byte[] b = new byte[12];
        
        int offset = 0;
        offset = setByte(b, offset, aAddress);
        byte command = (byte) ((mMotorId == 0) ? 41 : 42);
        offset = setByte(b, offset, command);
        offset = setLong(b, offset, mPulsesPerSecond);
        offset = setLong(b, offset, mPulsesDistance);
        offset = setByte(b, offset, (byte)(mStopOtherCommands ? 1 : 0));
        setChecksum(b, offset);
        
        return b;
    }
    
}
