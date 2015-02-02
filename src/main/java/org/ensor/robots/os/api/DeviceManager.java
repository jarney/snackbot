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

package org.ensor.robots.os.api;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author jona
 */
public class DeviceManager {

    // Map of all applications "running".
    private Map<String, IApplicationProxy> mApplications;

    // Map of all devices which exist.
    private Map<String, IDevice> mDevices;

    // Map with key = device, value = application which has the device
    // opened in exclusive mode.
    private Map<String, String> mDevicesExclusive;

    // Map with key = device, value = set of applications which have opened
    // in read-only mode.
    private Map<String, Set<String>> mDevicesReadOnly;


    public DeviceManager() {

    }

    /**
     * Requests that a device be opened on behalf of an application.  If
     * another application has the device open, this method closes it and
     * notifies the other application that the device has been closed.
     * @param aApplicationId The application requesting the device open.
     * @param aDeviceId The device ID of the device being opened.
     * @param aUsage The type of open (read-only or read-write).
     */
    public void openDevice(
            final String aApplicationId,
            final String aDeviceId,
            final DeviceUsage aUsage) {

        IApplicationProxy app = mApplications.get(aApplicationId);

        String alreadyOpenedApplicationId = mDevicesExclusive.get(aDeviceId);
        if (alreadyOpenedApplicationId != null) {
            IApplicationProxy closingApplication =
                    mApplications.get(alreadyOpenedApplicationId);
            sendClosedMessage(closingApplication, aDeviceId);
        }
        sendOpenedMessage(app, aDeviceId);

    }

    public void closeDevice(
            final String aApplicationId,
            final String aDeviceId) {

        IApplicationProxy app = mApplications.get(aApplicationId);

        String applicationHolding = mDevicesExclusive.get(aDeviceId);
        if (applicationHolding != null &&
                applicationHolding.equals(aApplicationId)) {
            sendClosedMessage(app, aDeviceId);
            mDevicesExclusive.remove(aDeviceId);
        }
    }

    private void sendClosedMessage(
            final IApplicationProxy aApplication,
            final String aDeviceId) {
        aApplication.sendDeviceClosed(aDeviceId);
    }

    private void sendOpenedMessage(
            final IApplicationProxy aApplication,
            final String aDeviceId) {
        aApplication.sendDeviceOpened(aDeviceId);
    }

}
