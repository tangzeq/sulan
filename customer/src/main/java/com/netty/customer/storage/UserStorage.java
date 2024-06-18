package com.netty.customer.storage;

import cn.hutool.core.net.NetUtil;
import com.alibaba.fastjson.JSON;
import com.netty.customer.message.user.BaseUser;
import com.netty.customer.message.user.LoginUser;
import com.netty.customer.utils.Md5Utils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import javax.lang.model.element.Modifier.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 功能描述：用户信息库
 * 作者：唐泽齐
 */
@Component
public class UserStorage {

    private static volatile  Cache<Long, BaseMemory> user;

    public static List<LoginUser> userList() {
        List<LoginUser> list = new ArrayList<>();
        Iterator<Cache.Entry<Long, BaseMemory>> iterator = user.iterator();
        while (iterator.hasNext()) {
            list.add((LoginUser) iterator.next().getValue().getBs());
        }
        return list;
    }


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

    public static void del(Long userId) {
        user.remove(userId);
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

    public static BaseMemory add(LoginUser lu) {
        Assert.isTrue(!ObjectUtils.isEmpty(lu.getUsername()),"请确认用户账号");
        Long index = makeIndex(lu.getUsername());
        BaseMemory storage = null;
        if(user.containsKey(index)) storage = user.get(index);
        Assert.isTrue(!ObjectUtils.isEmpty(storage),"用户已存在！");
        lu.setUserId(index);
        lu.setIndex(index);
        lu.setTime(System.currentTimeMillis());
        storage.setIndex(index);
        storage.setBs(lu);
        storage.setMemory(new ConcurrentHashMap());
        user.put(index,storage);
        return storage;
    }

    public static BaseMemory update(LoginUser lu) throws Exception {
        BaseMemory storage = user.get(lu.getUserId());
        if(ObjectUtils.isEmpty(storage)) {
            throw new Exception("用户不存在");
        }
        storage.setBs(lu);
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

    /**
     *
     * @param now 对比的数据
     * @param old 存储的数据
     * @return
     */
    private static boolean safeCompareTo(String now, String old) {
        boolean equals = true;
        char v1[] = now.toCharArray();
        char v2[] = old.toCharArray();
        //使用old的长度，固定比较次数，防止比较次数过短或者过长
        int lim = old.length();
        int k = 0;
        while (k < lim) {
            char c1 = v1[k];
            char c2 = v2[k];
            if (c1 != c2) {
                equals = false;
            }
            k++;
        }
        //增加一次长度对比
        if(now.length() != old.length()) {
            equals = false;
        }
        return equals;
    }
}
