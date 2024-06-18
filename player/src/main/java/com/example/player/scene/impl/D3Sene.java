package com.example.player.scene.impl;

import ch.qos.logback.core.testUtil.RandomUtil;
import com.example.player.config.EnvironmentConfig;
import com.example.player.scene.BaseSene;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

@Component
public class D3Sene implements BaseSene {
    @Resource
    private EnvironmentConfig environmentConfig;
    int x = 0;
    int y = 0;
    int z = 0;
    double size = 100;
    float[][] d3Coord = new float[][]{
            {-0.5f, -0.5f,  0.5f},
            {0.5f, -0.5f,  0.5f},
            {0.5f,  0.5f,  0.5f},
            {-0.5f,  0.5f,  0.5f},
            {-0.5f,  0.5f, -0.5f},
            {0.5f,  0.5f, -0.5f},
            {0.5f, -0.5f, -0.5f},
            {-0.5f, -0.5f, -0.5f},
            {0f, 0f, -1f},
            {0f, 0f, 1f},
            {0f, -1f, 0f},
            {0f, 1f, 0f},
            {-1f, 0f, 0f},
            {1f, 0f, 0f},
    };
    @Override
    public void draw() {
        size += environmentConfig.offset[1];
        environmentConfig.offset[1]= 0;
        glLoadIdentity();
        glMatrixMode(GL_PROJECTION);
        for (Integer keys : environmentConfig.keySet) {
            if (keys == GLFW_KEY_W) y += 10;
            if (keys == GLFW_KEY_S) y -= 10;
            if (keys == GLFW_KEY_D) x += 10;
            if (keys == GLFW_KEY_A) x -= 10;
        }
        glRotatef(x, 0, 1, 0);
        glRotatef(y, 1, 0, 0);
//        glRotatef(z, 0, 0, 1);
        glScalef((float) (size/200f), (float) (size/200f), (float) (size/200f));
        glBegin(GL_POLYGON); // 使用GL_QUADS来绘制矩形的四个面
        float[] color = new float[]{0,1,0};
        Random random = new Random();
        for (float[][] floats : pointToPlane(d3Coord)) {
//            color[random.nextInt(0,2)]+=0.2f;
            glColor3f(color[0],color[1],color[2]);
            for (float[] aFloat : floats) {
                glVertex3f(aFloat[0],aFloat[1],aFloat[2]);
            }
        }
        glEnd();
    }
    /**
     * 从三维坐标组中生成顺序的三角形面
     * 要求 plane>=type
     * @param points 三维坐标点集合
     * @return
     */
    private ArrayList<float[][]> pointToPlane(float[][] points) {
        ArrayList<float[][]> list = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            for (int j = i+1; j < points.length; j++) {
                for (int m = j+1; m < points.length; m++) {
                    list.add(new float[][]{points[i],points[j],points[m]});
                }
            }
        }
        return list;
    }

    /**
     * 判断两个多边形是否相交
     * @param image1
     * @param image2
     * @return
     */
    private boolean touch(float[][] image1,float[][] image2) {
        Path2D.Float a1 = new Path2D.Float();
        a1.moveTo(image1[0][0],image1[0][1]);
        for (int i=1;i<image1.length;i++) a1.lineTo(image1[i][0],image1[i][1]);
        a1.closePath();

        Path2D.Float a2 = new Path2D.Float();
        a2.moveTo(image2[0][0],image2[0][1]);
        for (int i=1;i<image2.length;i++) a2.lineTo(image2[i][0],image2[i][1]);
        a2.closePath();

        Area area1 = new Area(a1);
        Area area2 = new Area(a2);
        if(area1.equals(area2)) return false;
        area1.intersect(area2);
        return !area1.isEmpty();
    }

}
