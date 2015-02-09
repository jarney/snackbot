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

import org.ensor.math.geometry.Vector2;

/**
 * This class models the current position and speed
 * of the robot.  This is used by various controllers
 * to control the robot's motion.
 *
 * @author jona
 */
public class Position {
    private Vector2 mPos;
    private Vector2 mVel;
    private Double mAngle;
    /**
     * This constructs an object representing
     * the current position, orientation and speed
     * of the robot.
     * @param aPos The position of the robot in terms of a euclidian
     *             coordinate system relative to an arbitrary fixed point.
     * @param aVel The velocity of the robot in terms of m/s in terms of a
     *             fixed coordinate system.
     * @param aAngle The orientation of the robot according to a fixed
     *               coordinate system.  Angles are given in terms of radians.
     */
    public Position(
            final Vector2 aPos,
            final Vector2 aVel,
            final Double aAngle) {
        mPos = aPos;
        mVel = aVel;
        mAngle = aAngle;
    }
    /**
     * This method gives the position of the robot in terms
     * of meters in a fixed euclidian coordinate system.
     * @return The current position of the robot.
     */
    public Vector2 getPosition() {
        return mPos;
    }
    /**
     * This method gives the orientation of the robot
     * in terms of radians according to a fixed euclidian
     * coordinate system.
     * @return The orientation angle in radians.
     */
    public Double getAngle() {
        return mAngle;
    }
    /**
     * This method gives the current velocity of the robot
     * in terms of meters in a fixed euclidian coordinate system.
     * @return The velocity of the robot.
     */
    public Vector2 getVelocity() {
        return mVel;
    }
    
    public void setDirection(double aAngle) {
        mAngle = aAngle;
    }
    public void setPosition(Vector2 aPosition) {
        mPos = aPosition;
    }
    
    public void setVelocity(Vector2 aVelocity) {
        mVel = aVelocity;
    }
    
}