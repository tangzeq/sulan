package com.netty.customer.broadcast;

import lombok.SneakyThrows;
import org.bytedeco.javacv.FFmpegFrameRecorder;

import java.util.Arrays;

/**
 * 功能描述：测试
 * 作者：唐泽齐
 */
public class Test {

//    @SneakyThrows
//    public static void main(String[] args) {
//        FFmpegFrameRecorder recorder = FFmpegFrameRecorder.createDefault("test.flv", 1920, 1080);
////        recorder.setVideoOption("tune", "zerolatency");
////        recorder.setVideoOption("preset", "ultrafast");
//        recorder.setAudioChannels(2);
//        recorder.start();
//        Thread vt = new Thread(new VideoRecorder(recorder, 1920, 1080));
//        Thread at = new Thread(new AudioRecoder(recorder, 44100, 2));
////        Thread pl = new Thread(new Player("test1.flv"));
//        vt.start();
//        at.start();
////        pl.start();
//        vt.join();
//        at.join();
////        pl.join();
//    }

    public static void main(String[] args) {
        System.out.println("args = " + Math.toIntExact(Math.round(3.1d)));
    }

}
