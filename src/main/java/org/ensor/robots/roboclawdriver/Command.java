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
 * This is the base class of commands
 * which can be sent to the RoboClaw.
 * This class models the command and the response
 * which comes back from the RoboClaw.
 *
 * @author jona
 */
abstract class Command {
    private final int mReadMode;

    protected static final int READ_MODE_NONE = 0;
    protected static final int READ_MODE_NULL_TERMINATED = 1;
    protected static final int READ_MODE_FIXED = 2;

    /**
     * The constructor creates a new command with the response semantics
     * according to the type given.
     *
     * @param aReadMode This parameter indicates the type of command response
     *                  which will be given by the RoboClaw.
     */
    protected Command(final int aReadMode) {
        mReadMode = aReadMode;
    }
    /**
     * For commands, this returns the sequence of bytes
     * to send in the command.
     *
     * @param aAddress The packet-serial address.  For USB devices, this should
     *                 just be 0x80.
     * @return The sequence of bytes to send.
     */
    protected abstract byte[] getCommand(byte aAddress);
    /**
     * This method handles the response from the command and
     * delegates any side-effects of the response to the appropriate
     * handler.
     * @param aResponseBuffer The response from the motor controller to this
     *                      command.
     * @param aResponseLength The length of the buffer which has been filled
     *                        with a response.
     */
    protected abstract void onResponse(
            final byte[] aResponseBuffer,
            final int aResponseLength);
    /**
     * For commands which need to read a fixed length
     * response, this method should return the number
     * of bytes expected.
     * @return The number of bytes expected to be read
     *         as a response from this command.
     */
    protected abstract int getResponseLength();

    protected int getReadMode() {
        return mReadMode;
    }

    /**
     * This method returns the checksum calculated for the
     * given byte array.
     * @param aBytes A byte array containing bytes for which the checksum should
     *               be calculated.
     * @return The checksum which would appear in the final byte of the array.
     */
    protected byte calculateChecksum(final byte[] aBytes) {
        return calculateChecksum(aBytes, aBytes.length - 1);
    }

    protected byte calculateChecksum(final byte[] aBytes, final int aLength) {
        int cs = 0;

        int i;
        for (i = 0; i < aLength; i++) {
            cs += (aBytes[i]);
        }

        return (byte) (cs & 0x7f);
    }
    protected byte calculateChecksum(
            final byte[] aBytes1,
            final int aLength1,
            final byte[] aBytes2,
            final int aLength2) {
        int cs = 0;

        int i;
        for (i = 0; i < aLength1; i++) {
            cs += (aBytes1[i]);
        }
        for (i = 0; i < aLength2; i++) {
            cs += (aBytes2[i]);
        }

        return (byte) (cs & 0x7f);
    }

    protected int setLong(
            final byte[] b,
            final int aOffset,
            final long aValue) {
        b[aOffset] = (byte) ((aValue >> 24) & 0xff);
        b[aOffset + 1] = (byte) ((aValue >> 16) & 0xff);
        b[aOffset + 2] = (byte) ((aValue >> 8) & 0xff);
        b[aOffset + 3] = (byte) (aValue & 0xff);
        return aOffset + 4;
    }
    protected int setShort(
            final byte[] b,
            final int aOffset,
            final int aValue) {
        b[aOffset] = (byte) ((aValue >> 8) & 0xff);
        b[aOffset + 1] = (byte) ((aValue) & 0xff);
        return aOffset + 2;
    }
    protected int setByte(
            final byte[] b,
            final int aOffset,
            final byte aValue) {
        b[aOffset] = aValue;
        return aOffset + 1;
    }
    protected int setByte(
            final byte[] b,
            final int aOffset,
            final int aValue) {
        b[aOffset] = (byte) aValue;
        return aOffset + 1;
    }

    protected int setChecksum(
            final byte[] b,
            final int aOffset) {
        b[aOffset] = calculateChecksum(b, aOffset);
        return aOffset + 1;
    }

}
