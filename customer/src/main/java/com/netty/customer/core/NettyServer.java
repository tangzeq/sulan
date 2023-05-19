package com.netty.customer.core;

import com.netty.customer.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 功能描述：服务端
 * 作者：唐泽齐
 */
@Component
public class NettyServer {
    private NioEventLoopGroup accpt = new NioEventLoopGroup();
    private NioEventLoopGroup message = new NioEventLoopGroup();
    private boolean open = false;
    @Resource
    private ServerBootstrap server;

    public int makeServer(String host, AtomicInteger port, ServerHandler serverHandler) throws Throwable {
        serverHandler.setServerCache(host + ":" + port);
        serverHandler.host = host;
        serverHandler.port = port.get();
        serverHandler.makeonLine();
        if (open) {
            System.out.println("服务端更新绑定至 " + port);
            serverHandler.clear();
            server.bind(port.get());
            serverHandler.setServerCache(host + ":" + port);
            return port.get();
        }
        try {
            open = true;
            ChannelFuture sync = server
                    .group(accpt, message)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
                            ch.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                            ch.pipeline().addLast(new StringEncoder(Charset.forName("UTF-8")));
                            ch.pipeline().addLast(serverHandler);
                        }
                    })
                    .bind(port.get()).sync();
            port.set(((InetSocketAddress) sync.channel().localAddress()).getPort());
            serverHandler.port = port.get();
            System.out.println("server host = " + host + ", port = " + port);
            sync.channel().closeFuture().sync();
            return port.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//                    try {
//                        accpt.shutdownGracefully().sync();
//                        message.shutdownGracefully().sync();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
        }
        return port.get();
    }

    public void shutDown() {
        try {
            accpt.shutdownGracefully().sync();
            message.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            NettyServer server = new NettyServer();
            AtomicInteger port = new AtomicInteger(0);
            server.makeServer("192.168.0.158", port, new ServerHandler());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
