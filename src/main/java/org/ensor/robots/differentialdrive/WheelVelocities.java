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

package org.ensor.robots.differentialdrive;

/**
 * This class represents the state of movement of a differential
 * drive assembly by representing the velocities of each of the
 * two wheels.  Velocities are measured in terms of meters per second.
 */
public class WheelVelocities {
    private final double mVr;
    private final double mVl;

    /**
     * The constructor creates a new object representing
     * the state of movement of the differential drive assembly
     * from the given two wheel velocities.
     * @param aRight The speed of the right wheel in meters per second.
     * @param aLeft The speed of the left wheel in meters per second.
     */
    public WheelVelocities(
            final double aLeft,
            final double aRight) {
        mVl = aLeft;
        mVr = aRight;
    }
    /**
     * This method returns the speed of the right wheel in
     * meters per second.
     * @return Speed in meters per second.
     */
    public double getRightVelocity() {
        return mVr;
    }
    /**
     * This method returns the speed of the left wheel in
     * meters per second.
     * @return Speed in meters per second.
     */
    public double getLeftVelocity() {
        return mVl;
    }

}
