/*
 * The MIT License
 *
 * Copyright 2015 Jon Arney, Ensor Robotics.
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

package org.ensor.robots.sensors.kinect;

/**
 * These describe the various log levels for the
 * libfreenect library.
 * @author jona
 */
public enum LogLevel {
    /**< Log for crashing/non-recoverable errors. */
    FREENECT_LOG_FATAL(0),
    /**< Log for major errors. */
    FREENECT_LOG_ERROR(1),
    /**< Log for warning messages. */
    FREENECT_LOG_WARNING(2),
    /**< Log for important messages. */
    FREENECT_LOG_NOTICE(3),
    /**< Log for normal messages. */
    FREENECT_LOG_INFO(4),
    /**< Log for useful development messages. */
    FREENECT_LOG_DEBUG(5),
    /**< Log for slightly less useful messages. */
    FREENECT_LOG_SPEW(6),
    /**< Log EVERYTHING. May slow performance. */
    FREENECT_LOG_FLOOD(7);
    
    private final int mLevel;
    private LogLevel(final int aLevel) { mLevel = aLevel; }
    protected int getLevel() { return mLevel; }
}

