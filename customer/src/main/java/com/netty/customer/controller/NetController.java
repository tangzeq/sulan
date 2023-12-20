package com.netty.customer.controller;

import com.netty.customer.handler.CustomerHandler;
import com.netty.customer.handler.ServerHandler;
import com.netty.customer.message.net.ServerNet;
import com.netty.customer.storage.BaseMemory;
import com.netty.customer.storage.UserStorage;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;



/**
 * 功能描述：网络信息
 * 作者：唐泽齐
 */
@RestController
@CrossOrigin
@RequestMapping("net")
@RequiredArgsConstructor
@Slf4j
public class NetController {

    @Resource
    private CustomerHandler customerHandler;
    @Resource
    private ServerHandler serverHandler;

    @GetMapping("getServerInfo")
    public ServerNet getServerInfo(HttpServletRequest request) {
        BaseMemory memory = UserStorage.get(request);
        Assert.notNull(memory,"验证信息异常");
        return ServerNet
                .builder()
                .serverhost(serverHandler.host)
                .serverport(serverHandler.port)
                .remotes(customerHandler.getRemotes())
                .channles(serverHandler.getChannles())
                .nodes(serverHandler.getNodes())
                .build();
    }

    @PutMapping("closeCustomer/{customerId}")
    public void closeCustomer(HttpServletRequest request,@PathVariable String customerId) {
        BaseMemory memory = UserStorage.get(request);
        Assert.notNull(memory,"验证信息异常");
        for (ChannelHandlerContext channle : serverHandler.channles()) {
            if(channle.channel().id().toString().equals(customerId)) channle.close();
        }
    }
}
