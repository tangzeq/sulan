package com.example.player.element;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 文本绘制
 */
public interface Text {
    void draw(String text,float x,float y,float width,float height);
}
