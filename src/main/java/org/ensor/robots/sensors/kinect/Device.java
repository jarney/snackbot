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
 * This class represents an XBox Kinect(TM) sensor
 * and the ability to listen for video and depth-mapped data.
 *
 * In order to listen for video or depth data, simply set the
 * video or depth listeners appropriately.  The sensor will begin
 * collecting data and then calling the appropriate listener
 * to consume the data.
 *
 * In order to change the orientation of the sensor, simply
 * set the tilt degrees with the given setter.
 *
 * In order to read the tilt degrees or get data from the accelerometer,
 * call getState();
 *
 * @author jona
 */
class Device implements AutoCloseable {

    private IVideoListener mVideoListener;
    private IDepthListener mDepthListener;
    private final int mDeviceId;
    private Driver mDriver;
    
    protected Device(final Driver aDriver, final int aDeviceId) throws KinectException {
        mVideoListener = null;
        mDepthListener = null;
        mDeviceId = aDeviceId;
        mDriver = aDriver;
        int rc = nativeOpen(mDeviceId);
        if (rc < 0) {
            throw new KinectException("Could not open kinect device");
        }
    }
    
    /**
     * This method sets the current video listener and begins sending
     * frames to that listener.  If the listener given is null, then
     * the video capture is ended.
     * @param aListener A listener to listen for video data.  If the given
     *                  listener is null, no further updates will be made
     *                  and the underlying device will stop capturing data.
     */
    public void setVideoListener(final IVideoListener aListener) {
        // If we are going from listener to no listener,
        // stop the video feed.
        boolean needStop = mVideoListener != null && aListener == null;
        boolean needStart = aListener != null && mVideoListener == null;
        if (needStop) {
            nativeStopVideo(mDeviceId);
        }
        mVideoListener = aListener;
        setLEDStateAccordingToState();
        // If we are going from no listener to listener,
        // start the video feed;
        if (needStart) {
            nativeStartVideo(mDeviceId);
        }
    }
    
    private void videoCallback(byte[] aVideoData) {
        if (mVideoListener != null) {
            mVideoListener.videoCallback(aVideoData);
        }
    }
    private void depthCallback(byte[] aDepthData) {
        if (mDepthListener != null) {
            mDepthListener.depthCallback(aDepthData);
        }
    }
    /**
     * This method sets the current depth-map listener and begins
     * sending frames to that listener.  If the listener given is null,
     * then the depth-buffer capture is ended.
     *
     * @param aListener A listener to listen for depth-map data.  If the given
     *                  listener is null, no further updates will be made
     *                  and the underlying device will stop capturing data.
     */
    public void setDepthListener(final IDepthListener aListener) {
        boolean needStop = mDepthListener != null && aListener == null;
        boolean needStart = aListener != null && mDepthListener == null;
        if (needStop) {
            nativeStopDepth(mDeviceId);
        }
        mDepthListener = aListener;
        setLEDStateAccordingToState();
        if (needStart) {
            nativeStartDepth(mDeviceId);
        }
    }
    /**
     * This method sets the LED state on the device as on, off,
     * and if on, the particular color and blink state.  This is useful
     * for indicating to the user what the device is doing.
     *
     * @param aLEDState This value indicates which LED state
     *                  should be set on the device.
     */
    public void setLED(final LEDState aLEDState) {
        if (aLEDState == null) {
            nativeSetLED(mDeviceId, LEDState.LED_OFF.getValue());
        }
        else {
            nativeSetLED(mDeviceId, aLEDState.getValue());
        }
    }
    /**
     * This method sets the up/down tilt of the kinect device.
     * @param aTiltDegrees The number of degrees up or down to tilt the device.
     */
    public void setTiltDegrees(final double aTiltDegrees) {
        nativeSetTiltDegrees(mDeviceId, aTiltDegrees);
    }
    
    /**
     * This method retrieves the current state of the
     * tilt and accelerometer and fills in the given
     * state object.
     * @param aState An object to fill with the correct
     *               details about the current tilt and acceleration.
     */
    void getState(final ITiltListener aState) {
        nativeGetState(mDeviceId, aState);
    }
    
    private void setLEDStateAccordingToState() {
        if (mDepthListener == null && mVideoListener == null) {
            setLED(LEDState.LED_OFF);
        }
        else if (mDepthListener != null && mVideoListener == null) {
            setLED(LEDState.LED_YELLOW);
        }
        else if (mDepthListener == null && mVideoListener != null) {
            setLED(LEDState.LED_RED);
        }
        else {
            setLED(LEDState.LED_BLINK_RED_YELLOW);
        }
    }
        
    public void close() throws Exception {
        if (mDepthListener != null) {
            nativeStopDepth(mDeviceId);
            mDepthListener = null;
        }
        if (mVideoListener != null) {
            nativeStopVideo(mDeviceId);
            mVideoListener = null;
        }
        setLEDStateAccordingToState();
        nativeClose(mDeviceId);
        mDriver.closeDevice(mDeviceId);
    }

    // These are the methods implemented as
    // native C++ calls to the underlying Kinect device.
    
    /**
     * Open the native device.
     * @param aDeviceId The device ID to open.
     */
    private native int nativeOpen(final int aDeviceId);
    private native void nativeClose(final int aDeviceId);
    private native void nativeStopVideo(final int aDeviceId);
    private native void nativeStartVideo(final int aDeviceId);
    private native void nativeStopDepth(final int aDeviceId);
    private native void nativeStartDepth(final int aDeviceId);
    private native void nativeSetLED(final int aDeviceId, final int aLED);
    private native void nativeSetTiltDegrees(
            final int aDeviceId,
            final double aDegrees);
    private native void nativeGetState(
            final int aDeviceId,
            final ITiltListener aState);
}
