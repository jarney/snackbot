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

import org.ensor.algorithms.containers.RingBuffer;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ensor.algorithms.control.pid.IMover;
import org.ensor.algorithms.control.pid.ISensor;
import org.ensor.data.atom.DictionaryAtom;
import org.ensor.math.geometry.Vector2;
import org.ensor.robots.os.api.ComponentManager;
import org.ensor.robots.motors.ICurrentMeasurable;
import org.ensor.robots.motors.IEncoder;
import org.ensor.robots.motors.IMotor;
import org.ensor.algorithms.control.pid.IServo;
import org.ensor.algorithms.control.pid.PIDController;
import org.ensor.data.atom.Atom;
import org.ensor.data.atom.ImmutableDict;
import org.ensor.data.atom.ImmutableList;
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
public class DifferentialDriveBiote extends Biote {

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
    
    // PID constants for left motor.
    public static final String CONFIG_LEFT_PID_P = "leftPID_P";
    public static final String CONFIG_LEFT_PID_I = "leftPID_I";
    public static final String CONFIG_LEFT_PID_D = "leftPID_D";
    
    // PID constants for right motor.
    public static final String CONFIG_RIGHT_PID_P = "rightPID_P";
    public static final String CONFIG_RIGHT_PID_I = "rightPID_I";
    public static final String CONFIG_RIGHT_PID_D = "rightPID_D";

    // PID constants for distance->speed control.
    public static final String CONFIG_DISTANCE_PID_P = "distancePID_P";
    public static final String CONFIG_DISTANCE_PID_I = "distancePID_I";
    public static final String CONFIG_DISTANCE_PID_D = "distancePID_D";
    
    // PID constants for angle -> angle speed control.
    public static final String CONFIG_ANGLE_PID_P = "anglePID_P";
    public static final String CONFIG_ANGLE_PID_I = "anglePID_I";
    public static final String CONFIG_ANGLE_PID_D = "anglePID_D";
    
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
    
    private long mBioteId;

    private WheelSpeedSensor mLeftSpeedSensor;
    private WheelSpeedSensor mRightSpeedSensor;
    
    private WheelSpeedControl mLeftSpeedControl;
    private WheelSpeedControl mRightSpeedControl;
    
    private Odometry mOdometry;
    private double mLeftWheelDirection;
    private double mRightWheelDirection;
    
    private static final Event TICK_EVENT = new Event("Mover-Tick");
    private static final int TICK_DURATION_MILLISECONDS = 100;
    
    private RoboClaw mRoboClaw;
    private ICurrentMeasurable mM1;
    private ICurrentMeasurable mM2;
    
    private Model mSimpleModel;
    private IMover<Position, SpeedAndTurnRate> mPathServo;
    private PIDController mAngleController;
    private PIDController mDistanceController;
    private DifferentialDriveController mDifferentialDrive;
    private double mDistanceTolerance;
    private double mAngleTolerance;
    private final RingBuffer<DictionaryAtom> mLoggingRingBuffer;

    private double mLeftMaxSpeed;
    private double mRightMaxSpeed;
    
    private double mLeftEncoderTicksPerRevolution;
    private double mRightEncoderTicksPerRevolution;
    
    
    class WheelSpeedSensor implements ISensor<Double> {
        private double mEncoderUnitsPerMeter;
        private double mSpeedCounts;
        
        public WheelSpeedSensor(final double aEncoderUnitsPerMeter) {
            mEncoderUnitsPerMeter = aEncoderUnitsPerMeter;
        }
        public double getEncoderUnitsPerMeter() {
            return mEncoderUnitsPerMeter;
        }
        
        public void setEncoderUnitsPerMeter(
                final double aEncoderUnitsPerMeter
        ) {
            mEncoderUnitsPerMeter = aEncoderUnitsPerMeter;
        }
        
        
        public void setSpeedCounts(final double aSpeedCounts) {
            mSpeedCounts = aSpeedCounts;
        }
        
        public Double getValue() {
            return mSpeedCounts / mEncoderUnitsPerMeter;
        }
        
    }
    
    class WheelSpeedControl implements IServo {
        private double mEncoderUnitsPerMeter;
        private final IServo mSpeedControl;
        private final String mLogPrefix;
        
        public WheelSpeedControl(
                final double aEncoderUnitsPerMeter,
                final IServo aSpeedControl,
                final String aLogPrefix) {
            mSpeedControl = aSpeedControl;
            mEncoderUnitsPerMeter = aEncoderUnitsPerMeter;
            mLogPrefix = aLogPrefix;
        }
        
