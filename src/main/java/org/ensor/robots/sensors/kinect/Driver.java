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

package org.ensor.robots.sensors.kinect;

/**
 *
 * @author jona
 */
public final class Driver implements AutoCloseable {
    private static boolean mLibLoaded;
    private static Driver mDriver;
    
    private Device[] mDevices;
    private boolean mOpen;
    
    public static Driver getKinectDriver() throws Exception {
        synchronized (Driver.class) {
            if (!mLibLoaded) {
                System.loadLibrary("KinectJNI");
                mLibLoaded = true;
            }
            if (mDriver != null) {
                return mDriver;
            }
            mDriver = new Driver();
            return mDriver;
        }
    }
    
    private Driver() throws KinectException {
        synchronized (Driver.class) {
            int rc = nativeInit();
            if (rc < 0) {
                throw new KinectException("Could not initialize kinect library");
            }
            int devices = nativeDeviceCount();
            mOpen = true;
            if (devices > 0) {
                mDevices = new Device[devices];
                for (int i = 0; i < mDevices.length; i++) {
                    mDevices[i] = null;
                }
            }
            else {
                mDevices = null;
            }
        }
    }

    public int getDeviceCount() {
        return mDevices == null ? 0 : mDevices.length;
    }
    
    public void setLogLevel(LogLevel aLogLevel) {
        nativeSetLogLevel(aLogLevel.getLevel());
    }
    
    
    public Device openDevice(int aDeviceId) throws KinectException {
        synchronized (Driver.class) {
            if (!mOpen) {
                throw new KinectException(
                        "Driver is closed, please re-open " +
                        "driver before creating a new device" );
            }
            if (mDevices == null || aDeviceId >= mDevices.length) {
                throw new KinectException("Device does not exist");
            }
            else {
                if (mDevices[aDeviceId] == null) {
                    mDevices[aDeviceId] = new Device(this, aDeviceId);
                    return mDevices[aDeviceId];
                }
                else {
                    throw new KinectException(
                        "Device is already open"
                    );
                }
            }
        }
    }

    protected void closeDevice(final int aDeviceId) throws KinectException {
        synchronized (Driver.class) {
            if (aDeviceId >= mDevices.length) {
                throw new KinectException("Device does not exist");
            }
            if (mDevices[aDeviceId] == null) {
                throw new KinectException("Device already closed");
            }
            mDevices[aDeviceId] = null;
        }
    }
    
    @Override
    public void close() {
        synchronized (Driver.class) {
            if (mDriver != null) {
                mDriver = null;
                nativeShutdown();
                mOpen = false;
            }
        }
    }

    private native int nativeInit();
    private native void nativeShutdown();
    private native int nativeDeviceCount();
    private native void nativeSetLogLevel(int aLevel);
}
