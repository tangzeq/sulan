package com.netty.customer.controller;

import com.netty.customer.message.user.BaseUser;
import com.netty.customer.message.user.LoginUser;
import com.netty.customer.storage.BaseMemory;
import com.netty.customer.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 功能描述：用户
 * 作者：唐泽齐
 */
@RestController
@CrossOrigin
@RequestMapping("user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    /**
     * 获取用户信息
     *
     * @param request
     * @return
     */
    @GetMapping("getUser")
    public BaseUser getUser(HttpServletRequest request) {
        return UserStorage.get(request).getBs();
    }

    /**
     * 调整名称
     *
     * @param request
     * @param name
     * @return
     * @throws Exception
     */
    @PutMapping("putName/{name}")
    public BaseUser putName(HttpServletRequest request, @PathVariable String name) throws Exception {
        BaseUser baseUser;
        BaseMemory user = UserStorage.get(request);
        if (ObjectUtils.isEmpty(user)) return new BaseUser();
        LoginUser bs = (LoginUser) user.getBs();
        bs.setName(name);
        baseUser = UserStorage.update(bs).getBs();
        return baseUser;
    }

    /**
     * 修改密码
     *
     * @param request
     * @return
     * @throws Exception
     */
    @PutMapping("changePassword")
    public BaseUser changePassword(HttpServletRequest request) throws Exception {
        BaseUser baseUser;
        String token = request.getHeader("token");
        String old = request.getHeader("old");
        String now = request.getHeader("now");
        Assert.notNull(token, "登录失效");
        Assert.notNull(old, "指定旧密码");
        Assert.notNull(now, "指定新密码");
        BaseMemory storage = UserStorage.changePassword(Long.valueOf(token), old, now);
        baseUser = storage.getBs();
        return baseUser;
    }
}
