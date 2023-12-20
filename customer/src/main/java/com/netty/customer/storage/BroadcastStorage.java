package com.netty.customer.storage;

import com.netty.customer.broadcast.BroadCastCore;
import com.netty.customer.message.user.BaseUser;
import jakarta.annotation.Resource;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.netty.customer.storage.FileStorage.putFile;

/**
 * 功能描述：音视频存储桶
 * 作者：唐泽齐
 */
@Component
public class BroadcastStorage extends BaseUser implements Serializable {

    private static volatile Cache<String, BaseBroadcast> broadcast;

    @Resource
    public void makeCahe(CacheManager cacheManager) {
        broadcast = cacheManager.getCache("Broadcast", String.class, BaseBroadcast.class);
    }

    public static String push(BaseUser user, MultipartFile file) throws IOException {
        synchronized (user) {
            final String time = user.getUserId() + "-" + System.nanoTime();
            BaseBroadcast broad = new BaseBroadcast();
            BeanUtils.copyProperties(user, broad);
            broad.setTime(System.currentTimeMillis());
            broad.setFile(putFile(user.getUserId(), "broadcast", file));
            broad.getFile().setName(BroadCastCore.addFile(user.getUserId(), file));
            broadcast.put(time, broad);
            return time;
        }
    }

    public static BaseBroadcast get(String time) {
        if (broadcast.containsKey(time)) {
            return broadcast.get(time);
        } else {
            return null;
        }
    }

}
