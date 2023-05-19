package com.netty.customer.broadcast;

import lombok.SneakyThrows;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameGrabber;

/**
 * 功能描述：播放器
 * 作者：唐泽齐
 */
public class Player implements Runnable {

    private String file;

    public Player(String filePath) {
        file = filePath;
    }

    @SneakyThrows
    @Override
    public void run() {
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(file);
        CanvasFrame canvasFrame = new CanvasFrame("Video Preview", CanvasFrame.getDefaultGamma() / 2.2);
        grabber.start();
        while (canvasFrame.isDisplayable()) {
            canvasFrame.showImage(grabber.grab());
        }
        grabber.close();
    }
}
