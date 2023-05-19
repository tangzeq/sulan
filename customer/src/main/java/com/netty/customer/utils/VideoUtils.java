package com.netty.customer.utils;


import lombok.SneakyThrows;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;

import java.io.File;
import java.util.UUID;


/**
 * 功能描述：视频转换类
 * 作者：唐泽齐
 */
public class VideoUtils {

    public static String webmToMp4(String filePathName, String toFilePath) throws Exception {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePathName);
        grabber.start();
        String fileName = UUID.randomUUID().toString().replaceAll("-", "");
        File tempFile3 = new File(toFilePath, fileName + ".mp4");
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(tempFile3, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        recorder.setFormat("mp4");
        recorder.start();
        Frame frame = null;
        while ((frame = grabber.grabFrame()) != null) {
            try {
                recorder.record(frame);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        recorder.stop();
        recorder.release();
        grabber.stop();
        grabber.release();
        return tempFile3.getAbsolutePath();
    }


    /**
     * video, m3u8
     *
     * @param filePathName 需要转换文件
     * @param toFilePath   需要转换的文件路径
     */
    public static String mp4ToM3u8(String filePathName, String toFilePath) throws Exception {
        //加载文件
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePathName);
        grabber.start();
        File tempFile3 = new File(toFilePath);
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(tempFile3, grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
        //格式方式
        recorder.setFormat("hls");
        recorder.setOption("hls_time", "2");
        recorder.setOption("hls_flags", "split_by_time");
        recorder.setOption("hls_list_size", "0");
        recorder.setOption("hls_playlist_type", "event");
        recorder.start(grabber.getFormatContext());
        AVPacket packet;
        while ((packet = grabber.grabPacket()) != null) {
            try {
                if (packet.duration() <= 0l) packet.duration(1);
                recorder.recordPacket(packet);
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }
        recorder.setTimestamp(grabber.getTimestamp());
        recorder.stop();
        recorder.release();
        grabber.stop();
        grabber.release();
        return tempFile3.getAbsolutePath();
    }

    @SneakyThrows
    public static void main(String[] args) {
//        String mp4 = webmToMp4("D:/ideaWorkspace/message/tmpdir/temp/2052636967-752048249678900.sl", "D:\\学习\\m3u8");
//        System.out.println("args = " + mp4);
        mp4ToM3u8(
                "D:\\学习\\小男孩被大火包围，关键时刻挖土坑保护自己，真是太聪明了.mp4",
                "D:\\ideaWorkspace\\message\\tmpdir\\broadcast\\2052636967-752061453036300.m3u8"
        );
//        mp4ToM3u8("D:\\ideaWorkspace\\message\\tmpdir\\temp\\2052636967-746058918784300.sl","D:\\学习\\m3u8");
    }

}
