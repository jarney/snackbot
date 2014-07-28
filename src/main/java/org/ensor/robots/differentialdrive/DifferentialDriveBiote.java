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

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ensor.data.atom.DictionaryAtom;
import org.ensor.math.geometry.Vector2;
import org.ensor.robots.motors.ComponentManager;
import org.ensor.robots.motors.ICurrentMeasurable;
import org.ensor.robots.motors.IEncoder;
import org.ensor.robots.motors.IMotor;
import org.ensor.algorithms.control.pid.IServo;
import org.ensor.robots.network.server.BioteSocket;
import org.ensor.robots.os.configuration.Configuration;
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
public class DifferentialDriveBiote extends Biote implements IGoalReachedListener {

    public static final String CONFIG_LEFT_MOTOR_ID = "leftMotorId";
    public static final String CONFIG_RIGHT_MOTOR_ID = "rightMotorId";
    public static final String CONFIG_LEFT_ENCODER_TICKS_PER_REVOLUTION = "leftEncoderTicksPerRevolution";
    public static final String CONFIG_RIGHT_ENCODER_TICKS_PER_REVOLUTION = "rightEncoderTicksPerRevolution";
    public static final String CONFIG_LEFT_WHEEL_DIAMETER = "leftWheelDiameter";
    public static final String CONFIG_RIGHT_WHEEL_DIAMETER = "rightWheelDiameter";
    public static final String CONFIG_LEFT_ENCODER_CALIBRATION_TICKS_PER_METER = "leftEncoderCalibrationTicks";
    public static final String CONFIG_RIGHT_ENCODER_CALIBRATION_TICKS_PER_METER = "rightEncoderCalibrationTicks";
    public static final String CONFIG_WHEEL_DISTANCE = "wheelDistance";
    public static final String CONFIG_LEFT_WHEEL_MAX_ROTATION_SPEED = "leftWheelMaxRotationSpeed";
    public static final String CONFIG_RIGHT_WHEEL_MAX_ROTATION_SPEED = "rightWheelMaxRotationSpeed";
    public static final String CONFIG_DISTANCE_TOLERANCE = "distanceTolerance";
    public static final String CONFIG_ANGLE_TOLERANCE = "angleTolerance";
    public static final String CONFIG_DECELERATION_DISTANCE = "decelerationDistance";
    public static final String CONFIG_LEFT_WHEEL_DIRECTION = "leftWheelDirection";
    public static final String CONFIG_RIGHT_WHEEL_DIRECTION = "rightWheelDirection";

    private final Configuration mConfiguration;
    private DictionaryAtom mConfigDict;

    // Speed controllers for left and right motors,
    // respectively.
    private IMotor mLeftMotor;
    private IMotor mRightMotor;
    private IEncoder mLeftEncoder;
    private IEncoder mRightEncoder;
    private PrintStream mJourneyLog;
    private int mTimerId;
    
    private long mLeftPosition;
    private long mRightPosition;
    private long mLastUpdateTime;
    private Vector2 mPosition;
    private double mDirection;

    private long mBioteId;
    
    private double mLeftEncoderUnitsPerMeter;
    private double mRightEncoderUnitsPerMeter;
    private double mLeftWheelDirection;
    private double mRightWheelDirection;
    private static final Event TICK_EVENT = new Event("Mover-Tick");
    private static final int TICK_DURATION_MILLISECONDS = 100;
    private RoboClaw mRoboClaw;
    private ICurrentMeasurable mM1;
    private ICurrentMeasurable mM2;
    
    private Model mSimpleModel;
    private PathServo mPathServo;
    private DifferentialDriveServo mDifferentialDrive;
    private WheelSpeedControl mLeftSpeedControl;
    private WheelSpeedControl mRightSpeedControl;
    private String leftMotorId;
    private String rightMotorId;
    private final RingBuffer<DictionaryAtom> mLoggingRingBuffer;

