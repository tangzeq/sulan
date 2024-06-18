package com.example.player.listener;

/**
 * 点击事件监听
 */
public interface BaseClickListener {
    void mouseClick(long window, int button, int action, int mods,float[] mouseCoord);
    void keyClick();

}
