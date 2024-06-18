package com.example.player.config;

import com.example.player.scene.BaseSene;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Component
@ConfigurationProperties(prefix = "spring.environment-config")
public class EnvironmentConfig {
    //窗口
    public volatile long window;
    //窗口宽度
    public volatile int width;
    //窗口高度
    public volatile int height;
    //默认场景
    public volatile String defaultSene;
    //按键集合
    public volatile Set<Integer> keySet = new HashSet<>();
    //鼠标坐标
    public volatile float[] mouseCoord = new float[]{0, 0};
    //滚轮
    public volatile double[] offset = new double[]{0, 0};
    //场景组
    public volatile BaseSene[] senes = new BaseSene[]{};
    //fontSize
    public volatile int fontSize;
    public volatile Map<String,BaseSene> seneMap;
}
