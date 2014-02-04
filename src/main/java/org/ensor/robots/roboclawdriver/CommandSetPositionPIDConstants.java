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
            int aMotorId,
            int aP,
            int aI,
            int aD,
            int aMaxI,
            int aDeadzone,
            int aMinPos,
            int aMaxPos) {
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
    protected byte[] getCommand(byte aAddress) {
        byte[] b = new byte[31];
        b[0] = aAddress;
        b[1] = (byte) ((mMotorId == 0) ? 61 : 62);
        
        setLong(b, 2, mP);
        setLong(b, 6, mI);
        setLong(b, 10, mD);
        setLong(b, 14, mMaxI);
        setLong(b, 18, mDeadZone);
        setLong(b, 22, mMinPos);
        setLong(b, 26, mMaxPos);
        
        b[30] = calculateChecksum(b);
        
        return b;
    }
    
}