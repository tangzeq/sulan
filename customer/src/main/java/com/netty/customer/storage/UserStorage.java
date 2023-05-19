package com.netty.customer.storage;

import cn.hutool.core.net.NetUtil;
import com.alibaba.fastjson.JSON;
import com.netty.customer.message.user.BaseUser;
import com.netty.customer.message.user.LoginUser;
import com.netty.customer.utils.Md5Utils;
import lombok.SneakyThrows;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 功能描述：用户信息库
 * 作者：唐泽齐
 */
@Component
public class UserStorage {

    private static volatile  Cache<Long, BaseMemory> user;

    @Resource
    public void makeCahe(CacheManager cacheManager) {
        user = cacheManager.getCache("User", Long.class, BaseMemory.class);
    }

    public static synchronized BaseMemory login(LoginUser lu) {
        Long index = makeIndex(lu.getUsername());
        BaseMemory storage = null;
        if(user.containsKey(index)) storage = user.get(index);
        if (ObjectUtils.isEmpty(storage)) {
            storage = new BaseMemory();
            lu.setUserId(index);
            lu.setIndex(index);
            lu.setTime(System.currentTimeMillis());
            lu.setName("SL" + lu.getTime().toString());
            storage.setIndex(index);
            storage.setBs(lu);
            storage.setMemory(new ConcurrentHashMap());
            user.put(index,storage);
            return storage;
        } else {
            LoginUser bs = (LoginUser) storage.getBs();
            return safeCompareTo(lu.getPassword(), bs.getPassword()) ? storage : null;
        }
    }

    @SneakyThrows
    public static BaseMemory get(HttpServletRequest request) {
        try {
            getUser(Long.valueOf(request.getHeader("token")));
        } catch (Throwable e) {
            throw new Exception("未登录！");
        }
        return getUser(Long.valueOf(request.getHeader("token")));
    }

    public static BaseMemory update(LoginUser lu) throws Exception {
        BaseMemory storage = user.get(lu.getUserId());
        if(ObjectUtils.isEmpty(storage)) {
            throw new Exception("用户不存在");
        }
        storage.getBs().setName(lu.getName());
        storage.getBs().setPicture(lu.getPicture());
        user.put(lu.getUserId(),storage);
        return storage;
    }

    public static BaseMemory changePassword(Long userId, String old, String now) throws Exception {
        BaseMemory storage = user.get(userId);
        if(ObjectUtils.isEmpty(storage)) {
            throw new Exception("用户不存在");
        }
        if (!safeCompareTo(old, ((LoginUser) storage.getBs()).getPassword())) {
            throw new Exception("原密码错误");
        }
        ((LoginUser) storage.getBs()).setPassword(now);
        user.put(userId,storage);
        return storage;
    }

    public static BaseMemory getUser(Long userId) {
        return user.get(userId);
    }

    private static Long makeIndex(String username) {
        return Math.abs(Long.valueOf(Md5Utils.getMD5((username+ NetUtil.getLocalMacAddress()).getBytes()).hashCode()));
    }

    private static boolean safeCompareTo(String str1, String str2) {
        boolean equals = true;
        char v1[] = str1.toCharArray();
        char v2[] = str2.toCharArray();
        int lim = str1.length();
        int k = 0;
        while (k < lim) {
            char c1 = v1[k];
            char c2 = v2[k];
            if (c1 != c2) {
                equals = false;
            }
            k++;
        }
        return equals;
    }
}
