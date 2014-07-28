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

package org.ensor.algorithms.control.pid;

/**
 * This class represents a PID controller which is responsible
 * for calculating the appropriate process control output in order
 * to maintain a system at a given setpoint.  This control system relies on
 * having a sensor to faithfully measure the current value and a way to
 * influence the system by expressing an output.  This class uses
 * the PID control theory to calculate an appropriate output value based
 * on the error with coefficients which are proportional to the error,
 * integral of the error (sum of errors) and the differential error
 * (rate of error change).  The coefficients must be obtained externally
 * through a tuning process.
 * @author jona
 */
public class PIDController implements IServo {

    private double mKi;
    private double mKp;
    private double mKd;
    private double mMaxOutput;
    private double mMinOutput;
    private double mSetpoint;

    private final ISensor mSensor;
    private final IServo mController;

    private double mEIntegral;
    private double mELast;

    /**
     * The constructor creates a new PID controller
     * based on the given measurement sensor, the given
     * control output, and the given PID coefficients.  No limits
     * are established for the minimum and maximum output values
     * apart from the minimum and maximum values for a 'double' value.
     * @param aSensor A sensor input to read the current state of the system.
     * @param aController A control output to affect the state of the system.
     * @param aKp A constant to multiply with the error to obtain an output.
     * @param aKi A constant to multiply with the integral of error to obtain
     *            an output.
     * @param aKd A constant to multiply with the differential of error to
     *            obtain an output.
     */
    public PIDController(
            final ISensor aSensor,
            final IServo aController,
            final double aKp,
            final double aKi,
            final double aKd
    ) {
        this(aSensor, aController, aKp, aKi, aKd,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /**
     * The constructor creates a new PID controller
     * based on the given measurement sensor, the given
     * control output, and the given PID coefficients.  The minimum
     * and maximum range of the output is also given in order to allow
     * limiting the output to prevent damage to the system.
     * @param aSensor A sensor input to read the current state of the system.
     * @param aController A control output to affect the state of the system.
     * @param aKp A constant to multiply with the error to obtain an output.
     * @param aKi A constant to multiply with the integral of error to obtain
     *            an output.
     * @param aKd A constant to multiply with the differential of error to
     *            obtain an output.
     * @param aMinOutput The minimum allowed output.
     * @param aMaxOutput The maximum allowed output.
     */
    public PIDController(
            final ISensor aSensor,
            final IServo aController,
            final double aKp,
            final double aKi,
            final double aKd,
            final double aMinOutput,
            final double aMaxOutput
    ) {
        mSensor = aSensor;
        mController =  aController;
        mKp = aKp;
        mKi = aKi;
        mKp = aKd;
        mMinOutput = aMinOutput;
        mMaxOutput = aMaxOutput;
    }

    /**
     * This method allows setting the PID constants for the control system
     * to facilitate the tuning process.  Note that any wound-up integral
     * component is reset when the coefficients are changed.
     *
     * @param aKp A constant to multiply with the error to obtain an output.
     * @param aKi A constant to multiply with the integral of error to obtain
     *            an output.
     * @param aKd A constant to multiply with the differential of error to
     *            obtain an output.
     */
    public void setCoefficients(
            final double aKp,
            final double aKi,
            final double aKd) {
        mKp = aKp;
        mKi = aKi;
        mKd = aKd;
        mEIntegral = 0;
    }

    /**
     * This method sets the minimum and maximum allowed outputs for the
     * system.
     *
     * @param aMinOutput The minimum value of the output the system is allowed
     *                   to produce.
     * @param aMaxOutput The maximum value of output the system is allowed to
     *                   produce.
     */
    public void setRange(
            final double aMinOutput,
            final double aMaxOutput) {
        mMinOutput = aMinOutput;
        mMaxOutput = aMaxOutput;
    }

    /**
     * This method sets the desired set-point of the PID controller.
     * @param aPosition The desired position of the system.
     */
    public void setPosition(final double aPosition) {
        mSetpoint = aPosition;
    }

    /**
     * This method returns the integral PID constant, Ki.
     * @return The integral coefficient of the PID controller.
     */
    public double getIntegral() {
        return mKi;
    }
    /**
     * This method returns the proportional component of the PID controller.
     * @return The proportional constant.
     */
    public double getProportional() {
        return mKp;
    }
    /**
     * This method returns the differential component of the PID controller.
     * @return The differential constant.
     */
    public double getDifferential() {
        return mKd;
    }

    /**
     * The PID controller must execute the 'tick' loop periodically in order
     * to measure the integral and differential components of the error
     * and to update the output accordingly.  This should be executed inside
     * a simulation tick loop in order to correctly control a system using the
     * PID simulator.
     * @param dt The amount of time elapsed since the last simulation loop.
     */
    public void tick(final double dt) {

        double actualPosition = mSensor.getValue();

        double e = mSetpoint - actualPosition;

        double up = e * mKp;

        // If we're already fully saturated
        // at the output based on the proportional
        // control, don't bother with the integral
        // or differential components.  This prevents
        // the problem commonly known as "integral wind-up".
        if (up >= mMaxOutput) {
            mController.setPosition(mMaxOutput);
            mEIntegral = 0;
        }
        else if (up <= mMinOutput) {
            mController.setPosition(mMinOutput);
            mEIntegral = 0;
        }
        else {
            // In the intermediate range, apply the
            // integral and differential also.
            // It is in this range that we begin to wind up the integral
            // component.

            double eDifferential = mELast - e;

            double ui = mEIntegral * mKi;
            double ud = eDifferential * mKd;

            double output = up + ui + ud;

            output = Math.min(output, mMaxOutput);
            output = Math.max(output, mMinOutput);

            mController.setPosition(output);

            mEIntegral += (dt * e);
        }

        mELast = e;

    }
}
