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
        b[0] = aAddress;
        b[1] = (byte) ((mMotorId == 0) ? 28 : 29);
        
        b[2] = (byte) ((mP >> 24) & 0xff);
        b[3] = (byte) ((mP >> 16) & 0xff);
        b[4] = (byte) ((mP >> 8) & 0xff);
        b[5] = (byte) ((mP) & 0xff);

        b[6] = (byte) ((mI >> 24) & 0xff);
        b[7] = (byte) ((mI >> 16) & 0xff);
        b[8] = (byte) ((mI >> 8) & 0xff);
        b[9] = (byte) ((mI) & 0xff);
        
        b[10] = (byte) ((mD >> 24) & 0xff);
        b[11] = (byte) ((mD >> 16) & 0xff);
        b[12] = (byte) ((mD >> 8) & 0xff);
        b[13] = (byte) ((mD) & 0xff);

        b[14] = (byte) ((mQPPS >> 24) & 0xff);
        b[15] = (byte) ((mQPPS >> 16) & 0xff);
        b[16] = (byte) ((mQPPS >> 8) & 0xff);
        b[17] = (byte) ((mQPPS) & 0xff);
        
        b[18] = calculateChecksum(b);
        
        return b;
    }
    
}
