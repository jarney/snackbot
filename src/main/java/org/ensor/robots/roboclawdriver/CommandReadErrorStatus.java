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
class CommandReadErrorStatus extends CommandResponseFixed {

    private final RoboClaw mRoboClaw;
    
    protected CommandReadErrorStatus(RoboClaw aRoboClaw) {
        mRoboClaw = aRoboClaw;
    }

    @Override
    protected byte[] getCommand(byte aAddress) {
        byte[] b = new byte[2];
        b[0] = aAddress;
        b[1] = 90;
        return b;
    }

    @Override
    protected void onResponse(byte[] aResponseBuffer, int aResponseLength) {
        mRoboClaw.setErrorStatus(aResponseBuffer[0] << 8 | 
                aResponseBuffer[1]);
    }

    @Override
    protected int getResponseLength() {
        return 3;
    }
    
}
