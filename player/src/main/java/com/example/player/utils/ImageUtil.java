package com.example.player.utils;

import com.example.player.config.EnvironmentConfig;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * 图片处理工具类
 */
public class ImageUtil {
    public volatile static EnvironmentConfig environmentConfig;
    //文字组
    private volatile static Map<String, BufferedImage> textMap = new ConcurrentHashMap<>();
    private volatile static Map<CharSequence, BufferedImage> charMap = new ConcurrentHashMap<>();

    public static BufferedImage textImage(String c) {
        if(textMap.containsKey(c)) return textMap.get(c);
        Font font = new Font(null, Font.BOLD, environmentConfig.fontSize);
        BufferedImage i = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = i.createGraphics();
        graphics.setFont(font);
        graphics.drawString(c,0,0);
        int h = graphics.getFontMetrics().getHeight();
        int w = graphics.getFontMetrics().stringWidth(c);
        graphics.dispose();
        i.flush();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        // 设置文本颜色和字体
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        g2d.setComposite(ac);
        g2d.setFont(font);
//        g2d.setColor(Color.RED);
        // 绘制文本
        g2d.drawString(c, 0, font.getSize());
        // 释放图形上下文资源
        g2d.dispose();
        image.flush();
        textMap.put(c,image);
        return i;
    }

    public static BufferedImage charImage(CharSequence c) {
        if(charMap.containsKey(c)) return charMap.get(c);
        BufferedImage image = textImage(c.toString());
        charMap.put(c,image);
        return image;
    }


    @SneakyThrows
    public static void main(String[] args) {
        String text = "你好";
        Font font = new Font(null, Font.PLAIN, 50);
        BufferedImage i = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = i.createGraphics();
        graphics.setFont(font);
        graphics.drawString(text,0,0);
        int height = graphics.getFontMetrics().getHeight();
        int width = graphics.getFontMetrics().stringWidth(text);
        graphics.dispose();
        i.flush();
//        BufferedImage read = ImageIO.read(new File("player/src/main/resources/images/mouse1.jpg"));
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        // 设置背景为透明
        g2d.clearRect(0, 0, width, height);
        g2d.setBackground(Color.gray);
        g2d.dispose();
        g2d = image.createGraphics();
        // 设置文本颜色和字体
        g2d.setFont(font);
        g2d.setColor(Color.white);
        // 绘制文本
        g2d.drawString(text, 0, font.getSize());
        // 释放图形上下文资源
        g2d.dispose();
        image.flush();
        ImageIO.write(image,"PNG",new File("player/src/main/resources/images/mouse2.PNG"));
    }



}
