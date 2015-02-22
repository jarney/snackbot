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
 *
 * @author jona
 */
public enum LEDState {
    
    /** LED is turned off. */
    LED_OFF(0),
    /** LED is on green. */
    LED_GREEN(1),
    /** LED is on red. */
    LED_RED(2),
    /** LED is on yellow. */
    LED_YELLOW(3),
    /** LED is blinking green. */
    LED_BLINK_GREEN(4),
    // 5 is same as 4, LED blink Green
    /** Make LED blink Red/Yellow. */
    LED_BLINK_RED_YELLOW(6);

    private LEDState(final int a) { mState = a; }
    private final int mState;
    protected int getValue() { return mState; }
}
