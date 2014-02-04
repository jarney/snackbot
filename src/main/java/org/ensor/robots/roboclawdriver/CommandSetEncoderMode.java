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
class CommandSetEncoderMode extends CommandResponseNone {
    private final RoboClawMotor mMotor;
    private final boolean mQuatrature;
    private final boolean mSupportRCEncoder;

    protected CommandSetEncoderMode(RoboClawMotor aMotor, 
            boolean aSupportRCEncoder,
            boolean aQuadrature) {
        mMotor = aMotor;
        mQuatrature = aQuadrature;
        mSupportRCEncoder = aSupportRCEncoder;
    }
    
    @Override
    protected byte[] getCommand(byte aAddress) {
        byte[] b = new byte[3];
        
        byte modebyte = (byte) (mMotor.getMotorId() == 0 ? 92 : 93);

        int offset = 0;
        offset = setByte(b, offset, aAddress);
        offset = setByte(b, offset, modebyte);
        byte mode = 0;
        if (mQuatrature) {
            mode |= (0x01);
        }
        if (mSupportRCEncoder) {
            mode |= (0x80);
        }
        setByte(b, offset, mode);
        return b;
    }
    
}
