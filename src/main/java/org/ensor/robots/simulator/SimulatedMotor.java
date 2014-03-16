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

package org.ensor.robots.simulator;

import org.ensor.robots.motors.IComponent;
import org.ensor.robots.motors.IConfigurable;
import org.ensor.robots.motors.ICurrentMeasurable;
import org.ensor.robots.motors.IEncoder;
import org.ensor.robots.motors.IMotor;
import org.ensor.robots.motors.IServo;
import org.ensor.robots.motors.ITemperatureSensor;

/**
 * This method simulates a motor with a speed controller and
 * a quadrature encoder.  It simulates some degree of slippage
 * with respect to the desired speed and the actual amount shown
 * on the encoders meant to help simulate real-world slippage conditions.
 * @author jona
 */
class SimulatedMotor implements IComponent, IServo, IEncoder {
    private long mEncoderPosition;
    private long mDesiredSpeed;
    
    private double mAngularErrorPerSecond;
    private double mEncoderPulsesPerRevolution;
    private long mLastTime;
    
    private static final double MILLISECONDS_PER_SECOND = 1000;
    private static final double ONE_HALF = 0.5;
    
    /**
     * This constructor creates a simulated
     * motor with a speed controller and a simulated
     * encoder.
     * @param aAngularErrorPerSecond The number of radians per second
     *                               of error to introduce into the
     *                               rotation control.
     * @param aEncoderPulsesPerRevolution The number of encoder pulses
     *                                    per revolution (2pi radians).
     */
    public SimulatedMotor(
            final double aAngularErrorPerSecond,
            final long aEncoderPulsesPerRevolution) {
        
    }

    public ICurrentMeasurable getElectricalMonitor() {
        return null;
    }

    public IEncoder getEncoder() {
        return this;
    }
    
    public IMotor getMotorInterface() {
        return null;
    }

    public IConfigurable getConfigurableInterface() {
        return null;
    }

    public IServo getPositionServo() {
        return null;
    }

    public IServo getSpeedServo() {
        return this;
    }

    public ITemperatureSensor getTemperatureSensor() {
        return null;
    }

    public long getEncoderPosition() {
        long currentTime = System.currentTimeMillis();
        double dt = currentTime - mLastTime;
        dt = dt / MILLISECONDS_PER_SECOND;
        
        double randMinusHalfToHalf = Math.random() - ONE_HALF;
        double angularError = randMinusHalfToHalf * mAngularErrorPerSecond * dt;
        double randomEncoderAddition = angularError / (2 * Math.PI) *
                mEncoderPulsesPerRevolution;
                    
        
        mEncoderPosition =
                mEncoderPosition +
                    (long) ((double) mDesiredSpeed * (dt)) +
                    (long) randomEncoderAddition;
        mLastTime = currentTime;
        
        return mEncoderPosition;
    }

    public long getEncoderSpeed() {
        double randMinusHalfToHalf = Math.random() - ONE_HALF;
        double angularError = randMinusHalfToHalf * mAngularErrorPerSecond;
        long errorSpeed = (long) (
                angularError *
                2 * Math.PI *
                mEncoderPulsesPerRevolution);
        
        return mDesiredSpeed + errorSpeed;
    }

    public void setPosition(final long aPosition) {
        mDesiredSpeed = aPosition;
    }


}
