package com.example.player.listener.impl;

import com.example.player.config.EnvironmentConfig;
import com.example.player.listener.BaseListener;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

@Component
public class KeyListener implements BaseListener {
    @Resource
    EnvironmentConfig environmentConfig;
    @Override
    public void listener(long windows) {
        //键盘按键监控
        glfwSetKeyCallback(windows, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if(action == GLFW_PRESS && (key == GLFW_KEY_W || key == GLFW_KEY_S || key == GLFW_KEY_A || key == GLFW_KEY_D)) environmentConfig.keySet.add(key);
            if(action == GLFW_RELEASE && (key == GLFW_KEY_W || key == GLFW_KEY_S || key == GLFW_KEY_A || key == GLFW_KEY_D)) environmentConfig.keySet.remove(key);
        });
    }
}
