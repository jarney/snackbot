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

package org.ensor.io.tty;

/**
 * This is the factory which creates an appropriate TTY device object based
 * on the platform the code is being executed on.
 * @author jona
 */
public final class TTYFactory {

    private TTYFactory() { }

    /**
     * This method returns a suitable TTY device opened in RAW mode so that
     * binary communications can occur on it.  The TTY returned will send and
     * receive data on the associated streams without interpreting the data
     * being sent.  Any control characters will be sent as raw binary data
     * instead of being intercepted by the TTY device.
     *
     * @param aTTY The absolute path of the TTY device.  Note that the format
     *             of this path will be platform dependent.
     *
     * @return This method returns an ITTY interface suitable for use on this
     *         platform.
     * @throws Exception If the TTY device was not found or could not be
     *                   initialized for some reason, this method will throw
     *                   a general exception.
     */
    public static ITTY open(final String aTTY) throws Exception {
        return new TTYLinux(aTTY);
    }

}
