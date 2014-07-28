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
import org.ensor.algorithms.control.pid.IServo;
import org.ensor.algorithms.control.pid.PIDController;

/**
 *
 * @author jona
 */
public class ControlLoop {

    private final PIDController mAngleController;
    private final Model mSimpleModel;
    private final Odometry mOdometry;

    private double mDesiredSpeed;
    private double mDesiredAngle;
    
    private double mTurnRate;
    private double mMaxSpeed;
    
    private IServo mLeftSpeedControl;
    private IServo mRightSpeedControl;
    private ISensor mLeftSpeedSensor;
    private ISensor mRightSpeedSensor;
    
    class TurnRateController implements IServo {
        public void setPosition(double aValue) {
            mTurnRate = aValue;
        }
    }
    
    
    public ControlLoop() {

        mSimpleModel = new Model(0.361);
        
        // Calculate the maximum turn rate.
        double maxTurnRate = mSimpleModel.turnRateForSpeed(mMaxSpeed);

        mOdometry = new Odometry(
                mSimpleModel,
                mLeftSpeedSensor,
                mRightSpeedSensor
        );
        
        mAngleController = new PIDController(
                mOdometry.getAngleSensor(),
                new TurnRateController(),
                1,
                0.5,
                0.25,
                -maxTurnRate,
                maxTurnRate
        );
    }

    public void setPosition(
        final double aDesiredAngle, final double aDesiredSpeed) {
        mDesiredAngle = aDesiredAngle;
        mDesiredSpeed = aDesiredSpeed;
    }
    
    public void tickAnglePID(long dt) {

        mOdometry.tick(dt);
        mAngleController.setPosition(mDesiredAngle);
        mAngleController.tick(dt);

        // This serves to cap the speed based on the turn rate.
        double speedDifference = mSimpleModel.
                speedDifferenceForTurnRate(mTurnRate);

        double maxSpeedForTurn = mMaxSpeed - speedDifference;
        double speed = Math.min(maxSpeedForTurn, mDesiredSpeed);

        Model.WheelVelocities wheelVelocities =
                mSimpleModel.calculateWheelVelocities(speed, mTurnRate);

        mLeftSpeedControl.setPosition(wheelVelocities.getLeftVelocity());
        mRightSpeedControl.setPosition(wheelVelocities.getRightVelocity());

    }
}
