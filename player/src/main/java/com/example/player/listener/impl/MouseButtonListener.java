package com.example.player.listener.impl;

import com.example.player.config.EnvironmentConfig;
import com.example.player.listener.BaseClickListener;
import com.example.player.listener.BaseListener;
import jakarta.annotation.Resource;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;

@Component
public class MouseButtonListener implements BaseListener {
    @Resource
    EnvironmentConfig environmentConfig;
    @Resource
    List<BaseClickListener> clickListeners;
    @Override
    public void listener(long windows) {
        //鼠标按键监控
        glfwSetMouseButtonCallback(windows, new GLFWMouseButtonCallback() {
            /**
             *
             * @param window the window that received the event
             * @param button the mouse button that was pressed or released
             * @param action the button action. One of:<br><table><tr><td>{@link GLFW#GLFW_PRESS PRESS}</td><td>{@link GLFW#GLFW_RELEASE RELEASE}</td></tr></table>
             * @param mods   bitfield describing which modifiers keys were held down
             */
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if(!ObjectUtils.isEmpty(clickListeners)) for (BaseClickListener clickListener : clickListeners) {
                    clickListener.mouseClick(window,button,action,mods,environmentConfig.mouseCoord);
                }
            }
        });
    }
}
