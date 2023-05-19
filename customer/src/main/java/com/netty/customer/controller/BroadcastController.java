package com.netty.customer.controller;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.netty.customer.message.user.BaseUser;
import com.netty.customer.storage.BaseMemory;
import com.netty.customer.storage.BroadcastStorage;
import com.netty.customer.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述：音视频直播
 * 作者：唐泽齐
 */
@RestController
@RequestMapping("broadcast")
@RequiredArgsConstructor
@Slf4j
public class BroadcastController {

    private static volatile ConcurrentHashMap<Long, FluxProcessor<String, String>> processors = new ConcurrentHashMap<Long, FluxProcessor<String, String>>();
    private static volatile Cache<Long, BaseUser> users = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.SECONDS).build();

    @GetMapping("show/{user}")
    public Flux show(@PathVariable Long user, HttpServletResponse response) throws Exception {
        if (!users.asMap().keySet().contains(user)) {
            throw new Exception("用户未直播");
        }
        return processors.get(user).map(time -> {
            return BroadcastStorage.get(time);
        });
    }

    @PostMapping("upload")
    public ResponseEntity handleVideoUpload(HttpServletRequest request, @RequestParam("file") MultipartFile file) throws Throwable {
        BaseMemory user = UserStorage.get(request);
        if (ObjectUtils.isEmpty(user)) throw new Exception("请登录");
        String time = BroadcastStorage.push(user.getBs(), file);
        if (!processors.containsKey(user.getBs().getUserId())) {
            processors.put(user.getBs().getUserId(), DirectProcessor.<String>create().serialize());
        } else {
            processors.get(user.getBs().getUserId()).onNext(time);
        }
        users.put(user.getBs().getUserId(),user.getBs());
        return ResponseEntity.ok(null);
    }

    @GetMapping("broadcasts")
    public List<BaseUser> broadcasts(HttpServletRequest request) throws Throwable {
        return new ArrayList<>(users.asMap().values());
    }
}