    class WheelSpeedControl implements IServo {
        private final double mPulsesPerMeter;
        private final IServo mSpeedControl;
        private final String mLogPrefix;
        
        public WheelSpeedControl(
                final double aPulsesPerMeter,
                final IServo aSpeedControl,
                final String aLogPrefix) {
            mSpeedControl = aSpeedControl;
            mPulsesPerMeter = aPulsesPerMeter;
            mLogPrefix = aLogPrefix;
        }
        
        public void setPosition(double aPosition) {
            mSpeedControl.setPosition(aPosition * mPulsesPerMeter);
            long spd = (long) (aPosition * mPulsesPerMeter);
            Logger.getLogger(BioteSocket.class.getName()).log(
                    Level.INFO,
                    mLogPrefix + " = " + aPosition + ":" + spd);
        }
    
    }
    
    
    public DifferentialDriveBiote(
            final BioteManager aBioteManager,
            final Configuration aConfiguration) {
        super(aBioteManager, false);
        mConfiguration = aConfiguration;
        
        mLoggingRingBuffer = new RingBuffer<DictionaryAtom>(100);
        
        DictionaryAtom config = 
            mConfiguration.getConfigurationNode(
                "org.ensor.robots.pathfollower.DifferentialDriveBiote");
        if (config == null) {
            config = DictionaryAtom.newAtom();

            
            config.setString(CONFIG_LEFT_MOTOR_ID, "roboclaw-0-motor1");
            config.setString(CONFIG_RIGHT_MOTOR_ID, "roboclaw-0-motor0");
            
            config.setReal(CONFIG_LEFT_ENCODER_TICKS_PER_REVOLUTION, 1200.0);
            config.setReal(CONFIG_RIGHT_ENCODER_TICKS_PER_REVOLUTION, 1200.0);
            config.setReal(CONFIG_LEFT_WHEEL_DIAMETER, 0.09);
            config.setReal(CONFIG_RIGHT_WHEEL_DIAMETER, 0.09);
            config.setReal(CONFIG_LEFT_ENCODER_CALIBRATION_TICKS_PER_METER, 0);
            config.setReal(CONFIG_RIGHT_ENCODER_CALIBRATION_TICKS_PER_METER, 0);
            
            config.setReal(CONFIG_WHEEL_DISTANCE, 0.36195);
            config.setReal(CONFIG_LEFT_WHEEL_MAX_ROTATION_SPEED, 500);
            config.setReal(CONFIG_RIGHT_WHEEL_MAX_ROTATION_SPEED, 500);
            config.setReal(CONFIG_DISTANCE_TOLERANCE, 0.05);
            config.setReal(CONFIG_ANGLE_TOLERANCE, 10);
            config.setReal(CONFIG_DECELERATION_DISTANCE, 0.5);
            config.setReal(CONFIG_LEFT_WHEEL_DIRECTION, 1);
            config.setReal(CONFIG_RIGHT_WHEEL_DIRECTION, 1);
            
            mConfiguration.setConfigurationNode(
                    "org.ensor.robots.pathfollower.DifferentialDriveBiote",
                    config);
        }
        
        mConfigDict = config;

        
    }

