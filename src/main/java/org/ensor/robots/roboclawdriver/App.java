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

import java.io.FileNotFoundException;
import java.util.List;
import org.ensor.robots.motors.ComponentManager;
import org.ensor.robots.motors.IComponent;
import org.ensor.robots.motors.IMotorWithEncoder;

/**
 * This class is a simple test application to verify
 * the functionality of the RoboClaw motor controller.
 */
public final class App {

    private App() {}
    
    public static void poll(RoboClaw roboClaw,
            IMotorWithEncoder m1, IMotorWithEncoder m2) {
        
        roboClaw.updateData();
        System.out.println("Firmware revision: " + 
                roboClaw.getFirmwareVersion());
        System.out.println("Main battery voltage: " +
                roboClaw.getMainBatteryVoltage());
        System.out.println("Logic battery voltage: " +
                roboClaw.getLogicBatteryVoltage());
        
        long enc1pos = m1.getEncoderPosition();
        long enc1v = m1.getEncoderSpeed();
        double m1I = m1.getCurrentDraw();
        
        System.out.println("motor1 pos " + enc1pos + " v = " + enc1v + " I = " + m1I);
        
        long enc2pos = m2.getEncoderPosition();
        long enc2v = m2.getEncoderSpeed();
        double m2I = m2.getCurrentDraw();
        
        System.out.println("motor2 pos " + enc2pos + " v = " + enc2v + " I = " + m2I);
        
        
    }
    
    
    public static void main( String[] args ) throws Exception {
        RoboClaw roboClaw = new RoboClaw("/dev/ttyACM0");

        IComponent c1 = ComponentManager.getComponent("roboclaw-0-motor0");
        IComponent c2 = ComponentManager.getComponent("roboclaw-0-motor1");
        
        IMotorWithEncoder m1 = c1.getMotorWithEncoderInterface();
        IMotorWithEncoder m2 = c2.getMotorWithEncoderInterface();
        
        poll(roboClaw, m1, m2);
        
        m1.setDutyCycle(1.0);
        m2.setDutyCycle(0.5);
        
        Thread.sleep(500);
        poll(roboClaw, m1, m2);
        poll(roboClaw, m1, m2);
        poll(roboClaw, m1, m2);
        poll(roboClaw, m1, m2);
        poll(roboClaw, m1, m2);
 
        m1.setDutyCycle(0.0);
        m2.setDutyCycle(0.0);
        
        Thread.sleep(500);
        poll(roboClaw, m1, m2);
 
        m1.setDutyCycle(-0.5);
        m2.setDutyCycle(-1);
        
        Thread.sleep(500);
        poll(roboClaw, m1, m2);
        
        m1.setDutyCycle(0.0);
        m2.setDutyCycle(0.0);
    }
    
    
    public static void doit(RoboClaw roboClaw) {
        
    }
    
    
}
