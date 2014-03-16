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
package org.ensor.robots.motors.differentialdrive;

import org.ensor.math.geometry.Vector2;
import org.ensor.robots.motors.IServo;

/**
 * This class implements an elementary model for
 * differential steering based on
 * <a href="http://rossum.sourceforge.net/papers/DiffSteer/">this paper</a>.
 *
 * @author jona
 */
public class DifferentialDrive {
    private final double mWheelSeparationMeters;
    private final double mWheelDiameterMeters;
    private final long mEncoderCountsPerRevolution;
    private final IServo mRight;
    private final IServo mLeft;

    // Distance between the wheels
    // of the tank drive.
    public DifferentialDrive(
            final double aWheelSeparationMeters,
            final double aWheelDiameterMeters,
            final long aEncoderCountsPerRevolution,
            final IServo aRight,
            final IServo aLeft) {
        mWheelSeparationMeters = aWheelSeparationMeters;
        mWheelDiameterMeters = aWheelDiameterMeters;
        mEncoderCountsPerRevolution = aEncoderCountsPerRevolution;
        mRight = aRight;
        mLeft = aLeft;
    }
    
    public void recalculateTrajectory() {
        
        // Input:
        //   velocity at the start (m/s)
        //   acceleration at the start (m/s^2)
        //   time: time between samples (dt)
        Vector2 v0 = new Vector2(1,1);
        Vector2 a = new Vector2(1,1);
        
        double dt = 0.02;
        
        // dv = v0 + adt
        Vector2 v1 = a.multiply(dt).add(v0);
        double theta0 = Math.atan2(v0.getX(), v0.getY());
        double theta1 = Math.atan2(v1.getX(), v1.getY());
        double dthetadt = (theta1 - theta0) / dt;
        
        // Velocity of the robot is equal to the
        // magnitude of the x,y velocities.
        double v = Math.sqrt(
                v0.getX() * v0.getX() +
                v0.getY() * v0.getY());

        // dtheta/dt = (vr-vl)/b
        // dtheta/dt * b = (vr - vl)
        
        // Calculate velocity of the left and right right motors.
        double vr = v - dthetadt * mWheelSeparationMeters / 2;
        double vl = v + dthetadt * mWheelSeparationMeters / 2;
        
        // Scale this to the motor itself.
        
        // convert vr (m/s) into revolutions/sec
        // of the motor.  To do this, we multiply by the
        // circumference of the wheel.
        // r/m = revolutions per meter
        // m/s * r/m = r/s
        // r/s = revolutions per second.
        double rrps = vr / Math.PI * mWheelDiameterMeters;
        double lrps = vl / Math.PI * mWheelDiameterMeters;
        
        // r/m * p/r = p/s
        // Pulses per second:
        
        long rightSpeedPulsesPerSecond =
                (long) (rrps * mEncoderCountsPerRevolution);
        long leftSpeedPulsesPerSecond =
                (long) (lrps * mEncoderCountsPerRevolution);
        
        mRight.setPosition(rightSpeedPulsesPerSecond);
        mLeft.setPosition(leftSpeedPulsesPerSecond);
        
    }
}
