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
class CommandReadEncoderPosition extends CommandResponseFixed {
    private final int mMotorId;
    private final long[] mEncoderPosition;

    protected CommandReadEncoderPosition(
            final int aMotorId,
            final long[] aEncoderPosition) {
        mMotorId = aMotorId;
        mEncoderPosition = aEncoderPosition;
    }
    
    @Override
    protected byte[] getCommand(final byte aAddress) {
        byte[] b = new byte[2];
        
        byte cmd = (byte) ((mMotorId == 0) ? 16 : 17);
        
        int offset = 0;
        offset = setByte(b, offset, aAddress);
        setByte(b, offset, cmd);
        return b;
    }
    
    @Override
    protected void onResponse(
            final byte[] aResponseBuffer,
            final int aResponseLength) {


        mEncoderPosition[0] = (byteAsInt(aResponseBuffer[0]) << 24) |
                (byteAsInt(aResponseBuffer[1]) << 16) |
                (byteAsInt(aResponseBuffer[2]) << 8) |
                byteAsInt(aResponseBuffer[3]);
        
        mEncoderPosition[1] = aResponseBuffer[4];
    }

    @Override
    protected int getResponseLength() {
        return 6;
    }
    
}