        public double getEncoderUnitsPerMeter() {
            return mEncoderUnitsPerMeter;
        }
        
        public void setPulsesPerMeter(
                final double aEncoderUnitsPerMeter) {
            mEncoderUnitsPerMeter = aEncoderUnitsPerMeter;
        }
        
        public void setPosition(double aPosition) {
            mSpeedControl.setPosition(aPosition * mEncoderUnitsPerMeter);
            long spd = (long) (aPosition * mEncoderUnitsPerMeter);
            Logger.getLogger(BioteSocket.class.getName()).log(
                    Level.INFO,
                    mLogPrefix + " = " + aPosition + ":" + spd);
        }

        public void setPID(double P, double I, double D, double aMinError, double aMaxError) {
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
            
            config.setReal(CONFIG_LEFT_ENCODER_TICKS_PER_REVOLUTION, 1920.0);
            config.setReal(CONFIG_RIGHT_ENCODER_TICKS_PER_REVOLUTION, 1920.0);
            config.setReal(CONFIG_LEFT_WHEEL_DIAMETER, 0.09);
            config.setReal(CONFIG_RIGHT_WHEEL_DIAMETER, 0.09);
            config.setReal(CONFIG_LEFT_ENCODER_CALIBRATION_TICKS_PER_METER, 0);
            config.setReal(CONFIG_RIGHT_ENCODER_CALIBRATION_TICKS_PER_METER, 0);
            
            config.setReal(CONFIG_WHEEL_DISTANCE, 0.36195);
            config.setReal(CONFIG_LEFT_WHEEL_MAX_ROTATION_SPEED, 350);
            config.setReal(CONFIG_RIGHT_WHEEL_MAX_ROTATION_SPEED, 350);
            config.setReal(CONFIG_DISTANCE_TOLERANCE, 0.05);
            config.setReal(CONFIG_ANGLE_TOLERANCE, 10);
            config.setReal(CONFIG_DECELERATION_DISTANCE, 0.5);
            config.setReal(CONFIG_LEFT_WHEEL_DIRECTION, 1);
            config.setReal(CONFIG_RIGHT_WHEEL_DIRECTION, 1);
            
            config.setReal(CONFIG_LEFT_PID_P, 32);
            config.setReal(CONFIG_LEFT_PID_I, 8);
            config.setReal(CONFIG_LEFT_PID_D, 0.0);
            
            config.setReal(CONFIG_RIGHT_PID_P, 32);
            config.setReal(CONFIG_RIGHT_PID_I, 8);
            config.setReal(CONFIG_RIGHT_PID_D, 0.0);
            
            config.setReal(CONFIG_DISTANCE_PID_P, 1.0);
            config.setReal(CONFIG_DISTANCE_PID_I, 0.0);
            config.setReal(CONFIG_DISTANCE_PID_D, 0.0);
            
            config.setReal(CONFIG_ANGLE_PID_P, 3.0);
            config.setReal(CONFIG_ANGLE_PID_I, 0.003);
            config.setReal(CONFIG_ANGLE_PID_D, 0.0);
            
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
        
        subscribe("Mover-Set-Speeds", new IEventHandler() {
            public void process(Event msg) throws Exception {
                onSetSpeeds(msg);
            }

        });
        
        mJourneyLog = new PrintStream(new FileOutputStream("server-current/html/journey-log.txt"));

        mTimerId = startTimer(TICK_DURATION_MILLISECONDS, TICK_EVENT, true);
        
        readConfiguration(mConfigDict);

        mBioteId = 0;
        
    }