    @Override
    protected void onInit(final Event message) throws Exception {
        System.out.println("Differential Drive Biote ID: " + this.getBioteId());
        
        subscribe("Mover-MoveRequest", new IEventHandler() {
            public void process(final Event msg) throws Exception {
                onMoveRequest(msg);
            }
        });
        subscribe("Mover-SetDestinationPoint", new IEventHandler() {
            public void process(final Event msg) throws Exception {
                onSetDestinationPoint(msg);
            }
        });
        subscribe("Mover-Reset", new IEventHandler() {
            public void process (final Event msg) throws Exception {
                onReset(msg);
            }
        });
        subscribe("Mover-AllStop", new IEventHandler() {
            public void process(final Event msg) throws Exception {
                onAllStop(msg);
            }
        });
        subscribe("Mover-Tick", new IEventHandler() {
            public void process(Event msg) throws Exception {
                onTick(msg);
            }
        });
        subscribe("Mover-Subscribe", new IEventHandler() {
            public void process(Event msg) throws Exception {
                onSubscribe(msg);
            }
        });
        subscribe("Mover-DriveMotor", new IEventHandler() {
            public void process(Event msg) throws Exception {
                onDriveMotor(msg);
            }
        });
        subscribe("Mover-UpdateConfig", new IEventHandler() {
            public void process(Event msg) throws Exception {
                onConfigure(msg);
            }
        });
        
        
        mJourneyLog = new PrintStream(new FileOutputStream("server-current/html/journey-log.txt"));

        mTimerId = startTimer(TICK_DURATION_MILLISECONDS, TICK_EVENT, true);
        
        readConfiguration(mConfigDict);

        mPosition = new Vector2(0, 0);
        mDirection = 0;
        mBioteId = 0;
        
    }

    private void readConfiguration(DictionaryAtom aConfigDict) {
        // Wheel distance (in meters)
        
        mLeftEncoderUnitsPerMeter =
                aConfigDict.getReal(CONFIG_LEFT_ENCODER_TICKS_PER_REVOLUTION) /
                (aConfigDict.getReal(CONFIG_LEFT_WHEEL_DIAMETER) * Math.PI) + 
                aConfigDict.getReal(CONFIG_LEFT_ENCODER_CALIBRATION_TICKS_PER_METER);
        
        mRightEncoderUnitsPerMeter =
                aConfigDict.getReal(CONFIG_RIGHT_ENCODER_TICKS_PER_REVOLUTION) /
                (aConfigDict.getReal(CONFIG_RIGHT_WHEEL_DIAMETER) * Math.PI) + 
                aConfigDict.getReal(CONFIG_RIGHT_ENCODER_CALIBRATION_TICKS_PER_METER);

        double leftMaxRPM = aConfigDict.getReal(CONFIG_LEFT_WHEEL_MAX_ROTATION_SPEED);
        double rightMaxRPM = aConfigDict.getReal(CONFIG_RIGHT_WHEEL_MAX_ROTATION_SPEED);

        // rev/min * (1/60 min/sec) * PI*Diameter meters/rev = meters/sec
        double leftMaxSpeed = leftMaxRPM / 60.0 *
                aConfigDict.getReal(CONFIG_LEFT_WHEEL_DIAMETER) * Math.PI;
        double rightMaxSpeed = rightMaxRPM / 60.0 *
                aConfigDict.getReal(CONFIG_RIGHT_WHEEL_DIAMETER) * Math.PI;
        
        double maxMovementSpeed = (leftMaxSpeed + rightMaxSpeed)/2;
        
        double wheelDistance = aConfigDict.getReal(CONFIG_WHEEL_DISTANCE);
        double distanceTolerance = aConfigDict.getReal(CONFIG_DISTANCE_TOLERANCE);
        double angleTolerance = aConfigDict.getReal(CONFIG_ANGLE_TOLERANCE)
                * Math.PI * 2 / 360.0;
        double decelerationDistance = aConfigDict.getReal(CONFIG_DECELERATION_DISTANCE);

        mLeftWheelDirection = aConfigDict.getReal(CONFIG_LEFT_WHEEL_DIRECTION);
        mRightWheelDirection = aConfigDict.getReal(CONFIG_RIGHT_WHEEL_DIRECTION);
        mLeftEncoderUnitsPerMeter *= mLeftWheelDirection;
        mRightEncoderUnitsPerMeter *= mRightWheelDirection;
        
        String leftMotorId = aConfigDict.getString(CONFIG_LEFT_MOTOR_ID);
        String rightMotorId = aConfigDict.getString(CONFIG_RIGHT_MOTOR_ID);
        
        IServo leftSpeedControl = ComponentManager.
                getComponent(leftMotorId).getSpeedServo();
        IServo rightSpeedControl = ComponentManager.
                getComponent(rightMotorId).getSpeedServo();
        
        mRoboClaw = (RoboClaw) ComponentManager.getComponent("roboclaw-0");
        mM1 = ComponentManager.getComponent("roboclaw-0-motor0").getElectricalMonitor();
        mM2 = ComponentManager.getComponent("roboclaw-0-motor1").getElectricalMonitor();
        
        mLeftMotor = ComponentManager.
                getComponent(leftMotorId).getMotorInterface();
        mRightMotor = ComponentManager.
                getComponent(rightMotorId).getMotorInterface();

        mLeftEncoder = ComponentManager.
                getComponent(leftMotorId).getEncoder();
        mRightEncoder = ComponentManager.
                getComponent(rightMotorId).getEncoder();
        
        mLeftSpeedControl = new WheelSpeedControl(mLeftEncoderUnitsPerMeter,
                leftSpeedControl, "left");
        mRightSpeedControl = new WheelSpeedControl(mRightEncoderUnitsPerMeter,
                rightSpeedControl, "right");

        if (mSimpleModel == null) {
            mSimpleModel = new Model(wheelDistance);
        }
        else {
            mSimpleModel.setWheelDistance(wheelDistance);
        }

        if (mDifferentialDrive == null) {
            mDifferentialDrive = new DifferentialDriveServo(
                    mSimpleModel,
                    maxMovementSpeed,
                    mLeftSpeedControl,
                    mRightSpeedControl);
        }
        else {
            mDifferentialDrive.setMaxMovementSpeed(maxMovementSpeed);
        }

        if (mPathServo == null) {
            mPathServo = new PathServo(
                maxMovementSpeed,
                distanceTolerance,
                angleTolerance,
                decelerationDistance,
                mDifferentialDrive.getSpeedControl(),
                mDifferentialDrive.getAngleControl(),
                this);
        }
        else {
            mPathServo.setMaxMovementSpeed(maxMovementSpeed);
            mPathServo.setDistanceTolerance(distanceTolerance);
            mPathServo.setAngleTolerance(angleTolerance);
        }
        mConfigDict = aConfigDict;
        mConfiguration.setConfigurationNode(
                "org.ensor.robots.pathfollower.DifferentialDriveBiote",
                mConfigDict);
    }
    
