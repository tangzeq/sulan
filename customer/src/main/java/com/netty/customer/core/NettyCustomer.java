package com.netty.customer.core;

import com.netty.customer.handler.CustomerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.Collection;

import static com.netty.customer.utils.ChannelUtils.remoteHost;
import static com.netty.customer.utils.ChannelUtils.remotePort;

/**
 * 功能描述：客户端
 * 作者：唐泽齐
 */
@Component
public class NettyCustomer {
    private NioEventLoopGroup message = new NioEventLoopGroup();
    private boolean open = false;
    @Resource
    private Bootstrap customer;

    public void makerCustomer(String inetHost, int port, CustomerHandler customerHandler) throws Throwable {
        customerHandler.makeonLine();
        if (open) {
            System.out.println("客户端更新连接至 " + inetHost + ":" + port);
            final Collection<ChannelHandlerContext> remotes = customerHandler.remotes();
            customer.connect(inetHost, port);
            for (ChannelHandlerContext remote : remotes) remote.close();
            return;
        }
        try {
            open = true;
            if (customer == null) customer = new Bootstrap();
            ChannelFuture channelFuture = customer
                    .group(message)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
                            ch.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                            ch.pipeline().addLast(new StringEncoder(Charset.forName("UTF-8")));
                            ch.pipeline().addLast(customerHandler);
                        }
                    })
                    .connect(inetHost, port).sync();
            System.out.println("customer host = " + inetHost + ", port = " + port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//            try {
//                message.shutdownGracefully().sync();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    public void shutDown() {
        try {
            message.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            NettyCustomer customer = new NettyCustomer();
            customer.makerCustomer("localhost", 8888, new CustomerHandler());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


}
