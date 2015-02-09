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

import java.util.logging.Logger;
import org.ensor.algorithms.control.pid.IController;
import org.ensor.algorithms.control.pid.IMover;
import org.ensor.algorithms.control.pid.ISensor;
import org.ensor.algorithms.control.pid.PIDController;
import org.ensor.math.geometry.Vector2;

/**
 * This class represents a control servo
 * which acts to keep an object traveling
 * in the direction of the given target point
 * and traveling at an appropriate speed toward
 * it.  Any deviations from the path will be corrected.
 * @author jona
 */
public class MoverToPoint
            implements IMover<Position, SpeedAndTurnRate> {
    
    private static final Logger LOG =
            Logger.getLogger(MoverToPoint.class.getName());
    
    private final Vector2 mTargetPosition;
    private final double mTargetTolerance;
    
    private final PIDController mAngleController;
    private final PIDController mDistanceController;

    private double mCurrentAngle;
    private double mDistance;
    
    private double mSpeed;
    private double mTurnRate;
    
    private final ISensor<Double> mAngleSensor = new ISensor<Double>() {
        public Double getValue() {
            return mCurrentAngle;
        }
    };
    
    private final ISensor<Double> mDistanceSensor = new ISensor<Double>() {
        public Double getValue() {
            return mDistance;
        }
    };
    private final IController<Double> mSpeedController =
        new IController<Double>() {
        public void setOutput(final Double aT) {
            mSpeed = aT;
        }
    };
    private final IController<Double> mTurnRateController =
        new IController<Double>() {
        public void setOutput(final Double aT) {
            mTurnRate = aT;
        }
    };
    
    
    /**
     * The constructor creates a new regulator
     * which causes the robot to be controlled so that it reaches
     * a particular destination point to within a specified tolerance.
     * @param aTarget The destination point that the controller
     *                     will attempt to reach.
     * @param aTargetTolerance The distance in meters for the
     *                              destination tolerance.
     * @param aDistanceController This is a PID controller for controlling
     *                            speed based on the distance from the goal.
     * @param aAngleController This is a PID controller for controlling the
     *                         turn rate based on the angle.
     */
    public MoverToPoint(
            final Vector2 aTarget,
            final double aTargetTolerance,
            final PIDController aDistanceController,
            final PIDController aAngleController
    ) {
        mTargetPosition = aTarget;
        mTargetTolerance = aTargetTolerance;
        mDistanceController = aDistanceController;
        mDistanceController.reset();
        mAngleController = aAngleController;
        mAngleController.reset();
        
        mDistance = 0;
        mCurrentAngle = 0;
        
        mSpeed = 0;
        mTurnRate = 0;
    }
    
    /**
     * This method updates the speed and direction of turn of the
     * object based on the position read from the sensors.  This
     * uses a PID controller to control the position so that it converges
     * on a particular set point.
     * @param dt The amount of time elapsed since the last update.
     * @param aSensor This parameter is a sensor which gives the position
     *                and orientation of the robot.
     * @param aController This parameter allows the speed and turn rate
     *                    to be controlled.
     */
    @Override
    public void tick(
            final double dt,
            final ISensor<Position> aSensor,
            final IController<SpeedAndTurnRate> aController) {
        
        Position pos = aSensor.getValue();
        
        Vector2 currentPosition = pos.getPosition();
        
        Vector2 vectorToGoal = mTargetPosition.subtract(currentPosition);
        mDistance = vectorToGoal.length();
        if (mDistance != 0) {
            vectorToGoal.multiply(1 / mDistance);
        }
        mCurrentAngle = pos.getAngle();

        if (goalReached()) {
            return;
        }

        // This is the desired angle we wish to reach.
        double desiredAngle = Math.atan2(vectorToGoal.getY(),
                vectorToGoal.getX());
        
        // The desired distance from the goal is zero, so the PID
        // regulator should always target '0'.
        mDistanceController.setPosition(0.0);
        
        // The desired angle is the angle which takes us in the direction
        // toward the goal.
        mAngleController.setPosition(desiredAngle);

        // Activate the PID controllers to control
        // the speed and angular velocity as controlled by the
        // distance and angle inputs respectively.
        mDistanceController.tick(dt, mDistanceSensor, mSpeedController);
        mAngleController.tick(dt, mAngleSensor, mTurnRateController);
        
        // Use the output from the PID regulator to send output to
        // the speed and turn-rate controller.  This translates
        // directly to the left/right wheel velocities.
        aController.setOutput(
                new SpeedAndTurnRate(mSpeed, mTurnRate)
        );
        
    }

    /**
     * This method returns true when the robot is within the desired target
     * tolerance of the goal position.  The servos are operated until this
     * is the case.
     * @return True if the robot has reached its desired position.
     */
    public boolean goalReached() {
        return (mDistance < mTargetTolerance);
    }
}