    private void onConfigure(Event msg) throws Exception {
        String saveResult = "Saved OK";
        try {
            DictionaryAtom configDict =
                    msg.getData().getMutable();
            readConfiguration(configDict);
            mConfiguration.save();
        }
        catch (Exception ex) {
            saveResult = "Exception saving results";
            Logger.getLogger(BioteSocket.class.getName()).log(Level.WARNING,
                    "Exception saving configuration", ex);
        }
        DictionaryAtom dict = DictionaryAtom.newAtom();
        dict.setString("eventName", "updateConfigurationDone");
        dict.setString("saveResult", saveResult);
        Event positionUpdate = new Event("Net-Out", dict);
        sendStimulus(mBioteId, positionUpdate);
    }
    
    private void onSubscribe(Event msg) {
        mBioteId = msg.getData().getInt("bioteId");
        DictionaryAtom dict = DictionaryAtom.newAtom();
        dict.setString("eventName", "differential-drive-configuration");
        dict.setDictionary("configuration", mConfigDict.getImmutable());
        Event positionUpdate = new Event("Net-Out", dict);
        sendStimulus(mBioteId, positionUpdate);
    }
    
    private void onReset(Event msg) {
        mPosition = new Vector2(msg.getData().getReal("x"),
                msg.getData().getReal("y"));
        mDirection = msg.getData().getReal("theta");
        mPathServo.abort();
        mRightSpeedControl.setPosition((long) 0);
        mLeftSpeedControl.setPosition((long) 0);
        Logger.getLogger(BioteSocket.class.getName()).log(Level.INFO,
                "Path Abort");
    }
    
