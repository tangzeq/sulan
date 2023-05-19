package com.netty.customer.controller;

import com.netty.customer.core.NettyCustomer;
import com.netty.customer.core.NettyServer;
import com.netty.customer.handler.CustomerHandler;
import com.netty.customer.handler.ServerHandler;
import com.netty.customer.message.user.BaseUser;
import com.netty.customer.message.user.LoginUser;
import com.netty.customer.storage.BaseMemory;
import com.netty.customer.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 功能描述：登录相关
 * 作者：唐泽齐
 */
@RestController
@RequestMapping("login")
@RequiredArgsConstructor
@Slf4j
public class LoginController {
    @Resource
    private ExecutorService fixedThreadPool;
    @Resource
    private ServerHandler serverHandler;
    @Resource
    private NettyServer nettyServer;
    @Resource
    private NettyCustomer nettyCustomer;
    @Resource
    private CustomerHandler customerHandler;

    /**
     * 用户登录
     * @param request
     * @param response
     */
    @GetMapping("login")
    public BaseUser login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getHeader("username");
        String password = request.getHeader("password");
        BaseMemory user = UserStorage.login(LoginUser.builder().username(username).password(password).build());
        //利用报错 登录失败 user == null
        response.setHeader("Expires",user.getIndex().toString());
        BaseUser baseUser = new BaseUser();
        BeanUtils.copyProperties(user.getBs(),baseUser);
        return baseUser;
    }

    /**
     * 服务机登录
     * @param host
     * @param port
     * @return
     */
    @GetMapping("server/{host}/{port}")
    public String server(HttpServletRequest request,@PathVariable String host, @PathVariable Integer port) throws Exception {
        AtomicInteger integer = new AtomicInteger(port);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    nettyServer.makeServer(host, integer, serverHandler);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        return "主机服务开启" + port;
    }


    /**
     * 客户机登录
     * @param inetHost
     * @param port
     * @return
     */
    @GetMapping("connect/{inetHost}/{port}")
    public String connect(HttpServletRequest request,@PathVariable String inetHost, @PathVariable Integer port) throws Exception {
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    nettyCustomer.makerCustomer(inetHost, port, customerHandler);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        return "与" + inetHost + ":" + port + "建立连接";
    }
}
