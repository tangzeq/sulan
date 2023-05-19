package com.netty.customer.broadcast;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameGrabber;

/**
 * 功能描述：视频采集器
 * 作者：唐泽齐
 */
public class VideoRecorder implements Runnable {
    private static final int VIDEO_DEVICE_INDEX = 0;
    private FFmpegFrameRecorder recorder;
    private int width, height;
    public VideoRecorder(FFmpegFrameRecorder recorder, int width, int height) {
        this.recorder = recorder;
        this.width = width;
        this.height = height;
    }

    @Override
    public void run() {
        try {
            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(VIDEO_DEVICE_INDEX);
            grabber.setImageWidth(width);
            grabber.setImageHeight(height);
            grabber.start();

            long startTS = 0, videoTS = 0;
            Frame frame = null;
            while (!Thread.interrupted() && (frame = grabber.grab()) != null) {
                if (startTS == 0) {
                    startTS = System.currentTimeMillis();
                }
                videoTS = 1000 * (System.currentTimeMillis() - startTS);
                if (videoTS > recorder.getTimestamp()) {
                    recorder.setTimestamp(videoTS);
                }
                recorder.record(frame);
            }
            grabber.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
