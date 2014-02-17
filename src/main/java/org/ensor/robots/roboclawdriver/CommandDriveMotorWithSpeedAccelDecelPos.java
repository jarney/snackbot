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
class CommandDriveMotorWithSpeedAccelDecelPos extends CommandResponseNone {
    private final long mAccel;
    private final long mSpeed;
    private final long mDecel;
    private final long mPos;
    private final boolean mBuffer;
    private final int mMotorId;

    protected CommandDriveMotorWithSpeedAccelDecelPos(
            final int aMotorId,
            final long aAccel,
            final long aSpeed,
            final long aDecel,
            final long aPos,
            final boolean aBuffer) {
        mMotorId = aMotorId;
        mAccel = aAccel;
        mSpeed = aSpeed;
        mDecel = aDecel;
        mPos = aPos;
        mBuffer = aBuffer;
    }

    @Override
    protected byte[] getCommand(final byte aAddress) {
        byte[] b = new byte[20];
        
        byte cmd = (byte) ((mMotorId == 0) ? 65 : 66);

        int offset = 0;
        offset = setByte(b, offset, aAddress);
        offset = setByte(b, offset, cmd);
        offset = setLong(b, offset, mAccel);
        offset = setLong(b, offset, mSpeed);
        offset = setLong(b, offset, mDecel);
        offset = setLong(b, offset, mPos);
        offset = setByte(b, offset, (mBuffer ? 1 : 0));
        setChecksum(b, offset);
                
        return b;
    }
    
}
