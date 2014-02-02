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
class CommandDriveMotorsWithIndivAccelSpeed extends CommandResponseNone {
    private final long mAccel1;
    private final long mSpeed1;
    private final long mAccel2;
    private final long mSpeed2;

    protected CommandDriveMotorsWithIndivAccelSpeed(
            long aAccel1,
            long aSpeed1,
            long aAccel2,
            long aSpeed2) {
        mAccel1 = aAccel1;
        mSpeed1 = aSpeed1;
        mAccel2 = aAccel2;
        mSpeed2 = aSpeed2;
        
    }
    
    @Override
    protected byte[] getCommand(byte aAddress) {
        byte[]b = new byte[19];
        
        b[0] = aAddress;
        b[1] = 50;
        
        setLong(b, 2, mAccel1);
        setLong(b, 6, mSpeed1);
        setLong(b, 10, mAccel2);
        setLong(b, 14, mSpeed2);

        b[18] = calculateChecksum(b);
        
        return b;
        
    }
    
}
