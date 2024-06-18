package com.example.player.utils;
import lombok.SneakyThrows;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;

public class ScrollingMapPanel extends JPanel {
    private Set<Integer> keys = new HashSet<>();
    private BufferedImage mapImage;
    private Rectangle viewport = new Rectangle(0, 0, 10, 10); // 初始视图位置

    private static Runnable runnable = null;

    public ScrollingMapPanel() {
        try {
            // 加载地图图像
            mapImage = ImageIO.read(new File("player/src/main/resources/images/defaultSene/background.png"));
            if (mapImage == null) {
                throw new RuntimeException("无法加载地图图像");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 设置面板可以接收键盘焦点
        setFocusable(true);
        requestFocusInWindow();
        // 添加鼠标监听器来处理滚动（这里只是一个简单的示例）
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keys.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keys.remove(e.getKeyCode());
            }
        });
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true) {
                    Thread.sleep(1000/120);
                    int dx = 0;
                    int dy = 0;
                    for (Integer key : keys) {
                        switch (key) {
                            case KeyEvent.VK_W: dy--;break;
                            case KeyEvent.VK_S: dy++;break;
                            case KeyEvent.VK_A: dx--;break;
                            case KeyEvent.VK_D: dx++;break;
                        }
                    }
                    if(dx!=0 || dy!=0) {
                        viewport.translate(dx, dy);
                        repaint();
                    }
                }
            }
        }).start();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 确保viewport不会超出地图边界
        viewport.x = Math.max(0, Math.min(viewport.x, mapImage.getWidth() - getWidth()));
        viewport.y = Math.max(0, Math.min(viewport.y, mapImage.getHeight() - getHeight()));

        // 绘制viewport内的地图部分
        g.drawImage(mapImage, -viewport.x, -viewport.y, this);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Scrolling Map Example");
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(200, 200); // 设置窗口大小
        frame.add(new ScrollingMapPanel()); // 添加滚动地图面板
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setAlwaysOnTop(true);
        new Thread(runnable).start();
    }
}
