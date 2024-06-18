package com.example.player.utils;

import javax.swing.*;
import java.awt.*;
public class TransparentWindow extends JFrame {
    public TransparentWindow() {
        setUndecorated(true);
        setBackground(new Color(0f, 0f, 0f, 0.01f));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setLocationRelativeTo(null);
        setVisible(true);
        setAlwaysOnTop(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TransparentWindow();
//            // 获取默认的图形设备和屏幕配置
//            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//            // 将窗口设置为全屏模式
//            gd.setFullScreenWindow(new TransparentWindow());
        });
    }
}