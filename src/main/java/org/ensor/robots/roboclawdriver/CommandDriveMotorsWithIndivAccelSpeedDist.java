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
class CommandDriveMotorsWithIndivAccelSpeedDist 
    extends CommandResponseNone {

    private final long mAccel1;
    private final long mSpeed1;
    private final long mDist1;
    
    private final long mAccel2;
    private final long mSpeed2;
    private final long mDist2;

    protected CommandDriveMotorsWithIndivAccelSpeedDist(
            final long aAccel1,
            final long aSpeed1,
            final long aDist1,
            final long aAccel2,
            final long aSpeed2,
            final long aDist2) {
        mAccel1 = aAccel1;
        mSpeed1 = aSpeed1;
        mDist1 = aDist1;
        mAccel2 = aAccel2;
        mSpeed2 = aSpeed2;
        mDist2 = aDist2;
        
    }

    @Override
    protected byte[] getCommand(byte aAddress) {
        byte[] b = new byte[27];

        b[0] = aAddress;
        b[1] = 51;

        setLong(b, 2, mAccel1);
        setLong(b, 6, mSpeed1);
        setLong(b, 10, mDist1);
        setLong(b, 14, mAccel2);
        setLong(b, 18, mSpeed2);
        setLong(b, 22, mDist2);

        b[26] = calculateChecksum(b);

        return b;

    }
}
