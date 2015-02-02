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

import org.ensor.robots.os.api.ComponentManager;
import org.ensor.robots.motors.ICurrentMeasurable;
import org.ensor.robots.motors.IEncoder;
import org.ensor.robots.motors.IMotor;

/**
 * This class is a simple test application to verify
 * the functionality of the RoboClaw motor controller.
 */
public final class App {

    private App() {}
    
    public static void poll(RoboClaw roboClaw,
            IEncoder m1,
            ICurrentMeasurable c1,
            IEncoder m2,
            ICurrentMeasurable c2) {
        
        System.out.println("Firmware revision: " + 
                roboClaw.getFirmwareVersion());
        System.out.println("Main battery voltage: " +
                roboClaw.getMainBatteryVoltage());
        System.out.println("Logic battery voltage: " +
                roboClaw.getLogicBatteryVoltage());
        
        long enc1pos = m1.getEncoderPosition();
        long enc1v = m1.getEncoderSpeed();
        double m1I = c1.getCurrentDraw();
        
        System.out.println("motor1 pos " + enc1pos + " v = " + enc1v + " I = " + m1I);
        
        long enc2pos = m2.getEncoderPosition();
        long enc2v = m2.getEncoderSpeed();
        double m2I = c2.getCurrentDraw();
        
        System.out.println("motor2 pos " + enc2pos + " v = " + enc2v + " I = " + m2I);
        
        
    }
    
    
    public static void main( String[] args ) throws Exception {
        RoboClaw roboClaw = new RoboClaw("/dev/ttyACM0");

        ICurrentMeasurable cc1 = 
                ComponentManager.getComponent("roboclaw-0-motor0-current",
                        ICurrentMeasurable.class);
        IEncoder ce1 =
                ComponentManager.getComponent("roboclaw-0-motor0-encoder",
                        IEncoder.class);
        ICurrentMeasurable cc2 =
                ComponentManager.getComponent("roboclaw-0-motor1-current",
                        ICurrentMeasurable.class);
        IEncoder ce2 =
                ComponentManager.getComponent("roboclaw-0-motor1-encoder",
                        IEncoder.class);
        
        
        while (true) {
            poll(roboClaw, ce1, cc1, ce2, cc2);
//
//            IMotor m1 = c1.getMotorInterface();
//            IMotor m2 = c2.getMotorInterface();
//
//            m1.setDutyCycle(1.0);
//            m2.setDutyCycle(0.5);
//
//            Thread.sleep(500);
//            poll(roboClaw, ce1, cc1, ce2, cc2);
//            poll(roboClaw, ce1, cc1, ce2, cc2);
//            poll(roboClaw, ce1, cc1, ce2, cc2);
//            poll(roboClaw, ce1, cc1, ce2, cc2);
//            poll(roboClaw, ce1, cc1, ce2, cc2);
//
//            m1.setDutyCycle(0.0);
//            m2.setDutyCycle(0.0);
//
//            Thread.sleep(500);
//            poll(roboClaw, ce1, cc1, ce2, cc2);
//
//            m1.setDutyCycle(-0.5);
//            m2.setDutyCycle(-1);
//
//            Thread.sleep(500);
//            poll(roboClaw, ce1, cc1, ce2, cc2);
//
//            m1.setDutyCycle(0.0);
//            m2.setDutyCycle(0.0);
            Thread.sleep(500);
        }
        
    }
    
    
    public static void doit(RoboClaw roboClaw) {
        
    }
    
    
}
