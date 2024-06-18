package com.example.player.scene.impl;

import com.example.player.config.EnvironmentConfig;
import com.example.player.scene.BaseSene;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

@Component
public class GridSene implements BaseSene {
    @Resource
    EnvironmentConfig environmentConfig;
    List<float[]> dxs=new ArrayList<>();
    float[] defaultCoord = new float[]{0,0};
    @Override
    public void draw() {
        for (Integer keys : environmentConfig.keySet) {
            if(keys == GLFW_KEY_W) defaultCoord[1]+=0.01;
            if(keys == GLFW_KEY_S) defaultCoord[1]-=0.01;
            if(keys == GLFW_KEY_D) defaultCoord[0]+=0.01;
            if(keys == GLFW_KEY_A) defaultCoord[0]-=0.01;
            defaultCoord[1] = defaultCoord[1]<-1?1:defaultCoord[1];
            defaultCoord[1] = defaultCoord[1]>1?-1:defaultCoord[1];
            defaultCoord[0] = defaultCoord[0]<-1?1:defaultCoord[0];
            defaultCoord[0] = defaultCoord[0]>1?-1:defaultCoord[0];
        }
        dxs.clear();
        dxs.add(environmentConfig.mouseCoord);
        dxs.add(defaultCoord);
        glBegin(GL_LINE_LOOP);
        for (float[] dx : dxs) {
            glVertex2f(dx[0],dx[1]);
        }
        glEnd(); // 结束绘制
    }
}
