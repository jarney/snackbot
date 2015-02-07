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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ensor.math.geometry.Vector2;
import org.ensor.algorithms.control.pid.PIDController;
import org.ensor.robots.network.server.BioteSocket;

/**
 * This class represents a control servo
 * which acts to keep an object traveling
 * in the direction of the given target point
 * and traveling at an appropriate speed toward
 * it.  Any deviations from the path will be corrected.
 * @author jona
 */
public class PathServo {
    private static Logger mLogger = Logger.getLogger(PathServo.class.getName());

    private Vector2 mCurrentPosition;
    private double mCurrentAngle;
    
    private Vector2 mTargetPosition;
    private double mTargetDirection;

    private PIDController mDistanceServo;
    private double mDistanceTolerance;
    
    private PIDController mAngleServo;
    private double mAngleTolerance;
    
    private IGoalReachedListener mGoalReachedListener;
    
    private int mState;
    
    private static final int STATE_IDLE = 0;
    private static final int STATE_MOVING = 1;
    private static final int STATE_TURNING = 2;
    
    
    /**
     * The constructor creates a new path
     * following servo object with the given
     * maximum speed and turn speed in order to
     * place a bound on the speed at which the object
     * will attempt to move.
     * @param aDistanceTolerance The distance (in meters) at which we decide we
     *                           are close enough to our goal to consider that
     *                           we have reached it.
     * @param aAngleTolerance The angle at which we decide we are close enough
     *                        to our goal.
     * @param aDecelerationDistance The distance at which we should begin
     *                              to slow down before reaching the goal.
     * @param aGoalReachedListener This is a listener which will be called
     *                             when the goal has been reached and we are
     *                             ready to traverse to a new goal.
     */
    public PathServo(
            final double aDistanceTolerance,
            final double aAngleTolerance,
            final PIDController aSpeedServo,
            final PIDController aDirectionServo,
            final IGoalReachedListener aGoalReachedListener
    ) {
        
        mDistanceTolerance = aDistanceTolerance;
        
        mAngleTolerance = aAngleTolerance;

        mGoalReachedListener = aGoalReachedListener;
        
        mTargetPosition = null;
        mTargetDirection = 0.0;
        
        mCurrentPosition = null;
        mCurrentAngle = 0.0;
        
        mDistanceServo = aSpeedServo;
        mAngleServo = aDirectionServo;
        
        mState = STATE_IDLE;
        
    }
    
    /**
     * This method sets the destination of the path controller
     * and indicates the speed at which the object should be traveling
     * when the destination is reached.
     * @param aDestination The point the object will be traveling toward
     *                     in meters based on a stationary coordinate system.
     * @param aDestinationDirection The direction the object should be facing
     *                              when it reaches the destination as measured
     *                              in radians.
     */
    public void setDestination(
            final Vector2 aDestination,
            final double aDestinationDirection
    ) {
        mTargetPosition = aDestination;
        mTargetDirection = aDestinationDirection;
        mState = STATE_MOVING;
    }
    
    /**
     * This method sets the current position of the object
     * so that the direction and speed it should move in order to
     * reach the destination can be calculated.
     * @param aCurrentPosition The current position of the object in meters
     *                         based on a stationary coordinate reference.
     * @param aCurrentDirection The direction the object is currently facing as
     *                          as measured in radians.
     */
    public void setCurrentPosition(
            final Vector2 aCurrentPosition,
            final double aCurrentDirection
    ) {
        mCurrentPosition = aCurrentPosition;
        double y = Math.sin(aCurrentDirection);
        double x = Math.cos(aCurrentDirection);
        mCurrentAngle = Math.atan2(y, x);
    }
    public void setDistanceTolerance(final double aDistanceTolerance) {
        mDistanceTolerance = aDistanceTolerance;
    }
    public double getDistanceTolerance() {
        return mDistanceTolerance;
    }
    public void setAngleTolerance(final double aAngleTolerance) {
        mAngleTolerance = aAngleTolerance;
    }
    public double getAngleTolerance() {
        return mAngleTolerance;
    }
    private void tickMoving(final double dt) {
        Vector2 vectorToGoal = mTargetPosition.subtract(mCurrentPosition);
        double distanceFromGoal = vectorToGoal.length();
        
        if (distanceFromGoal < mDistanceTolerance) {
            mState = STATE_TURNING;
            return;
        }
        
        double angle = Math.atan2(vectorToGoal.getY(), vectorToGoal.getX());

        mLogger.log(Level.INFO, "Current distance is " + distanceFromGoal);
        mLogger.log(Level.INFO, "Current angle is " + mCurrentAngle);
        
        // Let the distance servo
        // calculate an appropriate speed
        // for the distance to the goal.
        mDistanceServo.setPosition(0.0);
        mDistanceServo.tick(dt, distanceFromGoal, "Distance");
        
        // Let the angle servo calculate
        // an appropriate angular velocity
        // for the distance to the goal.
        mAngleServo.setPosition(angle);
        mAngleServo.tick(dt, mCurrentAngle, "Angle");
        
    }

    public void tickTurning(double dt) {

        double distanceFromGoal = mCurrentAngle - mTargetDirection;
        
        if (Math.abs(distanceFromGoal) < mAngleTolerance) {
            mGoalReachedListener.reached();
            mState = STATE_IDLE;
        }
        Logger.getLogger(BioteSocket.class.getName()).log(Level.INFO,
            "Turning forward torward " + mTargetDirection + " current " + mCurrentAngle);
 
        
        // With these parameters, the distance
        // servo should calculate a zero forward
        // speed and we should simply spin in place.
        mDistanceServo.setPosition(0.0);
        mDistanceServo.tick(dt, 0.0, "Distance");
        

        // Let the direction servo calculate a suitable
        // direction to turn toward the goal.
        mAngleServo.setPosition(mTargetDirection);
        mAngleServo.tick(dt, mCurrentAngle, "Angle");
    }
    
    private void tickIdle() {
    }

    public void abort() {
        mState = STATE_IDLE;
        mDistanceServo.reset();
        mAngleServo.reset();
    }
    
    public boolean isMoving() {
        return mState == STATE_MOVING || mState == STATE_TURNING;
    }
    
    public void tick(double dt) {

        switch (mState) {
            case STATE_MOVING:
                tickMoving(dt);
                break;
            case STATE_TURNING:
                tickTurning(dt);
                break;
            case STATE_IDLE:
                tickIdle();
                break;
        }
    }

}
