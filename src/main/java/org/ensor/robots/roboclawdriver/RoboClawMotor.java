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

import org.ensor.robots.motors.IComponent;
import org.ensor.robots.motors.IConfigurable;
import org.ensor.robots.motors.IMotor;
import org.ensor.robots.motors.IMotorWithEncoder;
import org.ensor.robots.motors.IServo;
import org.ensor.robots.motors.ITemperatureSensor;

/**
 *
 * @author jona
 */
class RoboClawMotor implements
        IComponent,
        IMotorWithEncoder {

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
    private long mActualSpeed;
    private byte mBufferLength;
    private long mEncoderValue;
    private byte mEncoderStatus;
    private long mEncoderLastReadTime;
    
    
    public IMotor getMotorInterface() {
        return this;
    }

    public IMotorWithEncoder getMotorWithEncoderInterface() {
        return this;
    }

    public IConfigurable getConfigurableInterface() {
        return null;
    }

    public IServo getPositionServo() {
        return null;
    }

    public IServo getSpeedServo() {
        return null;
    }

    public ITemperatureSensor getTemperatureSensor() {
        return null;
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
    
    protected void setQPPSInternal(int aQPPSConstant) {
        mQPPSConstant = aQPPSConstant;
    }

    protected void setMinMaxPosInternal(int minPos, int maxPos) {
        mMinPos = minPos;
        mMaxPos = maxPos;
    }
    
    protected void setPIDConstantsInternal(
            int aPConstant,
            int aIConstant,
            int aDConstant
    ) {
        mPConstant = aPConstant;
        mIConstant = aIConstant;
        mDConstant = aDConstant;
    }
    
    public int getMotorId() {
        return mMotorId;
    }
    
    RoboClawMotor(RoboClaw aThis, int aMotorId) {
        mMotorController = aThis;
        mMotorId = aMotorId;
        mAbsoluteEncoder = false;
        mSupportRCEncoders = false;
        mBufferLength = 0;
    }

    public void setDutyCycle(double aDutyCycle) {
        byte speed;
        double dutyCycle;
        boolean forward;
        forward = aDutyCycle > 0;
        
        dutyCycle = Math.abs(aDutyCycle);
        if (dutyCycle > 1.0) {
            dutyCycle = 1.0;
        }
        speed = (byte)((double)(dutyCycle * 127.0));
        Command cmd;
        cmd = new CommandDriveMotor(speed, forward, mMotorId);
        
        mMotorController.handleCommand(cmd);
    }

    protected void setCurrentDraw(double aCurrentDraw) {
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

    void setEncoderSpeed(long d) {
        mActualSpeed = d;
    }
    
    /**
     * This method returns the actual speed of the motor in
     * terms of 'pulses-per-second'.
     *
     * @return The number of pulses per second.
     */
    public long getEncoderSpeed() {
        return mActualSpeed;
    }

    void setBufferLength(byte b) {
        mBufferLength = b;
    }
    
    public int getBufferLength() {
        return mBufferLength;
    }

    void setQuadratureEncoderPosition(long pos, byte status, long time) {
        mEncoderValue = pos;
        mEncoderStatus = status;
        mEncoderLastReadTime = time;
    }

    public long getEncoderPosition() {
        return mEncoderValue;
    }
    
}
