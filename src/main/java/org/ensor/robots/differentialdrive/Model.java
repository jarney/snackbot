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

package org.ensor.robots.differentialdrive;
/**
 * This class implements an elementary model for
 * differential steering based on
 * <a href="http://rossum.sourceforge.net/papers/DiffSteer/">this paper</a>.
 *
 * All units of length are presumed to be in terms of meters
 * and angle measures are in terms of radians.
 *
 * @author jona
 */
public class Model {

    private double mWheelDistance;
    private double mHalfWheelDistance;
    /**
     * This instantiates the simple differential drive model
     * with the distance between the two wheels
     * given in meters.
     *
     * @param aWheelDistance The distance between the center-point
     * of the wheels.  The model assumes that the wheels rotate along the
     * same axis independently of each other.
     */
    public Model(final double aWheelDistance) {
        mWheelDistance = aWheelDistance;
        mHalfWheelDistance = mWheelDistance / 2;
    }
    /**
     * This method adjusts the distance between the two wheels of
     * a differential drive system.
     * @param aWheelDistance The distance between wheels (in meters) of the
     *                       differential drive system.
     */
    public void setWheelDistance(final double aWheelDistance) {
        mWheelDistance = aWheelDistance;
        mHalfWheelDistance = mWheelDistance / 2;
    }

    /**
     * This method returns the distance between two wheels
     * in a differential drive system.
     * @return The distance between wheels (in meters) of the
     *         differential drive system.
     */
    public double getWheelDistance() {
        return mWheelDistance;
    }


    /**
     * This method calculates the speed and turn rate
     * from the given wheel velocities.
     *
     * @param aWheelVelocities The velocities of the two
     *        wheels of the differential drive.
     * @return The speed and rate of turn for the assembly.
     */
    public SpeedAndTurnRate calculateBearing(
            final WheelVelocities aWheelVelocities) {
        double vr = aWheelVelocities.getRightVelocity();
        double vl = aWheelVelocities.getLeftVelocity();
        double v = (vr + vl) / 2;
        double dthetadt = (vr - vl) / mWheelDistance;
        return new SpeedAndTurnRate(v, dthetadt);
    }
    /**
     * This method calculates the speed and turn rate
     * from the given wheel velocities.
     *
     * @param aLeftVelocity The left wheel velocity.
     * @param aRightVelocity The right wheel velocity.
     * @return The speed and rate of turn for the assembly.
     */
    public SpeedAndTurnRate calculateBearing(
            final double aLeftVelocity,
            final double aRightVelocity) {
        double v = (aRightVelocity + aLeftVelocity) / 2;
        double dthetadt = (aRightVelocity - aLeftVelocity) / mWheelDistance;
        return new SpeedAndTurnRate(v, dthetadt);
    }

    /**
     * This method returns the difference in wheel speeds
     * required in order to achieve a particular
     * turn rate.
     * @param aTurnRate The turn rate (radians/sec) required.
     * @return The difference in wheel speeds required in order to achieve the
     *         turn rate.
     */
    public double speedDifferenceForTurnRate(final double aTurnRate) {
        return mWheelDistance * Math.abs(aTurnRate);
    }

    /**
     * This method helps to calculate the speed turn rate which would
     * result from each wheel moving at the given speed in opposite directions.
     *
     * @param aWheelSpeed Rate at which each wheel is moving in opposite
     *                    directions (m/s)
     * @return The number of radians per second that the object will turn.
     */
    public double turnRateForSpeed(final double aWheelSpeed) {
        return Math.abs(2 * aWheelSpeed) / mWheelDistance;
    }

    /**
     * This method calculates the speed of each of the
     * wheels given the total velocity and rate of turn
     * desired.
     * @param aVelocity The desired total velocity.
     * @param aRadiansPerSecond The desired rate of turn.
     * @return The left and right wheel velocities to achieve this state.
     */
    public WheelVelocities calculateWheelVelocities(
            final double aVelocity,
            final double aRadiansPerSecond) {
        double rotationVelocity = mHalfWheelDistance * aRadiansPerSecond;
        double vl = aVelocity + rotationVelocity;
        double vr = aVelocity - rotationVelocity;
        return new WheelVelocities(vl, vr);
    }

}
