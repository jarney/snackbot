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

package org.ensor.robots.motors;

/**
 * This interface models a component of a robotic system.
 * This interface mainly reports what types of capabilities the
 * component provides.
 * @author jona
 */
public interface IComponent {

    /**
     * This method returns the IMotor interface
     * associated with this object.
     * @return The IMotor interface.
     */
    IMotor getMotorInterface();

    /**
     * This method returns the IMotorWithEncoder
     * interface associated with this object.
     *
     * @return The IMotorWithEncoder interface.
     */
    IMotorWithEncoder getMotorWithEncoderInterface();

    /**
     * This method returns the object associated with configuring this
     * component.
     *
     * @return The IConfigurable interface used to configure the component.
     */
    IConfigurable getConfigurableInterface();

    /**
     * Returns the IServo interface for controlling the position of this servo.
     * @return The IServo interface for controlling the position of this servo.
     */
    IServo getPositionServo();

    /**
     * Returns the speed servo associated with this object.
     * @return The IServo interface for controlling the speed of this servo.
     */
    IServo getSpeedServo();

    /**
     * Returns the temperature sensor of this component.
     * @return The temperature sensor of this component.
     */
    ITemperatureSensor getTemperatureSensor();

}
