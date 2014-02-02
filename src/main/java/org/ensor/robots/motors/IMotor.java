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
 * This interface represents a single motor object which
 * is run at a particular voltage and can draw current.
 * The motor can be driven by specifying a duty cycle as a signed
 * value from -1 to 1 indicating the direction and strength with which
 * the motor should be driven.
 *
 * @author jona
 */
public interface IMotor extends ICurrentMeasurable {

    /**
     * This method drives the motor according to the
     * given duty-cycle.  The duty-cycle is a value between
     * -1 and 1 which indicates the direction and relative strength
     * with which to drive this motor.  A value of "-1" drives the
     * motor as hard as possible in the reverse direction.  A value of "1"
     * drives the motor as hard as possible in the forward direction.
     * A value of "0" causes the motor not to be driven.  Note that
     * due to physical and electrical characteristics of the motor,
     * driver, wheels, and other factors, the input duty cycle and the output
     * force and speed may not be linear.
     *
     * @param aDutyCycle A value between "-1" and "1" which indicates how
     *                   hard to drive the motor and in what direction.
     */
    void setDutyCycle(final double aDutyCycle);

}
