package com.example.player.element.elementImpl;

import com.example.player.element.Button;
import com.example.player.element.Text;
import com.example.player.utils.ImageUtil;
import lombok.SneakyThrows;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.springframework.util.ObjectUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.GL11.*;

/**
 * 鼠标左键按钮组件
 */
public class LeftMouseButton implements Button, Text {
    private float x, y, width, height;
    private float red = 1, green = 1, blue = 1;
    private String text;
    private Runnable onClick;

    /**
     * 构建按钮，默认为【比例模式】:-1 ~ 1
     *
     * @param x      基于窗口中心点的相对 x轴位置
     * @param y      基于窗口中心点的相对 y轴位置
     * @param width  按钮宽度
     * @param height 按钮高度
     * @param text   按钮文本
     */
    public LeftMouseButton(float x, float y, float width, float height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
    }

    ;

    /**
     * 设置窗口参数,调用方法后 坐标参数由 【比例模式】 变更为 【坐标系模式】
     *
     * @param width  窗口宽度
     * @param height 窗口高度
     * @return
     */
    public LeftMouseButton coordinate(int width, int height) {
        this.x = this.x / (width / 2);
        this.y = this.y / (height / 2);
        this.width = this.width / (width / 2);
        this.height = this.height / (height / 2);
        return this;
    }

    public void checkPoint(int button, int action, float[] mouseCoord) {
        if (
            //必要参数判断：有鼠标坐标 有点击事件
                !ObjectUtils.isEmpty(mouseCoord) && mouseCoord.length >= 2 && !ObjectUtils.isEmpty(this.onClick)
                        //鼠标点击判断 左键 按下
                        && button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_TRUE
                        //鼠标坐标判断 元素框范围内
                        && mouseCoord[0] >= this.x && mouseCoord[0] <= this.x + width && mouseCoord[1] >= this.y && mouseCoord[1] <= this.y + height
        ) this.onClick.run();
    }

    public LeftMouseButton onClick(Runnable runnable) {
        this.onClick = runnable;
        return this;
    }

    /**
     * 设置颜色
     *
     * @param red
     * @param green
     * @param blue
     * @return
     */
    public LeftMouseButton color(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        return this;
    }

    public LeftMouseButton draw() {
        if(!ObjectUtils.isEmpty(text)) draw(text,x,y,width,height);
        else {
            glColor3f(red, green, blue); // 设置按钮颜色为淡蓝色
            glBegin(GL_QUADS);
            glVertex2f(x, y);
            glVertex2f(x + width, y);
            glVertex2f(x + width, y + height);
            glVertex2f(x, y + height);
            glEnd();
        }
        return this;
    }

    @Override
    public void click(int button, int action, float[] mouseCoord) {
        checkPoint(button,action,mouseCoord);
    }

    @SneakyThrows
    @Override
    public void draw(String text, float x, float y, float width, float height) {
        BufferedImage image = ImageUtil.textImage(text);
        // 在渲染透明纹理之前调用
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_TEXTURE_2D);
        int textures = glGenTextures();
        glBindTexture(GL_TEXTURE_2D,textures);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_EDGE_FLAG);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_EDGE_FLAG);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        int[] pixels = new int[image.getHeight()*image.getWidth()];
        image.getRGB(0,0,image.getWidth(),image.getHeight(),pixels,0,image.getWidth());
        // 转换整数像素数组到字节缓冲
        ByteBuffer buffer = ByteBuffer.allocateDirect(pixels.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        for (int pixel : pixels) {
            buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red component
            buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green component
            buffer.put((byte) (pixel & 0xFF));        // Blue component
            // 如果需要Alpha通道，可以添加一个分量，例如：(byte) 0xFF
            //用这个调整透明度
            buffer.put((byte) 127);
        }
        buffer.flip(); // 准备缓冲区以供读取
        // 上传纹理数据
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0,
                GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        buffer.clear();
        glBindTexture(textures,0);
        glColor3f(red, green, blue);; // 设置颜色
        glBegin(GL_POLYGON);
        glVertex2f(x,y);glTexCoord2f(0,0);
        glVertex2f(x,y+height);glTexCoord2f(1,0);
        glVertex2f(x+width,y+height);glTexCoord2f(1,1);
        glVertex2f(x+width,y);glTexCoord2f(0,1);
        glEnd();
        glDisable(GL_BLEND);
    }
}
