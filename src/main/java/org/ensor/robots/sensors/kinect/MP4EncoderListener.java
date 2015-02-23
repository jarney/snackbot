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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.jcodec.codecs.h264.H264Encoder;
import org.jcodec.codecs.h264.H264Utils;
import org.jcodec.common.FileChannelWrapper;
import org.jcodec.common.NIOUtils;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.containers.mp4.Brand;
import org.jcodec.containers.mp4.MP4Packet;
import org.jcodec.containers.mp4.TrackType;
import org.jcodec.containers.mp4.muxer.FramesMP4MuxerTrack;
import org.jcodec.containers.mp4.muxer.MP4Muxer;
import org.jcodec.scale.RgbToYuv420;

/**
 *
 * @author jona
 */
public class MP4EncoderListener implements IVideoListener, AutoCloseable {

    private MP4Muxer mMuxer;
    private FileChannelWrapper mChannel;
    private FramesMP4MuxerTrack mVideoTrack;
    private int mFrameNumber;
    private ArrayList<ByteBuffer> spsList;
    private ArrayList<ByteBuffer> ppsList;
    private int width = 640;
    private int height = 480;
    private byte[] yuv = null;
    private H264Encoder mH264Encoder;
    private Picture pictureYUV;
    private ByteBuffer mFrameBuffer;
    private RgbToYuv420 mRGBToYUVEncoder;

    public MP4EncoderListener() throws Exception {
        // Encode as MP4 stream...
        mChannel = NIOUtils.writableFileChannel(new File("out.mp4"));

        mMuxer = new MP4Muxer(mChannel, Brand.MP4);

        mVideoTrack = mMuxer.addTrackForCompressed(TrackType.VIDEO, 25);

        mFrameNumber = 0;

        spsList = new ArrayList<>();
        ppsList = new ArrayList<>();
        
        mH264Encoder = new H264Encoder();

        // Allocate a buffer big enough to hold output frames
        mFrameBuffer = ByteBuffer.allocate(width * height * 3);
        
        mRGBToYUVEncoder = new RgbToYuv420(0, 0);
        
    }

    @Override
    public void videoCallback(byte[] videoBuffer) {
        try {
            
            Picture pictureRGB = Picture.create(width, height, ColorSpace.RGB);
            int[] dstData = pictureRGB.getPlaneData(0);

            int x,y;
            int off = 0;
            for (y = 0; y < height; y++) {
                for (x = 0; x < width; x++) {
                    dstData[off] = (int)videoBuffer[off] & 0xff;
                    off++;
                    dstData[off] = (int)videoBuffer[off] & 0xff;
                    off++;
                    dstData[off] = (int)videoBuffer[off] & 0xff;
                    off++;
                }
            }
            
            pictureYUV = Picture.create(width, height, ColorSpace.YUV420);
 
            mRGBToYUVEncoder.transform(pictureRGB, pictureYUV);
            
            // Encode image into H.264 frame, the result is stored in 'mFrameBuffer' buffer
            mFrameBuffer.clear();
            ByteBuffer result = mH264Encoder.encodeFrame(mFrameBuffer, pictureYUV);
            
            spsList.clear();
            ppsList.clear();

            H264Utils.encodeMOVPacket(result, spsList, ppsList);

            mVideoTrack.addFrame(
                    new MP4Packet(
                        result,
                        mFrameNumber,
                        25,
                        1,
                        mFrameNumber,
                        true,
                        null,
                        mFrameNumber,
                        0
                    )
            );

            mFrameNumber++;
        }
        catch (Exception ex) {
            System.out.println("Exception");
            ex.printStackTrace();
        }
    }
    

    public void close() throws Exception {
        mVideoTrack.addSampleEntry(H264Utils.createMOVSampleEntry(spsList, ppsList));
        mMuxer.writeHeader();
        NIOUtils.closeQuietly(mChannel);
    }
}
