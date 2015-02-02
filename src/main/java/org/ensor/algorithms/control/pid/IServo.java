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

package org.ensor.algorithms.control.pid;

/**
 * A servo object is an object which represents a physical servo device.
 * A servo is an object which can control a particular operation, process,
 * or state through the use of feedback.  In practice, this can be used
 * to represent a standard RC servo or a motor with an encoder to sense the
 * position and/or speed.  For a standard RC servo, the angular position is
 * controlled through the use of a motor and a potentiometer to sense the
 * actual position as compared to the desired position.  For a motor with
 * a encoder, the motor can be driven and the encoder can be used to sense
 * the position or the speed.  In this case, there are 2 types of servo
 * objects possible, a "Speed Servo" which controls the speed of rotation
 * and a "Position Servo" which controls the position of rotation.  This
 * interface can be used to model either of these processes.
 *
 * @author jona
 */
public interface IServo {

    /**
     * This method can be used to set the 'position' of the servo.  The input
     * parameter is a double precision floating point value.  The value of
     * the servo position is abstract and the meaning depends on what type of
     * servo controller it is.  In the case of a 'speed' servo, this indicates
     * the speed rather than the actual position since in this case what is
     * being modeled as the 'control' parameter is the 'speed'.
     * @param aPosition The set point of the control system.
     */
    void setPosition(double aPosition);
    
    // HACK!  This should be in a PID regulator interface instead.
    void setPID(double P, double I, double D, double aMinError, double aMaxError);
    
}