    private void onTick(Event msg) {
        
        long now = System.currentTimeMillis();
        long dt = (now - mLastUpdateTime);
        
        long newRightPosition = mRightEncoder.getEncoderPosition();
        long newLeftPosition = mLeftEncoder.getEncoderPosition();
        
        String errorStatus = mRoboClaw.getErrorStatus();
        double logicBattery = mRoboClaw.getLogicBatteryVoltage();
        double mainBattery = mRoboClaw.getMainBatteryVoltage();
        double temperature = mRoboClaw.getTemperature();
        double Im1 = mM1.getCurrentDraw();
        double Im2 = mM2.getCurrentDraw();
        

        // Left/right speed in ticks/sec.
        double leftSpeedCounts = (newLeftPosition - mLeftPosition) * 1000.0 / (double)dt;
        double rightSpeedCounts = (newRightPosition - mRightPosition) * 1000.0 / (double)dt;
        
        double leftSpeed = leftSpeedCounts / mLeftEncoderUnitsPerMeter;
        double rightSpeed = rightSpeedCounts / mRightEncoderUnitsPerMeter;
        
        Model.WheelVelocities wv = new Model.WheelVelocities(leftSpeed, rightSpeed);
        Model.SpeedAndTurnRate bearing = mSimpleModel.calculateBearing(wv);
        
        Vector2 distanceTraveled = new Vector2(
                bearing.getVelocity() * dt / 1000.0 * Math.cos(mDirection),
                bearing.getVelocity() * dt / 1000.0 * Math.sin(mDirection)
        );
        mPosition = mPosition.add(distanceTraveled);
        mDirection = mDirection + bearing.getTurnRate() * dt / 1000.0;

        mPathServo.setCurrentPosition(mPosition, mDirection);
        mPathServo.tick();
        if (mPathServo.isMoving()) {
            mDifferentialDrive.setAngle(mDirection);
            mDifferentialDrive.tick();
        }
        
        DictionaryAtom logData = DictionaryAtom.newAtom();
        logData.setInt("time", now);
        logData.setString("error_status", errorStatus);
        logData.setReal("logic_battery", logicBattery);
        logData.setReal("main_battery", mainBattery);
        logData.setReal("temperature", temperature);
        logData.setReal("current_m1", Im1);
        logData.setReal("current_m2", Im2);
        logData.setReal("leftSpeed", leftSpeed);
        logData.setReal("rightSpeed", rightSpeed);
        logData.setReal("leftSpeedSetpoint", mDifferentialDrive.getLeftSpeed());
        logData.setReal("rightSpeedSetpoint", mDifferentialDrive.getRightSpeed());
        mLoggingRingBuffer.add(logData);
        
        if (errorStatus.length() != 0) {
            for (DictionaryAtom d : mLoggingRingBuffer) {
                Logger.getLogger(BioteSocket.class.getName()).log(
                        Level.INFO,
                        "Error condition:" + 
                                d.getInt("time") +
                                ": " + d.getString("error_status") + 
                                ": Vlogic = " + d.getReal("logic_battery") + 
                                ": Vmain = " + d.getReal("main_battery") + 
                                ": temp = " + d.getReal("temperature") + 
                                ": Im1 = " + d.getReal("current_m1") + 
                                ": Im2 = " + d.getReal("current_m2") + 
                                ": vl = " + d.getReal("leftSpeed") + 
                                ": vr = " + d.getReal("rightSpeed") +
                                ": svl = " + d.getReal("leftSpeedSetpoint") + 
                                ": svr = " + d.getReal("rightSpeedSetpoint"));
            }
            mLoggingRingBuffer.clear();
        }

        if (mRightPosition != newRightPosition ||
                mLeftPosition != newLeftPosition) {
            
            Logger.getLogger(BioteSocket.class.getName()).log(
                    Level.INFO,
                    "bearing: v = " + bearing.getVelocity() + " d = " + mDirection);
        
            Logger.getLogger(BioteSocket.class.getName()).log(
                    Level.INFO,
                    "leftSpeed = " + leftSpeedCounts + " rightSpeed = " + rightSpeedCounts + 
                    " leftSpeed = " + leftSpeed + " rightSpeed = " + rightSpeed);
        
        
            Logger.getLogger(BioteSocket.class.getName()).log(Level.INFO,
                    "Distance traveled " +
                            distanceTraveled.getX() + "," + distanceTraveled.getY());
        
            Logger.getLogger(BioteSocket.class.getName()).log(
                    Level.INFO,
                    "dt = " + dt +
                    " r = " + newRightPosition +
                    " l = " + newLeftPosition +
                    " x = " + mPosition.getX() +
                    " y = " + mPosition.getY() +
                    " theta = " + mDirection);
            if (mBioteId != 0) {
                
                DictionaryAtom dict = DictionaryAtom.newAtom();
                dict.setString("eventName", "position-update");
                dict.setInt("time", System.currentTimeMillis());
                dict.setReal("x", mPosition.getX());
                dict.setReal("y", mPosition.getY());
                dict.setReal("angle", mDirection);
                dict.setReal("v", bearing.getVelocity());
                dict.setReal("leftSpeed", leftSpeed);
                dict.setReal("rightSpeed", rightSpeed);
                Event positionUpdate = new Event("Net-Out", dict);
                sendStimulus(mBioteId, positionUpdate);
                
            }

            
        }
        else {
            mJourneyLog.flush();
        }
        
        mLastUpdateTime = now;
        mRightPosition = newRightPosition;
        mLeftPosition = newLeftPosition;
    }
    
