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
package org.ensor.robots.motors;

/**
 *
 * @author jona
 */
public interface IMotorWithEncoder extends IMotor {
    /**
     * This method returns the current position of the quadrature encoder
     * register.
     * @return The current value of the quadrature encoder register.
     */
    long getEncoderPosition();

    /**
     * This method returns the current speed of the motor in terms of
     * quadrature encoder pulses per second.
     * @return The number of pulses per second of the quadrature encoder.
     */
    long getEncoderSpeed();

    /**
     * This method returns true if the motor has a quadrature encoder
     * and 'false' if the motor has an absolute position encoder.
     * @return True if the motor has a quadrature encoder.
     */
    boolean getEncoderQuatratureMode();

}
