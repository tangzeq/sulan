package com.example.player.element;

import com.example.player.config.EnvironmentConfig;
import jakarta.annotation.Resource;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.GL11.*;

/**
 * 图片组件
 */
public class Image {
    private float x,y,width,height;
    private String path;

    /**
     * 构建图片，默认为【比例模式】:-1 ~ 1
     * @param x 基于窗口中心点的相对 x轴位置
     * @param y 基于窗口中心点的相对 y轴位置
     * @param width 图片宽度
     * @param height 图片高度
     * @param path 图片路径
     */
    public Image(float x,float y,float width,float height,String path) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.path = path;
    }

    /**
     * 设置窗口参数,调用方法后 坐标参数由 【比例模式】 变更为 【坐标系模式】
     * @param width 窗口宽度
     * @param height 窗口高度
     * @return
     */
    public Image coordinate(int width,int height) {
        this.x = this.x/(width/2);
        this.y = this.y/(height/2);
        this.width = this.width/(width/2);
        this.height = this.height/(height/2);
        return this;
    }


    /**
     * 绘制
     */
    public Image draw() {
        //绘制背景
        glEnable(GL_TEXTURE_2D);
        int textures = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,textures);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_EDGE_FLAG);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_EDGE_FLAG);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        try {
            BufferedImage read = ImageIO.read(new File(path));
            int[] pixels = new int[read.getHeight()*read.getWidth()];
            read.getRGB(0,0,read.getWidth(),read.getHeight(),pixels,0,read.getWidth());
            // 转换整数像素数组到字节缓冲
            ByteBuffer buffer = ByteBuffer.allocateDirect(pixels.length * 4);
            buffer.order(ByteOrder.nativeOrder());
            for (int pixel : pixels) {
                buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
                buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green component
                buffer.put((byte) (pixel & 0xFF));        // Blue component
                // 如果需要Alpha通道，可以添加一个分量，例如：(byte) 0xFF
            }
            buffer.flip(); // 准备缓冲区以供读取
            // 上传纹理数据
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, read.getWidth(), read.getHeight(), 0,
                    GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
            buffer.clear();
            glBindTexture(textures,0);
            glColor3f(1, 1, 1); // 设置颜色
            glBegin(GL_POLYGON);
            glVertex2f(x,y);glTexCoord2f(0,0);
            glVertex2f(x,y+height);glTexCoord2f(1,0);
            glVertex2f(x+width,y+height);glTexCoord2f(1,1);
            glVertex2f(x+width,y);glTexCoord2f(0,1);
            glEnd();
        } catch (IOException e) {
            System.out.println("读取图片失败");
            throw new RuntimeException(e);
        }
        return this;
    }

}
