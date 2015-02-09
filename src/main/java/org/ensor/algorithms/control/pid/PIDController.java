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

import java.util.logging.Level;
import java.util.logging.Logger;

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
public class PIDController implements IRegulator<Double, Double> {
    private static final Logger LOG =
            Logger.getLogger(PIDController.class.getName());
    
    private double mKi;
    private double mKp;
    private double mKd;
    private double mMaxOutput;
    private double mMinOutput;
    private double mSetpoint;
    private final String mDebugId;

    private double mEIntegral;
    private double mELast;

    /**
     * The constructor creates a new PID controller
     * based on the given measurement sensor, the given
     * control output, and the given PID coefficients.  No limits
     * are established for the minimum and maximum output values
     * apart from the minimum and maximum values for a 'double' value.
     * @param aKp A constant to multiply with the error to obtain an output.
     * @param aKi A constant to multiply with the integral of error to obtain
     *            an output.
     * @param aKd A constant to multiply with the differential of error to
     *            obtain an output.
     */
    public PIDController(
            final double aKp,
            final double aKi,
            final double aKd
    ) {
        this(aKp, aKi, aKd,
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, "");
    }

    /**
     * The constructor creates a new PID controller
     * based on the given measurement sensor, the given
     * control output, and the given PID coefficients.  The minimum
     * and maximum range of the output is also given in order to allow
     * limiting the output to prevent damage to the system.
     * @param aKp A constant to multiply with the error to obtain an output.
     * @param aKi A constant to multiply with the integral of error to obtain
     *            an output.
     * @param aKd A constant to multiply with the differential of error to
     *            obtain an output.
     * @param aMinOutput The minimum allowed output.
     * @param aMaxOutput The maximum allowed output.
     * @param aDebugId The ID of the controller for debug purposes.
     */
    public PIDController(
            final double aKp,
            final double aKi,
            final double aKd,
            final double aMinOutput,
            final double aMaxOutput,
            final String aDebugId
    ) {
        mKp = aKp;
        mKi = aKi;
        mKd = aKd;
        mMinOutput = aMinOutput;
        mMaxOutput = aMaxOutput;
        mDebugId = aDebugId;
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
     * This method is used to reset the internal state
     * of the PID controller.  This is useful for resetting any
     * integral windup which may have accumulated during a time when
     * the 'tick' method was not called (for instance if the control of
     * this controller is suspended for any length of time).
     */
    public void reset() {
        mELast = 0;
        mEIntegral = 0;
    }
    
    /**
     * The PID controller must execute the 'tick' loop periodically in order
     * to measure the integral and differential components of the error
     * and to update the output accordingly.  This should be executed inside
     * a simulation tick loop in order to correctly control a system using the
     * PID simulator.
     * @param dt The amount of time elapsed since the last simulation loop.
     */
    public void tick(
            final double dt,
            final ISensor<Double> aSensor,
            final IController<Double> aController) {

        double actualPosition = aSensor.getValue();

        double e = actualPosition - mSetpoint;

        double up = e * mKp;

        double eDifferential = (mELast - e) / dt;

        double ui = mEIntegral * mKi;
        double ud = eDifferential * mKd;

        double output = up + ui + ud;
        LOG.log(Level.INFO, mDebugId + " p = " + mKp + " i = " + mKi + " d = " + mKd);
        LOG.log(Level.INFO, mDebugId + " : error : " + e + " output : " + output);
        
        // If we're already fully saturated
        // at the output based on the proportional
        // control, don't bother with the integral
        // or differential components.  This prevents
        // the problem commonly known as "integral wind-up".
        if (output >= mMaxOutput) {
            LOG.log(Level.INFO, mDebugId + " : max output");
            aController.setOutput(mMaxOutput);
        }
        else if (output <= mMinOutput) {
            LOG.log(Level.INFO, mDebugId + " : min output");
            aController.setOutput(mMinOutput);
        }
        else {
            // In the intermediate range, apply the
            // integral and differential also.
            // It is in this range that we begin to wind up the integral
            // component.
            aController.setOutput(output);
            mEIntegral += (dt * e);
        }

        mELast = e;
    }

    /**
     * This method establishes the PID parameters for the controller.
     *
     * @param aKp A constant to multiply with the error to obtain an output.
     * @param aKi A constant to multiply with the integral of error to obtain
     *            an output.
     * @param aKd A constant to multiply with the differential of error to
     *            obtain an output.
     * @param aMinOutput The minimum value of the output the system is allowed
     *                   to produce.
     * @param aMaxOutput The maximum value of output the system is allowed to
     *                   produce.
     */
    public void setPID(
            final double aKp,
            final double aKi,
            final double aKd,
            final double aMinOutput,
            final double aMaxOutput) {
        mKp = aKp;
        mKi = aKi;
        mKd = aKd;
        mEIntegral = 0;
        mMinOutput = aMinOutput;
        mMaxOutput = aMaxOutput;
    }

}
