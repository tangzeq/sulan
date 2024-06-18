package com.netty.customer.controller;

import com.alibaba.fastjson.JSON;
import com.netty.customer.handler.CustomerHandler;
import com.netty.customer.message.core.BaseMessage;
import com.netty.customer.message.core.TextMessage;
import com.netty.customer.storage.BaseLink;
import com.netty.customer.storage.MessageStorage;
import com.netty.customer.storage.UserStorage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static com.netty.customer.utils.ChatGPTUtils.chat;

/**
 * 功能描述：信息传输
 * 作者：唐泽齐
 */
@RestController
@RequestMapping("message")
@RequiredArgsConstructor
@Slf4j
public class MessageCtroller {
    @Resource
    private CustomerHandler customerHandler;

    @PostMapping("sendMessage")
    public BaseMessage<TextMessage> sendMessage(HttpServletRequest request, @RequestBody TextMessage message) {
        BeanUtils.copyProperties(UserStorage.get(request).getBs(), message);
        return customerHandler.sendMessage(message);
    }

    @PostMapping("sendChatGPT")
    public BaseMessage<TextMessage> sendChatGPT(HttpServletRequest request, @RequestBody TextMessage message) {
        BeanUtils.copyProperties(UserStorage.get(request).getBs(), message);
        Thread.startVirtualThread(new Runnable() {
            @Override
            public void run() {
                try {
                    message.setMessage(chat(message.getUserId().toString(), message.getMessage()));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                message.setUserId(0l);
                message.setName("ChatGPT");
                customerHandler.sendMessage(message);
            }
        });
        return customerHandler.sendMessage(message);
    }

    @GetMapping(value = "onLineMessage/{type}/{index}")
    public Flux<byte[]> onLineMessage(HttpServletRequest request, @PathVariable Integer type, @PathVariable Long index) {
        index = index.compareTo(0l) == 0 ? UserStorage.get(request).getMemory().get(type) == null ? 0l : (Long) UserStorage.get(request).getMemory().get(type) : index;
        AtomicLong setp = new AtomicLong(index);
        return Flux
                .interval(Duration.ofMillis(1))
                .limitRate(5)
                .doOnError(e -> System.out.println())
                .retry()
                .publishOn(Schedulers.boundedElastic())
                .map(i -> {
                    BaseLink storage = MessageStorage.read(type, setp.get());
                    while (ObjectUtils.isEmpty(storage)) {
                        try {
                            Thread.sleep(200);
                            storage = MessageStorage.read(type, setp.get());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    setp.set(storage.getIndex());
                    UserStorage.get(request).getMemory().put(type, setp.get());
                    try {
                        return JSON.toJSONString(storage).getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
