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

import org.ensor.robots.motors.ComponentManager;
import org.ensor.robots.motors.IComponent;
import org.ensor.robots.motors.IMotorWithEncoder;
import static org.ensor.robots.roboclawdriver.App.poll;

/**
 *
 * @author jona
 */
public class AppReadEncoders {

    public static void poll(RoboClaw roboClaw,
            IMotorWithEncoder m1, IMotorWithEncoder m2) {
        
        roboClaw.updateData();
        
        long enc1pos = m1.getEncoderPosition();
        long enc1v = m1.getEncoderSpeed();
        double m1I = m1.getCurrentDraw();
        
        String errorStatus = roboClaw.getErrorStatus();
        System.out.println("Error status: " + errorStatus);
        
        String enc1posstr = String.format("%8x", enc1pos);
        String mode1 = m1.getEncoderQuatratureMode() ? "quad" : "abs";
        
        System.out.println("motor1 pos " + mode1 + " " + enc1posstr + " " + enc1pos + " v = " + enc1v + " I = " + m1I);
        
        long enc2pos = m2.getEncoderPosition();
        long enc2v = m2.getEncoderSpeed();
        double m2I = m2.getCurrentDraw();
        String enc2posstr = String.format("%8x", enc2pos);
        String mode2 = m1.getEncoderQuatratureMode() ? "quad" : "abs";

        System.out.println("motor2 pos " + mode2 + " " + enc2posstr + " " + enc2pos + " v = " + enc2v + " I = " + m2I);
    }
    
    public static void main(String[] args) throws Exception {
        RoboClaw roboClaw = new RoboClaw("/dev/ttyACM0");

        roboClaw.handleCommand(new CommandSetMainBatteryMinVoltage(6));
        
        IComponent c1 = ComponentManager.getComponent("roboclaw-0-motor0");
        IComponent c2 = ComponentManager.getComponent("roboclaw-0-motor1");
        
        IMotorWithEncoder m1 = c1.getMotorWithEncoderInterface();
        IMotorWithEncoder m2 = c2.getMotorWithEncoderInterface();

        while (true) {
            poll(roboClaw, m1, m2);

            Thread.sleep(50);
        }

    }
}
