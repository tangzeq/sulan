package com.netty.customer.handler;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.netty.customer.message.core.BaseMessage;
import com.netty.customer.message.core.MapMessage;
import com.netty.customer.message.core.TextMessage;
import com.netty.customer.message.net.NodeNet;
import com.netty.customer.message.user.BaseUser;
import com.netty.customer.storage.MessageStorage;
import com.netty.customer.utils.ChannelUtils;
import com.netty.customer.utils.MessageUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.netty.customer.utils.ChannelUtils.remoteHost;
import static com.netty.customer.utils.ChannelUtils.remotePort;

/**
 * 功能描述：服务端信息处理
 * 作者：唐泽齐
 */
@ChannelHandler.Sharable
@Component
public class CustomerHandler extends ChannelInboundHandlerAdapter {
    static volatile public Cache<Long, Object> messageCache = CacheBuilder.newBuilder().expireAfterAccess(Duration.ofSeconds(60)).build();
    static volatile ConcurrentLinkedQueue<BaseMessage> queue = new ConcurrentLinkedQueue<>();
    static volatile Map<String, ChannelHandlerContext> remoteCache = new ConcurrentHashMap<>();
    static volatile int online = 0;
    @Resource
    @Lazy
    private ServerHandler serverHandler;
    @Resource
    private Bootstrap customer;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String hostString = remoteHost(ctx);
        int portString = ChannelUtils.remotePort(ctx);
        remoteCache.put(ctx.channel().id().toString(), ctx);
        serverHandler.setServerCache(hostString + ":" + portString);
        BaseMessage bm = BaseMessage.builder().id(ChannelUtils.makeId(ctx)).type(1).message(MapMessage.builder().message(serverHandler.getServerCache()).build()).build();
        queue.add(bm);
        BaseMessage cbm = BaseMessage.builder().id(ChannelUtils.makeId(ctx)).type(0).message(TextMessage.builder().message(serverHandler.host + ":" + serverHandler.port).build()).build();
        ctx.writeAndFlush(Unpooled.copiedBuffer((JSON.toJSONString(cbm) + "" + System.getProperty("line.separator")).getBytes("UTF-8")));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (!ctx.channel().isActive()) {
            rest(ctx);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        while (queue.size() > 0) {
            BaseMessage s = queue.poll();
            if (!ObjectUtils.isEmpty(s) && remoteCache.size() > 0) {
                if (ObjectUtils.isEmpty(s.getId())) {
                    s.setId(ChannelUtils.makeId(remoteCache.values().stream().findFirst().get()));
                    s.setChanleId(remoteCache.values().stream().findFirst().get().channel().id().toString());
                }
                if(digestion(s)) continue;
                //推送信息到本地服务
                if (ObjectUtils.isEmpty(serverHandler.messageCache.getIfPresent(s.getId()))) serverHandler.getMessage().add(s);
                //推送信息到外部服务
                if (!ObjectUtils.isEmpty(messageCache.getIfPresent(s.getId()))) continue;
                for (ChannelHandlerContext context : remoteCache.values()) {
                    while (!serverHandler.active) {
                        Thread.sleep(50);
                    }
//                    System.out.println(ChannelUtils.localHost(context) + ":" + ChannelUtils.localPort(context) + "客户端 向" + ChannelUtils.remoteHost(context) + ":" + ChannelUtils.remotePort(context) + "发送信息, msg = " + s);
                    Thread.startVirtualThread(new Runnable() {
                        @SneakyThrows
                        @Override
                        public void run() {
                            context.writeAndFlush(Unpooled.copiedBuffer((JSON.toJSONString(s) + "" + System.getProperty("line.separator")).getBytes("UTF-8")));
                        }
                    });
                }
            }
        }
        if (!ObjectUtils.isEmpty(msg) && !"\r\n".equals(msg)) {
            BaseMessage bm = MessageUtils.resolve(msg);
            messageCache.put(bm.getId(), 1);
            if(bm.getType().compareTo(5) == 0) MessageStorage.add(bm);
            if(!digestion(bm)) queue.add(bm);
        }

    }