    private void readConfiguration(DictionaryAtom aConfigDict) {
        // Wheel distance (in meters)

        mLeftEncoderTicksPerRevolution = aConfigDict.getReal(CONFIG_LEFT_ENCODER_TICKS_PER_REVOLUTION);
        mRightEncoderTicksPerRevolution = aConfigDict.getReal(CONFIG_RIGHT_ENCODER_TICKS_PER_REVOLUTION);
        
        double leftEncoderUnitsPerMeter =
                mLeftEncoderTicksPerRevolution /
                (aConfigDict.getReal(CONFIG_LEFT_WHEEL_DIAMETER) * Math.PI) + 
                aConfigDict.getReal(CONFIG_LEFT_ENCODER_CALIBRATION_TICKS_PER_METER);
        
        double rightEncoderUnitsPerMeter =
                mRightEncoderTicksPerRevolution /
                (aConfigDict.getReal(CONFIG_RIGHT_WHEEL_DIAMETER) * Math.PI) + 
                aConfigDict.getReal(CONFIG_RIGHT_ENCODER_CALIBRATION_TICKS_PER_METER);

        double leftMaxRPM = aConfigDict.getReal(CONFIG_LEFT_WHEEL_MAX_ROTATION_SPEED);
        double rightMaxRPM = aConfigDict.getReal(CONFIG_RIGHT_WHEEL_MAX_ROTATION_SPEED);

        // rev/min * (1/60 min/sec) * PI*Diameter meters/rev = meters/sec
        mLeftMaxSpeed = leftMaxRPM / 60.0 *
                aConfigDict.getReal(CONFIG_LEFT_WHEEL_DIAMETER) * Math.PI;
        mRightMaxSpeed = rightMaxRPM / 60.0 *
                aConfigDict.getReal(CONFIG_RIGHT_WHEEL_DIAMETER) * Math.PI;
        
        double maxMovementSpeed = (mLeftMaxSpeed + mRightMaxSpeed)/2;
        
        double wheelDistance = aConfigDict.getReal(CONFIG_WHEEL_DISTANCE);
        mDistanceTolerance = aConfigDict.getReal(CONFIG_DISTANCE_TOLERANCE);
        mAngleTolerance = aConfigDict.getReal(CONFIG_ANGLE_TOLERANCE)
                * Math.PI * 2 / 360.0;
        double decelerationDistance = aConfigDict.getReal(CONFIG_DECELERATION_DISTANCE);

        mLeftWheelDirection = aConfigDict.getReal(CONFIG_LEFT_WHEEL_DIRECTION);
        mRightWheelDirection = aConfigDict.getReal(CONFIG_RIGHT_WHEEL_DIRECTION);
        
        leftEncoderUnitsPerMeter *= mLeftWheelDirection;
        rightEncoderUnitsPerMeter *= mRightWheelDirection;
        
        String leftMotorId = aConfigDict.getString(CONFIG_LEFT_MOTOR_ID);
        String rightMotorId = aConfigDict.getString(CONFIG_RIGHT_MOTOR_ID);
        
        mRoboClaw = ComponentManager.getComponent("roboclaw-0",
                RoboClaw.class);
        
        IServo leftSpeedControl = ComponentManager.
                getComponent(leftMotorId + "-speed", IServo.class);
        IServo rightSpeedControl = ComponentManager.
                getComponent(rightMotorId + "-speed", IServo.class);
        
        long leftqpps = (long)(aConfigDict.getReal(CONFIG_LEFT_ENCODER_TICKS_PER_REVOLUTION) * leftMaxRPM / 60);
        long rightqpps = (long)(aConfigDict.getReal(CONFIG_RIGHT_ENCODER_TICKS_PER_REVOLUTION) * rightMaxRPM / 60);
        
        leftSpeedControl.setPID(
                aConfigDict.getReal(CONFIG_LEFT_PID_P),
                aConfigDict.getReal(CONFIG_LEFT_PID_I),
                aConfigDict.getReal(CONFIG_LEFT_PID_D),
                -leftqpps, leftqpps);
        
        rightSpeedControl.setPID(
                aConfigDict.getReal(CONFIG_RIGHT_PID_P),
                aConfigDict.getReal(CONFIG_RIGHT_PID_I),
                aConfigDict.getReal(CONFIG_RIGHT_PID_D),
                -rightqpps, rightqpps);
        
        mM1 = ComponentManager.
                getComponent(leftMotorId + "-current", ICurrentMeasurable.class);
        mM2 = ComponentManager.
                getComponent(rightMotorId + "-current", ICurrentMeasurable.class);
        
        mLeftMotor = ComponentManager.
                getComponent(leftMotorId + "-motor", IMotor.class);
        mRightMotor = ComponentManager.
                getComponent(rightMotorId + "-motor", IMotor.class);

        mLeftEncoder = ComponentManager.
                getComponent(leftMotorId + "-encoder", IEncoder.class);
        mRightEncoder = ComponentManager.
                getComponent(rightMotorId + "-encoder", IEncoder.class);
        
        mLeftSpeedControl = new WheelSpeedControl(leftEncoderUnitsPerMeter,
                leftSpeedControl, "left");
        mRightSpeedControl = new WheelSpeedControl(rightEncoderUnitsPerMeter,
                rightSpeedControl, "right");

        mLeftSpeedSensor = new WheelSpeedSensor(leftEncoderUnitsPerMeter);
        mRightSpeedSensor = new WheelSpeedSensor(rightEncoderUnitsPerMeter);
        
        if (mSimpleModel == null) {
            mSimpleModel = new Model(wheelDistance);
        }
        else {
            mSimpleModel.setWheelDistance(wheelDistance);
        }

        mOdometry = new Odometry(
                mSimpleModel,
                mLeftSpeedSensor,
                mRightSpeedSensor
        );
        

        
        if (mDifferentialDrive == null) {
            mDifferentialDrive = new DifferentialDriveController(
                    mSimpleModel,
                    maxMovementSpeed,
                    mLeftSpeedControl,
                    mRightSpeedControl);
        }
        else {
            mDifferentialDrive.setMaxMovementSpeed(maxMovementSpeed);
        }

        if (mPathServo == null) {
            
            mDistanceController =
                    new PIDController(
                            aConfigDict.getReal(CONFIG_DISTANCE_PID_P),
                            aConfigDict.getReal(CONFIG_DISTANCE_PID_I),
                            aConfigDict.getReal(CONFIG_DISTANCE_PID_D),
                            -maxMovementSpeed,
                            maxMovementSpeed,
                            "distance"
                    );

            mAngleController =
                    new PIDController(
                            aConfigDict.getReal(CONFIG_ANGLE_PID_P),
                            aConfigDict.getReal(CONFIG_ANGLE_PID_I),
                            aConfigDict.getReal(CONFIG_ANGLE_PID_D),
                            -mSimpleModel.turnRateForSpeed(maxMovementSpeed),
                            mSimpleModel.turnRateForSpeed(maxMovementSpeed),
                            "angle"
                    );
            
        }
        else {
            mDistanceController.setPID(
                aConfigDict.getReal(CONFIG_DISTANCE_PID_P),
                aConfigDict.getReal(CONFIG_DISTANCE_PID_I),
                aConfigDict.getReal(CONFIG_DISTANCE_PID_D),
                -maxMovementSpeed,
                maxMovementSpeed
            );
            mAngleController.setPID(
                aConfigDict.getReal(CONFIG_ANGLE_PID_P),
                aConfigDict.getReal(CONFIG_ANGLE_PID_I),
                aConfigDict.getReal(CONFIG_ANGLE_PID_D),
                -mSimpleModel.turnRateForSpeed(maxMovementSpeed),
                mSimpleModel.turnRateForSpeed(maxMovementSpeed)
            );
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
        mOdometry.setPosition(new Vector2(
                msg.getData().getReal("x"),
                msg.getData().getReal("y"))
        );
        mOdometry.setVelocity(Vector2.ZERO);
        mOdometry.setDirection(msg.getData().getReal("theta"));
        mPathServo = null;
        mRightSpeedControl.setPosition((long) 0);
        mLeftSpeedControl.setPosition((long) 0);
        Logger.getLogger(BioteSocket.class.getName()).log(Level.INFO,
                "Path Abort");
    }
    
    private void onSetSpeeds(Event msg) {
        mOdometry.setPosition(Vector2.ZERO);
        mOdometry.setVelocity(Vector2.ZERO);
        mOdometry.setDirection(0);
        mPathServo = null;
        double right = msg.getData().getReal("rightSpeed");
        double left = msg.getData().getReal("leftSpeed");
        mRightSpeedControl.setPosition(right);
        mLeftSpeedControl.setPosition(left);
        Logger.getLogger(BioteSocket.class.getName()).log(Level.INFO,
                "Setting speed l=" + left + " r=" + right);
    }
    
    private void onTick(Event msg) {
        
        long now = System.currentTimeMillis();
        long dtms = (now - mLastUpdateTime);
        
        double dt = ((double) dtms) / (double)1000.0;
        
        long newRightPosition = mRightEncoder.getEncoderPosition();
        long newLeftPosition = mLeftEncoder.getEncoderPosition();
        
        String errorStatus = mRoboClaw.getErrorStatus();
        double logicBattery = mRoboClaw.getLogicBatteryVoltage();
        double mainBattery = mRoboClaw.getMainBatteryVoltage();
        double temperature = mRoboClaw.getTemperature();
        double Im1 = mM1.getCurrentDraw();
        double Im2 = mM2.getCurrentDraw();
        

        // Left/right speed in ticks/sec.
        double leftSpeedCounts = ((double) (newLeftPosition - mLeftPosition)) / dt;
        mLeftSpeedSensor.setSpeedCounts(leftSpeedCounts);
        double rightSpeedCounts = ((double) (newRightPosition - mRightPosition)) / dt;
        mRightSpeedSensor.setSpeedCounts(rightSpeedCounts);
        
               // rev/min   = counts/s * 60s/min = counts/s * rev/count = rev/s
        double leftSpeedRPM = leftSpeedCounts * 60.0 / mLeftEncoderTicksPerRevolution;
        double rightSpeedRPM = rightSpeedCounts * 60.0 / mRightEncoderTicksPerRevolution;
        
        mOdometry.tick(dt);
        
        if (mPathServo != null) {
            mPathServo.tick(dt, mOdometry.getPositionSensor(), mDifferentialDrive);
            if (mPathServo.goalReached()) {
                reached();
                mPathServo = null;
                mRightSpeedControl.setPosition((long) 0);
                mLeftSpeedControl.setPosition((long) 0);
            }
        }
        
        
        DictionaryAtom logData = DictionaryAtom.newAtom();
        logData.setInt("time", now);
        logData.setString("error_status", errorStatus);
        logData.setReal("logic_battery", logicBattery);
        logData.setReal("main_battery", mainBattery);
        logData.setReal("temperature", temperature);
        logData.setReal("current_m1", Im1);
        logData.setReal("current_m2", Im2);
        logData.setReal("leftSpeed", mLeftSpeedSensor.getValue());
        logData.setReal("rightSpeed", mRightSpeedSensor.getValue());
        logData.setReal("leftSpeedSetpoint", mDifferentialDrive.getLeftSpeed());
        logData.setReal("rightSpeedSetpoint", mDifferentialDrive.getRightSpeed());
        mLoggingRingBuffer.add(logData);

        
        Position p = mOdometry.getPositionSensor().getValue();
        
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
                    "bearing: v = " + mOdometry.getPositionSensor().getValue().getVelocity().length() + " d = " + p.getAngle());
        
            Logger.getLogger(BioteSocket.class.getName()).log(Level.INFO,
                    "Distance traveled " +
                            p.getVelocity().getX() + "," + p.getVelocity().getY());
        
            Logger.getLogger(BioteSocket.class.getName()).log(
                    Level.INFO,
                    "dt = " + dtms +
                    " r = " + newRightPosition +
                    " l = " + newLeftPosition +
                    " x = " + p.getPosition().getX() +
                    " y = " + p.getPosition().getY() +
                    " theta = " + p.getAngle());
            if (mBioteId != 0) {
                
                DictionaryAtom dict = DictionaryAtom.newAtom();
                dict.setString("eventName", "position-update");
                dict.setInt("time", System.currentTimeMillis());
                dict.setReal("x", p.getPosition().getX());
                dict.setReal("y", p.getPosition().getY());
                dict.setReal("angle", p.getAngle());
                dict.setReal("v",
                        mOdometry.getPositionSensor().getValue().getAngle());
                dict.setReal("leftSpeed", mLeftSpeedSensor.getValue());
                dict.setReal("rightSpeed", mRightSpeedSensor.getValue());
                dict.setReal("leftSpeedCounts", leftSpeedCounts);
                dict.setReal("rightSpeedCounts", rightSpeedCounts);
                dict.setReal("leftSpeedRPM", leftSpeedRPM);
                dict.setReal("rightSpeedRPM", rightSpeedRPM);
                dict.setReal("angleSetpoint", mAngleController.getSetpoint());
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
        ImmutableList list = aEvent.getData().getList("movements");
        
        List<IMover<Position, SpeedAndTurnRate>> moverList =
                new ArrayList<IMover<Position, SpeedAndTurnRate>>();
        
        for (Atom a : list) {
            ImmutableDict movement = (ImmutableDict) a;
            String type = movement.getString("t");
            if (type.equals("point")) {
                double x = movement.getReal("x");
                double y = movement.getReal("y");
                
                Vector2 dest = new Vector2(x, y);

                MoverToPoint mtp = new MoverToPoint(
                    dest, mDistanceTolerance,
                    mDistanceController,
                    mAngleController);
                moverList.add(mtp);
            }
            else if (type.equals("angle")) {
                double theta = movement.getReal("a");

                MoverToAngle mta = new MoverToAngle(
                        theta, mAngleTolerance,
                        mDistanceController,
                        mAngleController
                );
                moverList.add(mta);
            }
        }
        
        mPathServo = new MoverComposite(moverList);
        
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
