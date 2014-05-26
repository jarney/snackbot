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

package org.ensor.robots.motors.differentialdrive;
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
public class SimpleModel {

    private final double mWheelDistance;
    private final double mHalfWheelDistance;
    /**
     * This instantiates the simple differential drive model
     * with the distance between the two wheels
     * given in meters.
     *
     * @param aWheelDistance The distance between the center-point
     * of the wheels.  The model assumes that the wheels rotate along the
     * same axis independently of each other.
     */
    public SimpleModel(final double aWheelDistance) {
        mWheelDistance = aWheelDistance;
        mHalfWheelDistance = mWheelDistance / 2;
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
        double vr = aVelocity + rotationVelocity;
        double vl = aVelocity - rotationVelocity;
        return new WheelVelocities(vr, vl);
    }
    /**
     * This class represents the speed of a differential
     * drive assembly and the rate of turn.  Units of velocity
     * are assumed to be in meters per second and angle changes
     * are measured in terms of radians per second.
     */
    public static class SpeedAndTurnRate {
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
    /**
     * This class represents the state of movement of a differential
     * drive assembly by representing the velocities of each of the
     * two wheels.  Velocities are measured in terms of meters per second.
     */
    public static class WheelVelocities {
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
                final double aRight,
                final double aLeft) {
            mVr = aRight;
            mVl = aLeft;
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

}
