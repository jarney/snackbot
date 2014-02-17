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
class CommandSetPositionPIDConstants extends CommandResponseNone {
    private final int mMotorId;
    private final int mP;
    private final int mI;
    private final int mD;
    private final int mMaxI;
    private final int mDeadZone;
    private final int mMinPos;
    private final int mMaxPos;

    protected CommandSetPositionPIDConstants(
            final int aMotorId,
            final int aP,
            final int aI,
            final int aD,
            final int aMaxI,
            final int aDeadzone,
            final int aMinPos,
            final int aMaxPos) {
        mMotorId = aMotorId;
        mP = aP;
        mI = aI;
        mD = aD;
        mMaxI = aMaxI;
        mDeadZone = aDeadzone;
        mMinPos = aMinPos;
        mMaxPos = aMaxPos;
    }
    
    @Override
    protected byte[] getCommand(final byte aAddress) {
        byte[] b = new byte[31];
        
        int offset = 0;
        byte cmd = (byte) ((mMotorId == 0) ? 61 : 62);
        offset = setByte(b, offset, aAddress);
        offset = setByte(b, offset, cmd);
        offset = setLong(b, offset, mP);
        offset = setLong(b, offset, mI);
        offset = setLong(b, offset, mD);
        offset = setLong(b, offset, mMaxI);
        offset = setLong(b, offset, mDeadZone);
        offset = setLong(b, offset, mMinPos);
        offset = setLong(b, offset, mMaxPos);
        setChecksum(b, offset);
        
        return b;
    }
    
}
