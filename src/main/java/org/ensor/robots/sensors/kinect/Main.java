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

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author jona
 */
public class Main {
    public static void main(String[] args) throws Exception {

        MP4EncoderListener encoder = new MP4EncoderListener();
        
        System.out.println("Property " + System.getProperty("java.library.path"));
        
        try (Driver d = Driver.getKinectDriver()) {
            
            System.out.println("Device count: " + d.getDeviceCount());
            
            try (Device openDevice = d.openDevice(0)) {
                
                System.out.println("Device is open, setting up video stream");
                
                MP4EncoderListener encoderListener = new MP4EncoderListener();
                openDevice.setVideoListener(encoderListener);
                
                System.out.println("Video stream set up, setting up depth stream");
                
                IDepthListener dl = new IDepthListener() {
                    @Override
                    public void depthCallback(byte[] aDepthData) {
                        System.out.println("Depth buffer: " + aDepthData[1024]);
                    }
                };
                openDevice.setDepthListener(dl);

                System.out.println("Collecting data...");
                
                Thread.sleep(2000);
                
                System.out.println("Closing video stream");
                
                openDevice.setVideoListener(null);
                
                System.out.println("Closing depth stream");
                
                openDevice.setDepthListener(null);
                
                encoderListener.close();
                
            }
        }
        
    }
}
