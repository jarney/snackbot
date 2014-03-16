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

package org.ensor.robots.pathfollower;

import org.ensor.robots.motors.ComponentManager;
import org.ensor.robots.motors.IServo;
import org.ensor.robots.motors.differentialdrive.SimpleModel;
import org.ensor.threads.biote.Biote;
import org.ensor.threads.biote.BioteManager;
import org.ensor.threads.biote.Event;
import org.ensor.threads.biote.IEventHandler;

/**
 * This method implements the differential drive command system.
 * It allows an event to come in giving a desired velocity and bearing
 * change together with a duration during which to make the angle change
 * (radians per second).  It uses an elementary forward kinematics model
 * to estimate the desired velocity of each of the wheels.
 * @author jona
 */
public class DifferentialDriveBiote extends Biote {

    // Differential drive model.
    private final SimpleModel mDifferentialDriveModel;

    // Speed controllers for left and right motors,
    // respectively.
    private final IServo mLeftSpeedControl;
    private final IServo mRightSpeedControl;

    private final double mLeftEncoderUnitsPerMeter;
    private final double mRightEncoderUnitsPerMeter;
    
    public DifferentialDriveBiote(final BioteManager aBioteManager) {
        super(aBioteManager, false);
        
        // All of this comes from configuration manager.
        mDifferentialDriveModel = new SimpleModel(1.0);
        mLeftEncoderUnitsPerMeter = 2000;
        mRightEncoderUnitsPerMeter = 2000;
        mRightSpeedControl = ComponentManager.
                getComponent("roboclaw-0-motor0").getSpeedServo();
        mLeftSpeedControl = ComponentManager.
                getComponent("roboclaw-0-motor1").getSpeedServo();
    }

    @Override
    protected void onInit(final Event message) throws Exception {
        subscribe("Mover-MoveRequest", new IEventHandler() {
            public void process(final Event msg) throws Exception {
                onMoveRequest(msg);
            }
        });
    }

    private void onMoveRequest(final Event aEvent) {
        double theta = aEvent.getData().getReal("direction");
        double velocity = aEvent.getData().getReal("velocity");
        double dt = aEvent.getData().getReal("delta_time");

        // We're going to move the
        // angle by this much
        double dthetadt = theta / dt;
        SimpleModel.WheelVelocities wheelVelocities =
                mDifferentialDriveModel.
                        calculateWheelVelocities(velocity, dthetadt);

        // Now, we have the desired wheel velocities in terms of meters
        // per second.  We need to convert them into units of
        // encoder pulses per second, so we multiply the number of
        // encoder pulses per meter.
        double encoderVr = wheelVelocities.getRightVelocity() *
                mRightEncoderUnitsPerMeter;
        double encoderVl = wheelVelocities.getRightVelocity() *
                mLeftEncoderUnitsPerMeter;

        System.out.println("vr = " + encoderVr);
        System.out.println("vl = " + encoderVl);
        
        // Finally, we ask the motors to execute on that speed.
        mRightSpeedControl.setPosition((long) encoderVr);
        mLeftSpeedControl.setPosition((long) encoderVl);
    }

    @Override
    protected void onFinalize(Event message) throws Exception {
    }

}
