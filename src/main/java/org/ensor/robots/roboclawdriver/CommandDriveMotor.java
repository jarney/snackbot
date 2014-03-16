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
final class CommandDriveMotor extends CommandResponseNone {

    private final byte mSpeed;
    private final boolean mForward;
    private final int mMotorId;

    protected CommandDriveMotor(
            final byte aSpeed,
            final boolean aForward,
            final int aMotorId) {
        mSpeed = aSpeed;
        mForward = aForward;
        mMotorId = aMotorId;
    }

    protected byte[] getCommand(final byte aAddress) {
        byte[] b = new byte[4];

        int offset = 0;
        offset = setByte(b, offset, aAddress);
        if (mMotorId == 0) {
            if (mForward) {
                offset = setByte(b, offset, 0);
            } else {
                offset = setByte(b, offset, 1);
            }
        } else {
            if (mForward) {
                offset = setByte(b, offset, 4);
            } else {
                offset = setByte(b, offset, 5);
            }
        }
        offset = setByte(b, offset, mSpeed);
        setChecksum(b, offset);

        return b;
    }

}