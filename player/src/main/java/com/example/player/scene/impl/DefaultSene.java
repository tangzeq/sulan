package com.example.player.scene.impl;

import com.example.player.config.EnvironmentConfig;
import com.example.player.element.Button;
import com.example.player.element.elementImpl.LeftMouseButton;
import com.example.player.element.Image;
import com.example.player.listener.BaseClickListener;
import com.example.player.scene.BaseSene;
import com.example.player.utils.ImageUtil;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

@Component
public class DefaultSene implements BaseSene, BaseClickListener {
    @Resource
    private EnvironmentConfig environmentConfig;
    private volatile Map<String, Button> buttons = new ConcurrentHashMap();
    private volatile Map<String,Boolean> modules = new ConcurrentHashMap();
    String background = "player/src/main/resources/images/defaultSene/background.png";
    @Override
    public void draw() {
        //加载背景
        new Image(-environmentConfig.width/2,-environmentConfig.height/2,environmentConfig.width,environmentConfig.height,background)
                .coordinate(environmentConfig.width,environmentConfig.height)
                .draw();
        //加载确定按钮
        if(!modules.containsKey("确定") || modules.get("确定")) {
            LeftMouseButton leftMouseButton = new LeftMouseButton(-environmentConfig.width / 2, -environmentConfig.height / 2, environmentConfig.width/2, environmentConfig.height/6, "确定")
                    .coordinate(environmentConfig.width, environmentConfig.height)
                    .color(1,0,1)
                    .onClick(new Runnable() {
                        @Override
                        public void run() {
                            environmentConfig.senes = new BaseSene[]{environmentConfig.seneMap.get("keyPhotoSene"),environmentConfig.seneMap.get("mousePhotoSene"),environmentConfig.seneMap.get("titlePhotoSene")};
                            modules.put("确定",false);
                            buttons.remove("确定");
                        }
                    })
                    .draw();
            buttons.put("确定", leftMouseButton);
        }
        //加载确定按钮
        if(!modules.containsKey("退出") || modules.get("退出")) {
            LeftMouseButton leftMouseButton = new LeftMouseButton(0, -environmentConfig.height / 2, environmentConfig.width/2, environmentConfig.height/6, "退出")
                    .coordinate(environmentConfig.width, environmentConfig.height)
                    .color(1,1,0)
                    .onClick(new Runnable() {
                        @Override
                        public void run() {
                            glfwSetWindowShouldClose(environmentConfig.window,true);
                            modules.put("退出",false);
                            buttons.remove("退出");
                        }
                    })
                    .draw();
            buttons.put("退出", leftMouseButton);
        }

    }

    @Override
    public void mouseClick(long window, int button, int action, int mods, float[] mouseCoord) {
        if(!ObjectUtils.isEmpty(buttons.values())) for (Button bu : buttons.values()) {
            bu.click(button,action,mouseCoord);
        }
    }
    @Override
    public void keyClick() {
    }
}
