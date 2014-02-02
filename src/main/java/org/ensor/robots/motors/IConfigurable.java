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
 * This interface represents an object which can be configured.  The
 * configuration can be retrieved, can be set, and the object which
 * implements this may also choose to implement a means by which the
 * object can store some or all of the configuration into the non-volatile
 * memory of a device.
 *
 * @author jona
 */
public interface IConfigurable {

    /**
     * This method returns the current configuration settings
     * for this object.  The configuration settings are expressed
     * as a serialized string whose meaning is dependent on the
     * particular type of object being configured.
     * @return A string representation of this object's configuration.
     */
    String getConfiguration();

    /**
     * This method sets the configuration into this object's representation.
     * @param aConfiguration A string representation of this object's
     *                       configuration.
     */
    void setConfiguration(String aConfiguration);

    /**
     * When the object represents a physical device, this causes
     * any settings which are present on the physical device to be written into
     * the device's non-volatile storage.  Not all devices support this
     * type of configuration and in those cases, the device is expected
     * to silently ignore this method.
     */
    void saveNonVolatileConfiguration();

}
