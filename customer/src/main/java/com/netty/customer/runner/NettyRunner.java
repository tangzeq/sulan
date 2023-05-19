package com.netty.customer.runner;

import com.netty.customer.core.NettyCustomer;
import com.netty.customer.core.NettyServer;
import com.netty.customer.handler.CustomerHandler;
import com.netty.customer.handler.ServerHandler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.awt.*;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static com.netty.customer.utils.NetUtils.host;

/**
 * 功能描述：netty自启动程序
 * 作者：唐泽齐
 */
@Configuration
public class NettyRunner implements ApplicationRunner, Ordered {
    @Resource
    private Environment environment;
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

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String host = host();
        AtomicInteger port = new AtomicInteger(0);
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    nettyServer.makeServer(host, port, serverHandler);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        while (port.get() <= 0) {
        }
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    nettyServer.makeServer(host, port, serverHandler);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    nettyCustomer.makerCustomer(host, port.get(), customerHandler);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        // Launch browser
        Runtime.getRuntime().exec("explorer \""+"http://"+host+":"+Integer.parseInt(environment.getProperty("local.server.port"))+"\"");
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private String[] getCommand(String url) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return new String[]{"rundll32", "url.dll,FileProtocolHandler",url};
        } else if (os.contains("mac")) {
            return new String[]{"open",url};
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return new String[]{"xdg-open",url};
        }
        return null;
    }
}
