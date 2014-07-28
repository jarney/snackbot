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

import org.ensor.algorithms.control.pid.ISensor;
import org.ensor.math.geometry.Vector2;

/**
 * This class represents an odometry calculator
 * for differential drive mobile robots.  It calculates
 * position based on the movement of the left and right
 * wheels.  It relies on a simple differential wheel model.
 *
 * @author jona
 */
public class Odometry {

    private Vector2 mCurrentPosition;
    private double mDirection;

    private final Model mDifferentialDriveModel;
    private final ISensor mLeftSpeed;
    private final ISensor mRightSpeed;
    private final ISensor mAngleSensor;

    class AngleSensor implements ISensor {
        public double getValue() {
            return Math.atan2(Math.sin(mDirection), Math.cos(mDirection));
        }
    }
    /**
     * This constructor initializes an odometry calculator
     * capable of estimating position based on the input readings of
     * a pair of odometers and a differential drive wheel model.
     * @param aDifferentialDriveModel A model of the differential drive
     *                                which allows calculation of relative wheel
     *                                velocities from speed and angle and
     *                                vice-versa.
     * @param aLeftSpeedSensor This sensor returns the speed of the left wheel
     *                         in meters per second.
     * @param aRightSpeedSensor This sensor returns the speed of the right wheel
     *                         in meters per second.
     */
    public Odometry(
            final Model aDifferentialDriveModel,
            final ISensor aLeftSpeedSensor,
            final ISensor aRightSpeedSensor
        ) {
        mDifferentialDriveModel = aDifferentialDriveModel;
        mLeftSpeed = aLeftSpeedSensor;
        mRightSpeed = aRightSpeedSensor;
        mAngleSensor = new AngleSensor();
    }

    /**
     * This method overrides the direction calculated by the odometry.  This
     * is useful when other measures of the current position yield better
     * estimates based on other sensors.  The direction is measured according
     * to a fixed reference coordinate system and is always in units of radians.
     * @param aDirection New direction.
     */
    public void setDirection(final double aDirection) {
        mDirection = aDirection;
    }
    /**
     * This method returns the direction calculated by odometry.  The direction
     * is measured according to a fixed reference coordinate system
     * and is always in units of radians.
     * @return Returns the direction angle (counter-clockwise from (1,0) )
     *         in radians.
     */
    public double getDirection() {
        return mDirection;
    }
    /**
     * This method overrides the position calculated by odometry.  This is
     * useful when other measures of the current position yield better
     * estimates based on other sensors.
     * @param aPosition New position.
     */
    public void setPosition(final Vector2 aPosition) {
        mCurrentPosition = aPosition;
    }
    /**
     * This method returns a sensor which can return the current angle
     * based on the odometry calculations.
     * @return An angle sensor which returns the angle based on odometry.
     *         The angle sensor is suitable for use in a PID controller
     *         because it wraps the angle to be strictly between -PI and PI.
     */
    public ISensor getAngleSensor() {
        return mAngleSensor;
    }
    /**
     * This method advances the odometry simulation by the time unit
     * given.
     * @param dt The elapsed time (in seconds).
     */
    public void tick(final double dt) {
        double leftSpeed = mLeftSpeed.getValue();
        double rightSpeed = mRightSpeed.getValue();

        Model.SpeedAndTurnRate bearing =
                mDifferentialDriveModel.calculateBearing(leftSpeed, rightSpeed);

        Vector2 distanceTraveled = new Vector2(
                bearing.getVelocity() * dt * Math.cos(mDirection),
                bearing.getVelocity() * dt * Math.sin(mDirection)
        );
        mCurrentPosition = mCurrentPosition.add(distanceTraveled);
        mDirection = mDirection + bearing.getTurnRate() * dt;
    }
}
