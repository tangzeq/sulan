package com.netty.customer.handler;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.netty.customer.message.core.BaseMessage;
import com.netty.customer.message.core.TextMessage;
import com.netty.customer.message.net.NodeNet;
import com.netty.customer.utils.ChannelUtils;
import com.netty.customer.utils.MessageUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.netty.customer.utils.ChannelUtils.remoteHost;
import static com.netty.customer.utils.ChannelUtils.remotePort;

/**
 * 功能描述：服务端信息处理
 * 作者：唐泽齐
 */
@ChannelHandler.Sharable
@Component
public class ServerHandler extends ChannelInboundHandlerAdapter {
    static volatile public Cache<Long, Object> messageCache = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build();
    static volatile ConcurrentLinkedQueue<BaseMessage> message = new ConcurrentLinkedQueue<>();
    static volatile Map<String, ChannelHandlerContext> customerCache = new ConcurrentHashMap<>();
    static volatile Map<String, String> serverCache = new ConcurrentHashMap<String,String>();
    static volatile Map<String, String> customerHost = new ConcurrentHashMap<String,String>();
    static volatile int online = 0;
    static volatile public String host = "";
    static volatile public Integer port = -1;
    static volatile public boolean active = true;
    @Resource
    CustomerHandler customerHandler;
    @Resource
    private ExecutorService scheduledThreadPool;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        customerCache.put(ctx.channel().id().toString(), ctx);
//        System.out.println("customer size :" + customerCache.size());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        customerCache.remove(ctx.channel().id().toString(), ctx);
//        System.out.println("customer size :" + customerCache.size());
        String server = customerHost.remove(ctx.channel().id().toString());
        BaseMessage bm = BaseMessage.builder().id(ChannelUtils.makeId(ctx)).type(2).message(TextMessage.builder().message(server).build()).build();
        if(!customerHost.containsValue(server)) customerHandler.getQueueQueue().add(bm);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!ObjectUtils.isEmpty(msg) && !"\r\n".equals(msg)) {
            BaseMessage bm = MessageUtils.resolve(msg);
            if("0".equals(bm.getType().toString())) {
                customerHost.put(ctx.channel().id().toString(),((TextMessage)bm.getMessage()).getMessage());
            }
            else if (ObjectUtils.isEmpty(messageCache.getIfPresent(bm.getId()))) {
                message.add(bm);
//                System.out.println(ChannelUtils.localHost(ctx) + ":" + ChannelUtils.localPort(ctx) + "服务端 收到" + ChannelUtils.remoteHost(ctx) + ":" + ChannelUtils.remotePort(ctx) + "的信息, msg = " + bm);
            }
        }
    }

    public void makeonLine() {
        if (online == 0) {
            System.out.println("服务端 激活");
            online++;
            scheduledThreadPool.submit(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    while (true) {
                        Thread.sleep(50);
                        //信息
                        while (message.size() > 0) {
                            BaseMessage s = message.poll();
                            if (!ObjectUtils.isEmpty(s)) {
                                if (ObjectUtils.isEmpty(messageCache.getIfPresent(s.getId()))) {
                                    messageCache.put(s.getId(), 1);
                                    customerHandler.getQueueQueue().add(s);
                                    for (ChannelHandlerContext context : customerCache.values()) {
                                        while (!active) {
                                            Thread.sleep(50);
                                        }
//                                        System.out.println(ChannelUtils.localHost(context) + ":" + ChannelUtils.localPort(context) + "服务端 向" + ChannelUtils.remoteHost(context) + ":" + ChannelUtils.remotePort(context) + "发送信息, msg = " + s);
                                        scheduledThreadPool.submit(new Runnable() {
                                            @SneakyThrows
                                            @Override
                                            public void run() {
                                                context.writeAndFlush(Unpooled.copiedBuffer((JSON.toJSONString(s) + "" + System.getProperty("line.separator")).getBytes("UTF-8")));
                                            }
                                        });
                                    }
                                }
                            }
                        }
                        //保活
                        for (ChannelHandlerContext context : customerCache.values()) {
                            while (!active) {
                                Thread.sleep(50);
                            }
                            scheduledThreadPool.submit(new Runnable() {
                                @SneakyThrows
                                @Override
                                public void run() {
                                    context.writeAndFlush(Unpooled.copiedBuffer((System.getProperty("line.separator")).getBytes("UTF-8")));
                                }
                            });
                        }
                        //节点识别
                    }
                }
            });
        }
    }

    public Integer getOnline() {
        return online - 1;
    }

    public Map<String, String> getServerCache() {
        return serverCache;
    }

    public Map<String, String> setServerCache(String s) {
        serverCache.put(s.hashCode()+"",s);
        return serverCache;
    }

    public Map<String, String> delServerCache(String s) {
        try {
            Integer.valueOf(s);
            serverCache.remove(s);
        } catch (Throwable e) {
            serverCache.remove(s.hashCode()+"",s);
        }
        return serverCache;
    }

    public Map<String, String> getCustomerHost() {
        return customerHost;
    }
//
//    public Map<String, String> delCustomerHost(String s) {
//        try {
//            Integer.valueOf(s);
//            customerHost.remove(s);
//        } catch (Throwable e) {
//            customerHost.remove(s.hashCode()+"");
//        }
//        return customerHost;
//    }

    public ConcurrentLinkedQueue<BaseMessage> getMessage() {
        return message;
    }

    public List<NodeNet> getChannles() {
        List<NodeNet> channles = new ArrayList<>();
        for (Map.Entry<String, ChannelHandlerContext> entry : customerCache.entrySet()) {
            channles.add(NodeNet.builder().type(entry.getKey()).host(remoteHost(entry.getValue())).port(remotePort(entry.getValue())).build());
        }
        return channles;
    }

    public Collection<ChannelHandlerContext> channles() {
        return customerCache.values();
    }

    public List<NodeNet> getNodes() {
        List<NodeNet> nodes = new ArrayList<>();
        for (Map.Entry<String, String> entry : serverCache.entrySet()) {
            nodes.add(NodeNet.builder().type(entry.getKey()).host((entry.getValue().split(":"))[0]).port(Integer.valueOf((entry.getValue().split(":"))[1])).build());
        }
        return nodes;
    }

    public void clear() {
        customerCache.values().forEach(c -> c.close());
        serverCache.clear();
        customerHost.clear();
    }
}