    private void onMoveRequest(final Event aEvent) {
        Logger.getLogger(BioteSocket.class.getName()).log(Level.INFO,
                "Processing move request " +
                aEvent.getData().getReal("leftMotor") +
                " " +
                aEvent.getData().getReal("rightMotor"));
        
        // Finally, we ask the motors to execute on that speed.
        
        double left = aEvent.getData().getReal("leftMotor");
        double right = aEvent.getData().getReal("rightMotor");

        mLeftSpeedControl.setPosition(left * mDifferentialDrive.getMaxMovementSpeed());
        mRightSpeedControl.setPosition(right * mDifferentialDrive.getMaxMovementSpeed());
    }
    
    private void onSetDestinationPoint(final Event aEvent) {
        double x = aEvent.getData().getReal("x");
        double y = aEvent.getData().getReal("y");
        double theta = aEvent.getData().getReal("theta");

        Vector2 dest = new Vector2(x, y);
        mPathServo.setDestination(dest, theta);
        
        Logger.getLogger(BioteSocket.class.getName()).log(Level.INFO,
                "Setting destination point " + x + "," + y + " theta " + theta);
        
    }

    private void onAllStop(Event aEvent) throws Exception {
        // Finally, we ask the motors to execute on that speed.
        mRightSpeedControl.setPosition((long) 0);
        mLeftSpeedControl.setPosition((long) 0);
        mLeftMotor.setDutyCycle(0);
        mRightMotor.setDutyCycle(0);
    }
    
    private void onDriveMotor(Event aEvent) throws Exception {
        mLeftMotor.setDutyCycle(aEvent.getData().getReal("leftMotor") * mLeftWheelDirection);
        mRightMotor.setDutyCycle(aEvent.getData().getReal("rightMotor") * mRightWheelDirection);
    }
    
    @Override
    protected void onFinalize(Event message) throws Exception {
        cancelTimer(mTimerId);
    }

    public void reached() {
        mRightSpeedControl.setPosition((long) 0);
        mLeftSpeedControl.setPosition((long) 0);
        Logger.getLogger(BioteSocket.class.getName()).log(
                Level.INFO, 
                "Destination Reached");
    }

}
