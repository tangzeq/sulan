package com.netty.customer.broadcast;

import org.bytedeco.javacv.FFmpegFrameRecorder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * 功能描述：音频采集器
 * 作者：唐泽齐
 */
public class AudioRecoder implements Runnable{
    private FFmpegFrameRecorder recorder;
    private int channels;
    private int sampleRate;
    public AudioRecoder(FFmpegFrameRecorder recorder, int sampleRate, int channels) {
        this.recorder = recorder;
        this.sampleRate = sampleRate;
        this.channels = channels;
    }

    @Override
    public void run() {
        try {
            AudioFormat format = new AudioFormat(Float.valueOf(sampleRate), 16, channels, true, false);
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(new DataLine.Info(TargetDataLine.class, format));
            line.open(format);
            line.start();

            int sampleRate = (int) format.getSampleRate();
            int numChannels = format.getChannels();
            byte[] buffer = new byte[sampleRate * numChannels];

            while (!Thread.interrupted()) {
                int nBytesRead = 0;
                while (nBytesRead == 0) {
                    nBytesRead = line.read(buffer, 0, line.available());
                }
                int nSamplesRead = nBytesRead / 2;
                short[] samples = new short[nSamplesRead];
                ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
                ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);
                recorder.recordSamples(sampleRate, numChannels, sBuff);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
