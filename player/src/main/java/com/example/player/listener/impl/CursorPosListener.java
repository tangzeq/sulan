package com.example.player.listener.impl;

import com.example.player.config.EnvironmentConfig;
import com.example.player.listener.BaseListener;
import jakarta.annotation.Resource;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.springframework.stereotype.Component;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;

@Component
public class CursorPosListener implements BaseListener {

    @Resource
    EnvironmentConfig environmentConfig;
    @Override
    public void listener(long windows) {
        //鼠标指针监控
        glfwSetCursorPosCallback(windows, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                environmentConfig.mouseCoord = new float[]{(float) ((xpos-(environmentConfig.width/2))/(environmentConfig.width/2)), (float) (-(ypos-environmentConfig.height/2)/(environmentConfig.height/2))};
            }
        });
    }
}
