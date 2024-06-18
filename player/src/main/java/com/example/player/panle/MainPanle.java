package com.example.player.panle;

import com.example.player.config.EnvironmentConfig;
import com.example.player.listener.BaseListener;
import com.example.player.scene.BaseSene;
import com.example.player.utils.ImageUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.nio.*;
import java.util.*;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

@Component
public class MainPanle {
    @Resource
    EnvironmentConfig environmentConfig;
    @Resource
    Map<String,BaseSene> seneMap;
    @Resource
    List<BaseListener> listeners;
    @PostConstruct
    public void run() {
        //启动时将默认场景加入场景组
        environmentConfig.senes = new BaseSene[]{seneMap.get(environmentConfig.defaultSene)};
        initEv();
        loop();
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(environmentConfig.window);
        glfwDestroyWindow(environmentConfig.window);
        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
    private void initEv() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwWindowHint(GLFW_RED_BITS, vidmode.redBits());
        glfwWindowHint(GLFW_GREEN_BITS, vidmode.greenBits());
        glfwWindowHint(GLFW_BLUE_BITS, vidmode.blueBits());
        glfwWindowHint(GLFW_ALPHA_BITS, 0);
        glfwWindowHint(GLFW_REFRESH_RATE, vidmode.refreshRate());
        glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);
        glfwWindowHint(GLFW_ICONIFIED, GLFW_FALSE);
        environmentConfig.width = vidmode.width();
        environmentConfig.height = vidmode.height();
        // Create the window
        environmentConfig.window = glfwCreateWindow(environmentConfig.width, environmentConfig.height, "World", glfwGetPrimaryMonitor(), 0);
        if ( environmentConfig.window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
        // Get the thread stack and push a new frame
//        try ( MemoryStack stack = stackPush() ) {
//            IntBuffer pWidth = stack.mallocInt(1); // int*
//            IntBuffer pHeight = stack.mallocInt(1); // int*
//            // Get the window size passed to glfwCreateWindow
//            glfwGetWindowSize(environmentConfig.window, pWidth, pHeight);
//             Get the resolution of the primary monitor
//            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//            // Center the window
//            glfwSetWindowPos(
//                    environmentConfig.window,
//                    (vidmode.width() - pWidth.get(0)) / 2,
//                    (vidmode.height() - pHeight.get(0)) / 2
//            );
//        } // the stack frame is popped automatically
        // Make the OpenGL context current
        glfwMakeContextCurrent(environmentConfig.window);
        // Enable v-sync
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(environmentConfig.window);
        //加载监听
        for (BaseListener listener : listeners) {
            listener.listener(environmentConfig.window);
        }
        //初始化工具类
        ImageUtil.environmentConfig = environmentConfig;
        environmentConfig.seneMap = seneMap;
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(environmentConfig.window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
            //加载场景
            if(!ObjectUtils.isEmpty(environmentConfig.senes) && environmentConfig.senes.length>0) for (BaseSene sene : environmentConfig.senes) {
                sene.draw();
            }
            //绘制网格
            glfwSwapBuffers(environmentConfig.window); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }
}
