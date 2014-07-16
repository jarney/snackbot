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
class CommandSetPIDQPPSConstants extends CommandResponseNone {
    private final int mMotorId;
    private final int mP;
    private final int mI;
    private final int mD;
    private final int mQPPS;

    protected CommandSetPIDQPPSConstants(
            final int aMotorId,
            final int aP,
            final int aI,
            final int aD,
            final int aQPPS) {
        mMotorId = aMotorId;
        mP = aP;
        mI = aI;
        mD = aD;
        mQPPS = aQPPS;
    }
    
    @Override
    protected byte[] getCommand(byte aAddress) {
        byte[] b = new byte[19];
        int offset = 0;
        offset = setByte(b, offset, aAddress);
        byte command = (byte) ((mMotorId == 0) ? 28 : 29);
        offset = setByte(b, offset, command);
        offset = setLong(b, offset, mD);
        offset = setLong(b, offset, mP);
        offset = setLong(b, offset, mI);
        offset = setLong(b, offset, mQPPS);
        setChecksum(b, offset);

        return b;
    }
    
}
