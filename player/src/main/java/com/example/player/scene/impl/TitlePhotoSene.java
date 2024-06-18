package com.example.player.scene.impl;

import com.example.player.scene.BaseSene;
import org.lwjgl.opengl.GL11;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.GL11.*;

@Component
public class TitlePhotoSene implements BaseSene {
    float[][] imageTitleCoord = new float[][]{
            {-0.2f,-0.2f},//左下角
            {-0.2f,0.2f},//左上角
            {0.2f,0.2f},//右上角
            {0.2f,-0.2f},//右下角
    };
    @Override
    public void draw() {
        glColor3f(1,1,1);
        glEnable(GL_TEXTURE_2D);
        int textures = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,textures);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_EDGE_FLAG);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_EDGE_FLAG);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        try {
            BufferedImage read = ImageIO.read(new File("player/src/main/resources/images/mousechange.jpg"));
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
            glBegin(GL_POLYGON);
            glVertex2f(imageTitleCoord[0][0],imageTitleCoord[0][1]);glTexCoord2f(0,0);
            glVertex2f(imageTitleCoord[1][0],imageTitleCoord[1][1]);glTexCoord2f(1,0);
            glVertex2f(imageTitleCoord[2][0],imageTitleCoord[2][1]);glTexCoord2f(1,1);
            glVertex2f(imageTitleCoord[3][0],imageTitleCoord[3][1]);glTexCoord2f(0,1);
            glEnd();

        } catch (IOException e) {
            System.out.println("读取图片失败");
            throw new RuntimeException(e);
        }
    }
}