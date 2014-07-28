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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ensor.io.tty.ITTY;
import org.ensor.io.tty.TTYFactory;
import org.ensor.robots.motors.ComponentManager;
import org.ensor.robots.motors.IComponent;
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
public class RoboClaw implements
        IComponent,
        IConfigurable,
        ITemperatureSensor {
    
    private static final Logger LOGGER = Logger.getLogger(RoboClaw.class.getCanonicalName());
    
    /**
     * This is the default address of the RoboClaw
     * on a packet-serial device.
     */
    public static final byte ADDRESS_DEFAULT = (byte) 0x80;
    
    private final byte mAddress;
    private final OutputStream mOutputStream;
    private final InputStream mInputStream;
    private String mFirmwareVersion;
    private double mMainBatteryVoltage;
    private double mLogicBatteryVoltage;
    private final RoboClawMotor m1;
    private final RoboClawMotor m2;
    private double mTemperature;
    private int mErrorStatus;
    private Thread mRoboClawThread;
    private boolean mRunning;
    
    /**
     * The constructor creates a RoboClaw object based on the TTY
     * that the RoboClaw is connected to.  The Address for packet-serial
     * protocol purposes is set to the default address of 0x80.
     * @param aTTY The TTY on which to find the RoboClaw.
     * @throws Exception An exception is thrown if the TTY
     *                   device could not be found or opened.
     */
    public RoboClaw(final String aTTY)
            throws Exception {
        this(aTTY, ADDRESS_DEFAULT);
    }
    
    /**
     * The constructor creates a RoboClaw object based on the
     * TTY that the RoboClaw is connected to as well as the
     * address of the RoboClaw.  For a RoboClaw in Packet Serial mode,
     * the address must be configured on the RoboClaw.  The default address
     * is "0x80" and several RoboClaw devices may be chained on the same
     * physical TTY line.  For USB mode, the address defaults to "0x80"
     * and only one RoboClaw will be found on each of the virtual TTY devices.
     * @param aTTY The TTY on which to find the RoboClaw.
     * @param aAddress The address of the RoboClaw when in packet serial mode.
     * @throws Exception This exception is thrown if the TTY
     *                               device could not be found.
     */
    public RoboClaw(final String aTTY, final byte aAddress)
            throws Exception {

        ITTY tty = TTYFactory.open(aTTY);
        
        mOutputStream = tty.getOutputStream();
        mInputStream = tty.getInputStream();
        
        mAddress = aAddress;
        
        mTemperature = 70.0;
        mErrorStatus = 0;
        
        m1 = new RoboClawMotor(this, 0);
        m2 = new RoboClawMotor(this, 1);
        
        initialize();
        
        
        ComponentManager mgr = ComponentManager.getInstance();
        mgr.registerComponent("roboclaw-0-motor0", m1);
        mgr.registerComponent("roboclaw-0-motor1", m2);
        mgr.registerComponent("roboclaw-0", this);
        
    }

    public double getTemperature() {
        handleCommand(new CommandReadTemperature(this));
        return mTemperature;
    }
    
    protected void setTemperature(double aTemperature) {
        mTemperature = aTemperature;
    }

    private final void initialize() {
        handleCommand(new CommandReadFirmware(this));
        handleCommand(new CommandReadEncoderMode(m1, m2));
        handleCommand(new CommandSetEncoderMode(m1, false, true));
        handleCommand(new CommandSetEncoderMode(m2, false, true));
        
    }
    
    public void shutdown() {
        mRunning = false;
    }
    
    public String getFirmwareVersion() {
        return mFirmwareVersion;
    }
    
    public double getMainBatteryVoltage() {
        handleCommand(new CommandReadMainBatteryVoltage(this));
        return mMainBatteryVoltage;
    }

    protected void setMainBatteryVoltage(double aMainBatteryVoltage) {
        mMainBatteryVoltage = aMainBatteryVoltage;
    }
    
    public double getLogicBatteryVoltage() {
        handleCommand(new CommandReadLogicBatteryVoltage(this));
        return mLogicBatteryVoltage;
    }
    
    protected void setLogicBatteryVoltage(double aLogicBatteryVoltage) {
        mLogicBatteryVoltage = aLogicBatteryVoltage;
    }
    
    
    /**
     * This method sets the firmware version.  The firmware version is
     * obtained by connecting to the device and using the ReadFirmware command
     * to request the firmware version.
     * @param aFirmwareVersion 
     */
    protected void setFirmwareVersion(String aFirmwareVersion) {
        mFirmwareVersion = aFirmwareVersion;
    }
    
    /**
     * This method handles a command to the RoboClaw controller.
     * The given command is serialized and sent to the RoboClaw
     * and the response is read and given to the command to handle.
     *
     * @param aCommand The command to execute.
     */
    protected void handleCommand(Command aCommand) {

        // First, get sequence of bytes to send.
        byte[] commandBytes = aCommand.getCommand((byte) mAddress);
        
        // Next, we send them to the device.
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Command : ");
            for (int i = 0; i < commandBytes.length; i++) {
                sb.append((int) commandBytes[i]);
                sb.append(" ");
            }
            LOGGER.log(Level.FINER, sb.toString());
            
            mOutputStream.write(commandBytes);
            mOutputStream.flush();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Now, check the read mode of the command and handle it
        // accordingly.
        try {
            switch (aCommand.getReadMode()) {
                case Command.READ_MODE_FIXED:
                    int length = aCommand.getResponseLength();
                    byte[] fixedResponse = new byte[length];
                    int bytesRead = readUntilFull(mInputStream,
                                fixedResponse,
                                fixedResponse.length);
                    if (bytesRead != length) {
                        LOGGER.log(Level.SEVERE,
                                "Command returned " + bytesRead +
                                " bytes but was expected to return " + length + " bytes");
                    }
                    else {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Received : ");
                        for (int i = 0; i < bytesRead; i++) {
                            sb.append((int) fixedResponse[i]);
                            sb.append(" ");
                        }
                        LOGGER.log(Level.FINER, sb.toString());
                        
                        byte checksum = aCommand.calculateChecksum(
                                commandBytes, commandBytes.length,
                                fixedResponse, bytesRead - 1);
                        if (checksum != fixedResponse[bytesRead - 1]) {
                            LOGGER.log(Level.INFO,
                                    "Checksum was " +
                                    fixedResponse[bytesRead - 1] + 
                                    " but was expected to be " + checksum);
                        }
                        else {
                            aCommand.onResponse(fixedResponse, bytesRead);
                        }
                    }
                    break;
                case Command.READ_MODE_NONE:
                    aCommand.onResponse(null, 0);
                    break;
                case Command.READ_MODE_NULL_TERMINATED:
                    byte[] variableResponse = new byte[32];
                    int bytesRead2 = readUntilNull(mInputStream, variableResponse);
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append("Received : ");
                    for (int i = 0; i < bytesRead2; i++) {
                        sb.append((int) variableResponse[i]);
                        sb.append(" ");
                    }
                    LOGGER.log(Level.INFO, sb.toString());
                    aCommand.onResponse(variableResponse, bytesRead2);
                    break;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
    
    private int readUntilFull(InputStream aInputStream, byte[] fixedResponse, int length) throws IOException {
        int i;
        for (i = 0; i < length; i++) {
            int b = aInputStream.read();
            if (b == -1) {
                break;
            }
            fixedResponse[i] = (byte) b;
        }
        return i;
    }
    
    private int readUntilNull(InputStream aInputStream, byte[] variableResponse) throws IOException {
        int bytesRead = 0;
        while (true) {
            int b = aInputStream.read();
            if (b == -1 || b == 0) {
                break;
            }
            variableResponse[bytesRead] = (byte) b;
            bytesRead++;

        }
        // Checksum byte
        variableResponse[bytesRead] = (byte) aInputStream.read();
        bytesRead++;
        return bytesRead;
    }
    
    
    /**
     * This method sets the minimum and maximum main battery threshold
     * voltages.
     * @param minVoltage Sets main battery (B- / B+) minimum voltage level. If
     *                  the battery voltages drops below the set
     *                  voltage level RoboClaw will shut down.
     *
     * @param maxVoltage Sets the main battery high voltage level.
     */
    
    void setMainBatteryThresholds(double aMinVoltage, double aMaxVoltage) {
        handleCommand(new CommandSetMainBatteryMinVoltage(aMinVoltage));
        handleCommand(new CommandSetMainBatteryMaxVoltage(aMaxVoltage));
    }

    void setLogicBatteryThresholds(double aMinVoltage, double aMaxVoltage) {
        handleCommand(new CommandSetLogicBatteryMinVoltage(aMinVoltage));
        handleCommand(new CommandSetLogicBatteryMaxVoltage(aMaxVoltage));
    }

    protected void setErrorStatus(int b) {
        mErrorStatus = b;
    }

    public String getErrorStatus() {
        
        handleCommand(new CommandReadErrorStatus(this));
        handleCommand(new CommandReadMotorCurrents(m1, m2));
        String s = "";

        if (mErrorStatus != 0) {
            s = s + Integer.toHexString(mErrorStatus) + ":";
        }
        
        if ((mErrorStatus & 0x01) != 0) {
            s = s + "M1 Over Current:";
        }
        if ((mErrorStatus & 0x02) != 0) {
            s = s + "M2 OverCurrent:";
        }
        if ((mErrorStatus & 0x04) != 0) {
            s = s + "E-Stop:";
        }
        if ((mErrorStatus & 0x08) != 0) {
            s = s + "Temperature:";
        }
        if ((mErrorStatus & 0x10) != 0) {
            s = s + "Temperature2:";
        }
        if ((mErrorStatus & 0x20) != 0) {
            s = s + "Main Battery High:";
        }
        if ((mErrorStatus & 0x40) != 0) {
            s = s + "Main battery low:";
        }
        if ((mErrorStatus & 0x80) != 0) {
            s = s + "Logic battery high:";
        }
        if ((mErrorStatus & 0x100) != 0) {
            s = s + "Logic battery low:";
        }
        if ((mErrorStatus & 0x200) != 0) {
            s = s + "Fault M1:";
        }
        if ((mErrorStatus & 0x400) != 0) {
            s = s + "Fault M2:";
        }
        if ((mErrorStatus & 0x800) != 0) {
            s = s + "MBATHigh";
        }
        if ((mErrorStatus & 0x1000) != 0) {
            s = s + "Warn Overcurrent M1:";
        }
        if ((mErrorStatus & 0x2000) != 0) {
            s = s + "Warn Overcurrent M2:";
        }
        if ((mErrorStatus & 0x4000) != 0) {
            s = s + "TEMP1:";
        }
        if ((mErrorStatus & 0x8000) != 0) {
            s = s + "TEMP2:";
        }
        return s;
    }

    /**
     * This method retrieves the configuration of the RoboClaw and associated
     * objects.
     * @return The XML document representing the RoboClaw's configuration state.
     */
    public String getConfiguration() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * This method reads the configuration specified and uses it to
     * set the state of the RoboClaw and associated objects.
     * @param aConfiguration 
     */
    public void setConfiguration(String aConfiguration) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * This method saves the settings currently set in the RoboClaw to
     * the RoboClaw's non-volatile memory.
     */
    public void saveNonVolatileConfiguration() {
        Command c = new CommandWriteSettings();
        handleCommand(c);
    }

    /* From here down are the methods associated with
     * the IComponent interface.
     */

    public IMotor getMotorInterface() {
        return null;
    }

    public IConfigurable getConfigurableInterface() {
        return this;
    }

    public IServo getPositionServo() {
        return null;
    }

    public IServo getSpeedServo() {
        return null;
    }

    public ITemperatureSensor getTemperatureSensor() {
        return this;
    }

    public ICurrentMeasurable getElectricalMonitor() {
        return null;
    }

    public IEncoder getEncoder() {
        return null;
    }

}
