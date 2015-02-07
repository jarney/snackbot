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
    private static Logger mLogger = Logger.getLogger(PathServo.class.getName());

        // Differential drive model.
    private final Model mDifferentialDriveModel;
    private final IServo mRight;
    private final IServo mLeft;
    private double mSpeed;
    private double mTurnRate;
    private double mMaxVel;
    
    private final SpeedControl mSpeedControl;
    private final TurnRateControl mTurnRateControl;
    
    private double mLeftSpeed;
    private double mRightSpeed;
    
    public double getLeftSpeed() { return mLeftSpeed; }
    public double getRightSpeed() { return mRightSpeed; }
    
    class SpeedControl implements IServo {
        public void setPosition(final double aPosition) {
            mSpeed = aPosition;
        }

        public void setPID(double P, double I, double D, double aMinError, double aMaxError) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    class TurnRateControl implements IServo {
        public void setPosition(final double aPosition) {
            mTurnRate = aPosition;
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
        mTurnRateControl = new TurnRateControl();
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
        return mTurnRateControl;
    }
    
    public void tick() {
        double speed = mSpeed;
        double turnRate = mTurnRate;
        
        mLogger.log(Level.INFO, "Speed/turn rate: " + speed + ":" + turnRate);
        
        double maxTurnRate = mDifferentialDriveModel.turnRateForSpeed(mMaxVel);
        if (turnRate > maxTurnRate) {
            turnRate = maxTurnRate;
        }
        else if (turnRate < -maxTurnRate) {
            turnRate = -maxTurnRate;
        }
        
        double maxSpeedForTurnRate =
                mDifferentialDriveModel.speedDifferenceForTurnRate(turnRate);

        double maxVel = mMaxVel - maxSpeedForTurnRate;
        if (maxVel <= 0) {
            maxVel = 0.0;
        }
        
        // If this would lead us to travel faster than
        // our desired speed, then we move only at our desired speed.
        // This allows us to continue to turn at a high rate
        // while still allowing correct speed control.
        if (speed > maxVel) {
            speed = maxVel;
        }
        else if (speed < 0) {
            speed = 0;
        }

        Model.WheelVelocities wheelVelocities = mDifferentialDriveModel.
                calculateWheelVelocities(
                        speed,
                        turnRate
                );

        
        mRightSpeed = wheelVelocities.getRightVelocity();
        mLeftSpeed = wheelVelocities.getLeftVelocity();
        
        mLogger.log(Level.INFO, "Speeds: " + mLeftSpeed + ":" + mRightSpeed);
        
        mLeft.setPosition(mLeftSpeed);
        mRight.setPosition(mRightSpeed);
    }
    
}
