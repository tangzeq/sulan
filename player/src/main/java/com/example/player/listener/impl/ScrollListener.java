package com.example.player.listener.impl;

import com.example.player.config.EnvironmentConfig;
import com.example.player.listener.BaseListener;
import jakarta.annotation.Resource;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.springframework.stereotype.Component;

import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;

@Component
public class ScrollListener  extends EnvironmentConfig implements BaseListener {
    @Override
    public void listener(long windows) {
        //滚轮监控 鼠标滚轮或者手柄滚轮
        glfwSetScrollCallback(windows, new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xoffset, double yoffset) {
                offset = new double[]{xoffset,yoffset};
            }
        });
    }
}
