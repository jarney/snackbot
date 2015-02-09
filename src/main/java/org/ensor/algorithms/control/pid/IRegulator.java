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

package org.ensor.algorithms.control.pid;

/**
 * This interface represents an an abstract control regulator
 * which is capable of ensuring a system converges on a particular
 * desired state.  The regulator is capable of sensing the state
 * of the system using ISensor&lt;SensorType&gt; and is capable
 * of controlling the state of the system using the controller
 * IController&lt;ControllerType&gt;
 * @param <SensorType> The type of sensor this regulator operates on.
 * @param <ControllerType> The type of output that this system can be
 *                         changed by.
 * @author jona
 */
public interface IRegulator<SensorType, ControllerType> {
    
    /**
     * This is the main control loop for a regulator.  The regulator's job
     * is to reach a particular destination state as measured by the sensors
     * available.  The regulator is allowed to change the state of the system
     * using the controller given.  Note that the control of the system
     * may be straightforward such as a PID regulator based on speed for a
     * single speed parameter directly measurable, or may be more complex such
     * as controlling the position and orientation by affecting speeds
     * of different motors.
     *
     * @param aTimeElapsed The time elapsed since the last cycle of the
     *                     controller.
     * @param aSensor The sensor used to detect current conditions.
     * @param aController The controller capable of affecting the current
     *                    conditions.
     */
    void tick(double aTimeElapsed,
            ISensor<SensorType> aSensor,
            IController<ControllerType> aController);
}
