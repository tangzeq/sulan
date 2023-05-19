package com.netty.customer.configs;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 功能描述：netty
 * 作者：唐泽齐
 */
@Component
public class NettyBeans {

    @Bean("server")
    public ServerBootstrap makeServer() {
        return new ServerBootstrap();
    }

    @Bean("customer")
    public Bootstrap makeCustomer() {
        return new Bootstrap();
    }

}
