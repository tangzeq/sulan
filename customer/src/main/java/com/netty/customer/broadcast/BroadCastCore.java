package com.netty.customer.broadcast;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameRecorder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 功能描述：直播核心
 * 作者：唐泽齐
 */
@Slf4j
@Component
public class BroadCastCore {
    private final static String dir = "tmpdir/broadcast/";
    private static volatile ConcurrentHashMap<Long, String> userM3u8 = new ConcurrentHashMap<>();
    private static volatile ConcurrentHashMap<Long, LinkedBlockingQueue<MultipartFile>> userFile = new ConcurrentHashMap<>();
    private static volatile Cache<Long, Long> userCache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofSeconds(60)).build();

    @SneakyThrows
    public static synchronized String addFile(Long user, MultipartFile file) {
        if (!userM3u8.containsKey(user)) {
            String name = user + "-" + System.nanoTime() + ".m3u8";
            Files.createFile(Paths.get(dir + name));
            userM3u8.put(user, name);
            Thread.startVirtualThread(new Broad(user));
        }
        if (!userFile.containsKey(user)) {
            userFile.put(user, new LinkedBlockingQueue<MultipartFile>());
        }
        userFile.get(user).put(file);
        userCache.put(user, user);
        return userM3u8.get(user);
    }

    static class Broad implements Runnable {

        private Long user;

        public Broad(Long user) {
            this.user = user;
        }

        @SneakyThrows
        @Override
        public void run() {
            log.info("Broad 开始：用户={} m3u8 = {}", user, userM3u8.get(user));
            long millis = System.currentTimeMillis();
            String m3u8 = "";
            try {
                while (userCache.asMap().containsKey(user)) {
                    final MultipartFile poll = userFile.get(user).poll();
                    if (ObjectUtils.isEmpty(poll)) {
                        Thread.sleep(500);
                        continue;
                    }
                    m3u8 = dir + userM3u8.get(user);
                    String file = dir + user + "-" + System.nanoTime() + ".m3u8";
                    FFmpegFrameRecorder recorder = FFmpegFrameRecorder.createDefault(file, 1920, 1080);
                    recorder.setFormat("hls");
                    recorder.setOption("hls_time", "1");
                    recorder.setOption("hls_flags", "split_by_time");
                    recorder.setOption("hls_list_size", "0");
                    recorder.setOption("hls_playlist_type", "event");
                    AVPacket packet;
                    FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(poll.getInputStream());
                    grabber.start();
                    AVFormatContext context = grabber.getFormatContext();
                    recorder.start(context);
                    log.info("Broad 切片开始：用户={} 文件={}", user, file);
                    while ((packet = grabber.grabPacket()) != null) {
                        try {
                            recorder.recordPacket(packet);
                        } catch (FrameRecorder.Exception e) {
                            e.printStackTrace();
                        }
                    }
                    log.info("Broad 切片结束：用户={} 文件={}", user, file);
                    grabber.stop();
                    grabber.release();
                    recorder.stop();
                    recorder.release();
                    log.info("Broad 合并文件：用户={} 文件={}==>{}", user, file, m3u8);
                    mergeM3U8File(file, m3u8);
                    log.info("Broad 合并结束：用户={} 文件={}==>{}", user, file, m3u8);
                }
            } finally {
                userM3u8.remove(user);
                userFile.remove(user);
                if (!ObjectUtils.isEmpty(m3u8)) endM3u8(m3u8);
                log.info("Broad 结束：用户={} m3u8 = {} 持续时长{}毫秒", user, user, userM3u8.get(user), System.currentTimeMillis() - millis);
            }
        }
    }

    /**
     * #EXTM3U
     * #EXT-X-VERSION:3
     * #EXT-X-TARGETDURATION:2
     * #EXT-X-MEDIA-SEQUENCE:0
     * #EXT-X-PLAYLIST-TYPE:EVENT
     * #EXTINF:1.441000,
     * 2052636967-12640237988249000.ts
     * #EXTINF:1.848000,
     * 2052636967-12640237988249001.ts
     * #EXT-X-ENDLIST
     *
     * @param source
     * @param target
     */
    @SneakyThrows
    private static void mergeM3U8File(String source, String target) {

        //读取target
        List<String> sl = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(source))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sl.add(line);
            }
        }
        //读取source
        List<String> tl = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(target))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tl.add(line);
            }
        }
        //合并
        String filename = target.replace(dir, "");
        filename = filename.replace(".m3u8", "");
        Long order = 0l;
        if (tl.size() <= 0) {
            for (String s : sl) {
                if (s.startsWith("#")) tl.add(s);
                else {
                    tl.add(filename + order + ".ts");
                    log.info("转存文件 {}{} 存在状态 {}", dir, s, Files.exists(Paths.get(dir + s)));
                    Files.copy(Paths.get(dir + s), Paths.get(dir + filename + order + ".ts"));
                    Files.delete(Paths.get(dir + s));
                    order++;
                }
            }
        } else {
            //删除结束段落
            if (tl.get(tl.size() - 1).startsWith("#EXT-X-ENDLIST")) {
                tl.remove(tl.size() - 1);
            }
            //获取文件序号（dir+filename+order.ts）
            String s = tl.get(tl.size() - 1);
            s = s.replace(".ts", "");
            s = s.replace(filename, "");
            order = Long.parseLong(s);
            //sl处理
            int i = 0;
            for (i = 0; i < sl.size(); i++) {
                if (sl.get(i).startsWith("#EXTINF")) break;
            }
            for (int j = 0; j < i; j++) {
                sl.remove(0);
            }
            sl.remove(sl.size() - 1);
            tl.add("#EXT-X-DISCONTINUITY");
            //sl文件内容写入tl
            for (int j = 0; j < sl.size(); j++) {
                String s1 = sl.get(j);
                if (s1.startsWith("#EXTINF")) //#EXTINF
                    tl.add(s1);
                else//按规则写入文件索引
                {
                    order++;
                    tl.add(filename + order + ".ts");
                    log.info("转存文件 {}{} 存在状态 {}", dir, s1, Files.exists(Paths.get(dir + s1)));
                    Files.copy(Paths.get(dir + s1), Paths.get(dir + filename + order + ".ts"));
                    Files.delete(Paths.get(dir + s1));
                }
            }
        }
        //生成新的buffer
        StringBuffer buffer = new StringBuffer();
        for (String t : tl) {
            buffer.append(t).append(System.lineSeparator());
        }
        //写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(target))) {
            writer.write(buffer.toString());
        }
        //清除source
        Files.delete(Paths.get(source));
    }

    @SneakyThrows
    private static void endM3u8(String m3u8) {
        List<String> sl = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(m3u8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sl.add(line);
            }
        }
        sl.add("#EXT-X-ENDLIST");
        //生成新的buffer
        StringBuffer buffer = new StringBuffer();
        for (String t : sl) {
            buffer.append(t).append(System.lineSeparator());
        }
        //写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(m3u8))) {
            writer.write(buffer.toString());
        }

    }


}
