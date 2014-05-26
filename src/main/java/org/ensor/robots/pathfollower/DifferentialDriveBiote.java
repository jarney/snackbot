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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ensor.robots.motors.ComponentManager;
import org.ensor.robots.motors.IMotor;
import org.ensor.robots.motors.IServo;
import org.ensor.robots.motors.differentialdrive.SimpleModel;
import org.ensor.robots.network.server.BioteSocket;
import org.ensor.robots.roboclawdriver.RoboClaw;
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
    private final IMotor mRightMotor;
    private final IMotor mLeftMotor;
    private final RoboClaw mRoboClaw;

    private final double mLeftEncoderUnitsPerMeter;
    private final double mRightEncoderUnitsPerMeter;
    
    public DifferentialDriveBiote(final BioteManager aBioteManager) {
        super(aBioteManager, false);
        
        // All of this comes from configuration manager.
        mDifferentialDriveModel = new SimpleModel(0.36195);
        
        // To determine units per meter:
        // Meters per revolution:
        // 90/1000 * 3.14159265 = 
        // Encoder counts per revolution: 29 * 64 = 1856 counts/rev
        // counts/rev * rev/m
        
        mLeftEncoderUnitsPerMeter = 6564.4508108288;
        mRightEncoderUnitsPerMeter = 6564.4508108288;
        mRightSpeedControl = ComponentManager.
                getComponent("roboclaw-0-motor0").getSpeedServo();
        mLeftSpeedControl = ComponentManager.
                getComponent("roboclaw-0-motor1").getSpeedServo();
        mRightMotor = ComponentManager.
                getComponent("roboclaw-0-motor0").getMotorInterface();
        mLeftMotor = ComponentManager.
                getComponent("roboclaw-0-motor1").getMotorInterface();
        
        mRoboClaw = (RoboClaw)ComponentManager.getComponent("roboclaw-0");
        
    }

    @Override
    protected void onInit(final Event message) throws Exception {
        System.out.println("Differential Drive Biote ID: " + this.getBioteId());
        
        subscribe("Mover-MoveRequest", new IEventHandler() {
            public void process(final Event msg) throws Exception {
                onMoveRequest(msg);
            }
        });
        subscribe("Mover-MoveRequestLeftRight", new IEventHandler() {
            public void process(final Event msg) throws Exception {
                onMoveRequestLeftRight(msg);
            }
        });
        subscribe("Mover-AllStop", new IEventHandler() {
            public void process(final Event msg) throws Exception {
                onAllStop(msg);
            }
            
        });
    }

    private void onMoveRequestLeftRight(final Event aEvent) {
        Logger.getLogger(BioteSocket.class.getName()).log(Level.INFO, 
                "Processing move request " +
                aEvent.getData().getReal("left") + 
                " " + 
                aEvent.getData().getReal("right"));
        
        // Finally, we ask the motors to execute on that speed.
        
        double encoderVr = aEvent.getData().getReal("right") * 
                mRightEncoderUnitsPerMeter;
        double encoderVl = aEvent.getData().getReal("left") * 
                mLeftEncoderUnitsPerMeter;
        
        mRightSpeedControl.setPosition((long) encoderVr);
        mLeftSpeedControl.setPosition((long) encoderVl);

//        mRightMotor.setDutyCycle(aEvent.getData().getReal("right"));
//        mLeftMotor.setDutyCycle(aEvent.getData().getReal("left"));
        mRoboClaw.updateData();
        Logger.getLogger(BioteSocket.class.getName()).log(Level.INFO, 
                "Error status is " + mRoboClaw.getErrorStatus());
    }
    
    private void onMoveRequest(final Event aEvent) {
        double theta = aEvent.getData().getReal("direction");
        double velocity = aEvent.getData().getReal("velocity");
        double dt = aEvent.getData().getReal("delta_time");

        // We're going to move the
        // angle by this much
        double dthetadt = theta / dt;
        if (dthetadt > 10) {
            dthetadt = 10;
        }
        if (dthetadt < -10) {
            dthetadt = -10;
        }
        
        SimpleModel.WheelVelocities wheelVelocities =
                mDifferentialDriveModel.
                        calculateWheelVelocities(velocity, dthetadt);

        // Now, we have the desired wheel velocities in terms of meters
        // per second.  We need to convert them into units of
        // encoder pulses per second, so we multiply the number of
        // encoder pulses per meter.
        double encoderVr = wheelVelocities.getRightVelocity() *
                mRightEncoderUnitsPerMeter;
        double encoderVl = wheelVelocities.getLeftVelocity() *
                mLeftEncoderUnitsPerMeter;

        System.out.println("vr = " + wheelVelocities.getRightVelocity() + ": " + encoderVr);
        System.out.println("vl = " + wheelVelocities.getLeftVelocity() + ": " + encoderVl);
        
        // Finally, we ask the motors to execute on that speed.
        mRightSpeedControl.setPosition((long) encoderVr);
        mLeftSpeedControl.setPosition((long) encoderVl);
    }

    private void onAllStop(Event aEvent) throws Exception {
        // Finally, we ask the motors to execute on that speed.
        mRightSpeedControl.setPosition((long) 0);
        mLeftSpeedControl.setPosition((long) 0);
    }
    
    @Override
    protected void onFinalize(Event message) throws Exception {
    }

}