    private boolean digestion(BaseMessage bm) {
        boolean rest = false;
        switch (bm.getType().intValue()) {
            case 1: {
                final Integer old = serverHandler.getServerCache().size();
                 MapMessage mapMessage= (MapMessage) bm.getMessage();
                serverHandler.getServerCache().putAll(mapMessage.getMessage());
                final Integer now = serverHandler.getServerCache().size();
//                System.out.println("ServerCache = " + serverHandler.getServerCache().values());
                if (old.compareTo(now) != 0) {
                    BaseMessage nbm = BaseMessage.builder().message(MapMessage.builder().message(serverHandler.getServerCache()).build()).type(1).build();
                    queue.add(nbm);
                    rest = true;
                }
                break;
            }
            case 2: {
                final Integer old = serverHandler.getServerCache().size();
                TextMessage textMessage = (TextMessage) bm.getMessage();
                if(serverHandler.getCustomerHost().containsValue(textMessage.getMessage())) {
                    BaseMessage nbm = BaseMessage.builder().message(textMessage).type(1).build();
                    queue.add(nbm);
                    rest = true;
                    break;
                }
                serverHandler.delServerCache(textMessage.getMessage());
                final Integer now = serverHandler.getServerCache().size();
//                System.out.println("ServerCache = " + serverHandler.getServerCache().values());
                if (old.compareTo(now) != 0) {
                    BaseMessage nbm = BaseMessage.builder().message(textMessage).type(2).build();
                    queue.add(nbm);
                    rest = true;
                }
                break;
            }
        }
        return rest;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (!ctx.channel().isActive()) {
            rest(ctx);
        }
    }

    @SneakyThrows
    private void rest(ChannelHandlerContext ctx) {
        serverHandler.active = false;
        Thread.sleep(1000);
        String hostString = remoteHost(ctx);
        int portString = ChannelUtils.remotePort(ctx);
        System.out.println(ctx.channel().id() + "从" + hostString + ":" + portString + "断开");
        remoteCache.remove(ctx.channel().id().toString(), ctx);
        boolean single = true;
        for (ChannelHandlerContext context : remoteCache.values()) {
            if(remoteHost(context).equals(hostString) &&remotePort(context).compareTo(Integer.valueOf(portString)) == 0 ) single = false;
        }
        if(single) serverHandler.delServerCache(hostString + ":" + portString);
        restremote();
        BaseMessage bm = BaseMessage.builder().type(2).message(TextMessage.builder().message(hostString + ":" + portString).build()).build();
        if(single) queue.add(bm);
        serverHandler.active = true;
    }


    public BaseMessage sendMessage(BaseUser mes) {
        BaseMessage message = BaseMessage.builder().type(5).message(mes).build();
        queue.add(message);
        return message;
    }

    public ConcurrentLinkedQueue<BaseMessage> getQueueQueue() {
        return queue;
    }

    /**
     * 外部重连
     */
    public void restremote() {
        if(remoteCache.size()<=0){
            boolean connect = false;
            for (Object context : serverHandler.getServerCache().values()) {
                String[] split = context.toString().split(":");
                if (split[0].equals(serverHandler.host) && Integer.valueOf(split[1]).compareTo(Integer.valueOf(serverHandler.port)) == 0)
                    continue;
                customer.connect(split[0], Integer.parseInt(split[1]));
                connect = true;
                System.out.println("远程" + split[0] + ":" + Integer.parseInt(split[1]) + "连接");
                break;
            }
            //自连接
            if (!connect) {
                customer.connect(serverHandler.host, serverHandler.port);
            }
        }
    }

    public void makeonLine() {
        if (online == 0) {
            System.out.println("客户端 激活");
            online++;
            Thread.startVirtualThread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        for (ChannelHandlerContext context : remoteCache.values()) {
                            context.writeAndFlush(Unpooled.copiedBuffer((System.getProperty("line.separator")).getBytes("UTF-8")));
                        }
                    }
                }
            });
        }
    }

    public List<NodeNet> getRemotes() {
        List<NodeNet> remotes = new ArrayList<>();
        for (Map.Entry<String, ChannelHandlerContext> entry : remoteCache.entrySet()) {
            remotes.add(NodeNet.builder().type(entry.getKey()).host(remoteHost(entry.getValue())).port(remotePort(entry.getValue())).build());
        }
        return remotes;
    }

    public Collection<ChannelHandlerContext> remotes() {
        Collection<ChannelHandlerContext> set = remoteCache.values();
        return set;
    }

}
