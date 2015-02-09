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
 * This class represents the speed of a differential
 * drive assembly and the rate of turn.  Units of velocity
 * are assumed to be in meters per second and angle changes
 * are measured in terms of radians per second.
 */
public class SpeedAndTurnRate {
    private final double mV;
    private final double mDThetaDT;

    /**
     * This constructs an object which represents a rate of turn
     * and a velocity.  The velocity is measured in terms of meters
     * per second and the rate of turn is measured in terms of radians
     * per second.
     *
     * @param aVelocity Velocity in meters per second.
     * @param aRateOfTurn Rate of turn in radians per second.
     */
    public SpeedAndTurnRate(
            final double aVelocity,
            final double aRateOfTurn) {
        mV = aVelocity;
        mDThetaDT = aRateOfTurn;
    }
    /**
     * This method returns the rate of movement
     * of the object in terms of meters per second.
     * @return The rate of speed.
     */
    public double getVelocity() {
        return mV;
    }
    /**
     * This method returns the rate of turn in
     * terms of Radians per second.
     * @return The number of radians per second that
     *         the object should turn.
     */
    public double getTurnRate() {
        return mDThetaDT;
    }

}
