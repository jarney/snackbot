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
import org.ensor.algorithms.control.pid.IServo;
import org.ensor.robots.network.server.BioteSocket;

/**
 *
 * @author jona
 */
public class DifferentialDriveServo {

        // Differential drive model.
    private final Model mDifferentialDriveModel;
    private final IServo mRight;
    private final IServo mLeft;
    private double mSpeed;
    private double mAngle;
    private double mMaxVel;
    
    private final SpeedControl mSpeedControl;
    private final AngleControl mAngleControl;
    private double mCurrentAngle;
    
    private double mLeftSpeed;
    private double mRightSpeed;
    
    public double getLeftSpeed() { return mLeftSpeed; }
    public double getRightSpeed() { return mRightSpeed; }
    
    class SpeedControl implements IServo {
        public void setPosition(double aPosition) {
            mSpeed = aPosition;
        }

        public void setPID(double P, double I, double D, double aMinError, double aMaxError) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    class AngleControl implements IServo {
        public void setPosition(double aPosition) {
            mAngle = aPosition;
            double twopi = Math.PI * 2;
            while (mAngle > twopi) mAngle -= twopi;
            while (mAngle < 0) mAngle += twopi;
        }

        public void setPID(double P, double I, double D, double aMinError, double aMaxError) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    
    public DifferentialDriveServo(
            final Model aDifferentialDriveModel,
            final double aMaxVel,
            final IServo aLeft,
            final IServo aRight) {
        mLeft = aLeft;
        mRight = aRight;
        mDifferentialDriveModel = aDifferentialDriveModel;
        mMaxVel = aMaxVel;
        mSpeedControl = new SpeedControl();
        mAngleControl = new AngleControl();
    }
    public void setMaxMovementSpeed(double aMaxMovementSpeed) {
        mMaxVel = aMaxMovementSpeed;
    }
    public double getMaxMovementSpeed() {
        return mMaxVel;
    }
    public IServo getSpeedControl() {
        return mSpeedControl;
    }
    public IServo getAngleControl() {
        return mAngleControl;
    }
    
    public void setAngle(double aCurrentAngle) {
        mCurrentAngle = aCurrentAngle;
        double twopi = Math.PI * 2;
        while (mCurrentAngle > twopi) mCurrentAngle -= twopi;
        while (mCurrentAngle < 0) mCurrentAngle += twopi;
    }
    
    public void tick() {
        
        double dAngle = mCurrentAngle - mAngle;

        dAngle = Math.atan2(Math.sin(dAngle), Math.cos(dAngle));
        
        // The cosine of the difference in angles will tell us
        // how close the angles are to one another.  From this, we
        // deduce how quickly to turn.
        double absoluteTurnRate;
        double speed;
        if (dAngle > Math.PI/2) {
            absoluteTurnRate = mDifferentialDriveModel.turnRateForSpeed(mMaxVel);
            speed = 0;
        }
        else if (dAngle < -Math.PI/2) {
            absoluteTurnRate = -mDifferentialDriveModel.turnRateForSpeed(mMaxVel);
            speed = 0;
        }
        else {
            absoluteTurnRate = dAngle / (Math.PI/2) *
                    mDifferentialDriveModel.turnRateForSpeed(mMaxVel);
            speed = mSpeed -
                    mDifferentialDriveModel.
                            speedDifferenceForTurnRate(absoluteTurnRate);
        }
        

        // If this would lead us to travel faster than
        // our desired speed, then we move only at our desired speed.
        // This allows us to continue to turn at a high rate
        // while still allowing correct speed control.
        if (speed > mSpeed) {
            speed = mSpeed;
        }
        if (speed < 0) {
            speed = 0;
        }
        
        Model.WheelVelocities wheelVelocities = mDifferentialDriveModel.
                calculateWheelVelocities(
                        speed,
                        absoluteTurnRate/1.5
                );
        
        Logger.getLogger(BioteSocket.class.getName()).log(Level.INFO,
            "dAngle is " + dAngle + " current angle: " + mCurrentAngle + " desired angle " + mAngle);
        
        Logger.getLogger(BioteSocket.class.getName()).log(Level.INFO,
            "Trying to make speed " + speed + " and turn rate " + absoluteTurnRate);

        mRightSpeed = wheelVelocities.getRightVelocity();
        mLeftSpeed = wheelVelocities.getLeftVelocity();
        
        Logger.getLogger(BioteSocket.class.getName()).log(Level.INFO,
            "Velocities : " +
                    mSpeed + " l/r: " + 
            mLeftSpeed + " : " +
            mRightSpeed);
        
        mLeft.setPosition(mLeftSpeed);
        mRight.setPosition(mRightSpeed);
    }
    
}
