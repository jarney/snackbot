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
import org.ensor.robots.motors.IEncoder;
import org.ensor.robots.motors.ICurrentMeasurable;
import static org.ensor.robots.roboclawdriver.App.poll;

/**
 *
 * @author jona
 */
public class AppReadEncoders {

    public static void main(String[] args) throws Exception {
        RoboClaw roboClaw = new RoboClaw("/dev/ttyACM0");

        roboClaw.handleCommand(new CommandSetMainBatteryMinVoltage(6));
        
        ICurrentMeasurable ce1 =
                ComponentManager.getComponent("roboclaw-0-motor0-current",
                        ICurrentMeasurable.class);
        IEncoder e1 =
                ComponentManager.getComponent("roboclaw-0-motor0-encoder",
                        IEncoder.class);
        ICurrentMeasurable ce2 =
                ComponentManager.getComponent("roboclaw-0-motor1-current",
                        ICurrentMeasurable.class);
        IEncoder e2 =
                ComponentManager.getComponent("roboclaw-0-motor1-encoder",
                        IEncoder.class);
        

        while (true) {
            poll(roboClaw, e1, ce1, e2, ce2);

            Thread.sleep(50);
        }

    }
}
