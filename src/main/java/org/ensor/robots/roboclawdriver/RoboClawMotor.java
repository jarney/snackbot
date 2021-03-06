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
package org.ensor.robots.roboclawdriver;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ensor.robots.motors.IConfigurable;
import org.ensor.robots.motors.ICurrentMeasurable;
import org.ensor.robots.motors.IEncoder;
import org.ensor.robots.motors.IMotor;
import org.ensor.algorithms.control.pid.IServo;
import org.ensor.robots.motors.ITemperatureSensor;

/**
 *
 * @author jona
 */
class RoboClawMotor implements
        IMotor,
        ICurrentMeasurable,
        IEncoder
{
    private static final Logger LOGGER = Logger.getLogger(RoboClaw.class.getCanonicalName());
    private final RoboClaw mMotorController;
    private final int mMotorId;
    private double mCurrentDraw;
    private int mPConstant;
    private int mIConstant;
    private int mDConstant;
    private int mQPPSConstant;
    private int mMaxPos;
    private int mMinPos;
    private boolean mAbsoluteEncoder;
    private boolean mSupportRCEncoders;
    private byte mBufferLength;
    private double mDutyCycle;

    private final SpeedServo mSpeedServo;

    class SpeedServo implements IServo {

        private final RoboClawMotor mMotor;
        public SpeedServo(RoboClawMotor aMotor) {
            mMotor = aMotor;
        }
        
        public void setPosition(final double aPosition) {
            mMotorController.handleCommand(
                new CommandDriveMotorWithSignedSpeed(
                        mMotorId,
                        (long) aPosition)
            );
        }
        
        public void setPID(final double P, final double I, final double D, final double aMinError, double aMaxError) {
            mMotorController.handleCommand(
                new CommandSetPIDQPPSConstants(
                    mMotorId,
                    (int)(P * 65536.0),
                    (int)(I * 65536.0),
                    (int)(D * 65536.0),
                    (int)aMaxError
                )
            );
            mMotorController.handleCommand(
                new CommandReadPIDQPPS(mMotor));
        }
        
    }
    
    RoboClawMotor(
            final RoboClaw aThis,
            final int aMotorId) {
        mMotorController = aThis;
        mMotorId = aMotorId;
        mAbsoluteEncoder = false;
        mSupportRCEncoders = false;
        mBufferLength = 0;
        mSpeedServo = new SpeedServo(this);
        
//        mMotorController.handleCommand(
//            new CommandSetPIDQPPSConstants(
//            mMotorId,
//            0x00010000,
//            0x00008000,
//            0x00004000,
//            44000)
//        );
//
        mMotorController.handleCommand(
            new CommandReadPIDQPPS(this));

        LOGGER.log(Level.SEVERE,
                "Motor id : p = " + mPConstant +
                        " i = " + mIConstant +
                        " d = " + mDConstant +
                        " qpps " + mQPPSConstant);

    }


    
    public IMotor getMotorInterface() {
        return this;
    }

    public IConfigurable getConfigurableInterface() {
        return null;
    }

    public IServo getPositionServo() {
        return null;
    }

    public IServo getSpeedServo() {
        return mSpeedServo;
    }

    public ITemperatureSensor getTemperatureSensor() {
        return null;
    }
    
    public ICurrentMeasurable getElectricalMonitor() {
        return this;
    }

    public IEncoder getEncoder() {
        return this;
    }
            
    
    public int getPConstant() {
        return mPConstant;
    }
    
    public int getIConstant() {
        return mIConstant;
    }
    
    public int getDConstant() {
        return mDConstant;
    }
    
    public int getQPPSConstant() {
        return mQPPSConstant;
    }
    
    public int getMinPos() {
        return mMinPos;
    }
    
    public int getMaxPos() {
        return mMaxPos;
    }
    
    protected void setQPPSInternal(final int aQPPSConstant) {
        mQPPSConstant = aQPPSConstant;
    }

    protected void setMinMaxPosInternal(
            final int minPos,
            final int maxPos) {
        mMinPos = minPos;
        mMaxPos = maxPos;
    }
    
    protected void setPIDConstantsInternal(
            final int aPConstant,
            final int aIConstant,
            final int aDConstant
    ) {
        mPConstant = aPConstant;
        mIConstant = aIConstant;
        mDConstant = aDConstant;
    }
    
    public int getMotorId() {
        return mMotorId;
    }

    public void setDutyCycle(final double aDutyCycle) {
        mDutyCycle = aDutyCycle;
        mMotorController.handleCommand(
                new CommandDriveMotorWithDutyCycle(mMotorId, mDutyCycle)
        );
    }

    public double getDutyCycle() {
        return mDutyCycle;
    }
    
    protected void setCurrentDraw(final double aCurrentDraw) {
        mCurrentDraw = aCurrentDraw;
    }
    
    public double getCurrentDraw() {
        return mCurrentDraw;
    }

    public double getVoltage() {
        return mMotorController.getMainBatteryVoltage();
    }

    protected void setEncoderMode(byte b) {
        mAbsoluteEncoder = (b & 0x01) != 0;
        mSupportRCEncoders = (b & 0x80) != 0;
    }
    
    public boolean getEncoderQuatratureMode() {
        return !mAbsoluteEncoder;
    }
    public boolean getRCEncoderSupported() {
        return mSupportRCEncoders;
    }

    /**
     * This method returns the actual speed of the motor in
     * terms of 'pulses-per-second'.
     *
     * @return The number of pulses per second.
     */
    public long getEncoderSpeed() {
        long [] encoderSpeed = new long[1];
        mMotorController.handleCommand(
                new CommandReadEncoderSpeed(mMotorId, encoderSpeed));
        return encoderSpeed[0];
    }

    void setBufferLength(final byte b) {
        mBufferLength = b;
    }
    
    public int getBufferLength() {
        return mBufferLength;
    }

    /**
     * This method calls the RoboClaw to obtain the quadrature encoder
     * position and returns the number of encoder pulses.
     *
     * @return The count of pulses since the last reset of the quadrature
     *         encoder for this motor.
     */
    public long getEncoderPosition() {
        long [] encoderPosition = new long[2];
        mMotorController.handleCommand(
                new CommandReadEncoderPosition(mMotorId, encoderPosition));
        return encoderPosition[0];
    }
    
}
