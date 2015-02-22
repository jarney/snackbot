/*
 * The MIT License
 *
 * Copyright 2015 Jon Arney, Ensor Robotics.
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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.ensor.algorithms.control.pid.IController;
import org.ensor.algorithms.control.pid.IServo;

/**
 *
 * @author jona
 */
public class WheelVelocitiesController implements IController<WheelVelocities> {
    private static Logger LOG = Logger.getLogger(WheelVelocitiesController.class.getName());
    private final IServo mRight;
    private final IServo mLeft;

    private double mLeftSpeed;
    private double mRightSpeed;
    
    public WheelVelocitiesController(IServo aLeft, IServo aRight) {
        mLeft = aLeft;
        mRight = aRight;
        mLeftSpeed = 0;
        mRightSpeed = 0;
    }
    
    public double getLeftSpeed() { return mLeftSpeed; }
    public double getRightSpeed() { return mRightSpeed; }
    
    public void setOutput(WheelVelocities wheelVelocities) {
        mRightSpeed = wheelVelocities.getRightVelocity();
        mLeftSpeed = wheelVelocities.getLeftVelocity();
        
        LOG.log(Level.INFO, "Speeds: " + mLeftSpeed + ":" + mRightSpeed);
        
        mLeft.setPosition(mLeftSpeed);
        mRight.setPosition(mRightSpeed);
    }
}
