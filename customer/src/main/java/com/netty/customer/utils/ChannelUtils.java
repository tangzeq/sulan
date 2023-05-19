package com.netty.customer.utils;

import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * 功能描述：通信文档工具
 * 作者：唐泽齐
 */
public class ChannelUtils {

    public static String remoteHost(ChannelHandlerContext ctx) {
        String hostString = ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString();
        if("localhost".equals(hostString)) {
            hostString = "127.0.0.1";
        }
        return hostString;
    }

    public static String localHost(ChannelHandlerContext ctx) {
        String hostString = ((InetSocketAddress) ctx.channel().localAddress()).getHostString();
        if("localhost".equals(hostString)) {
            hostString = "127.0.0.1";
        }
        return hostString;
    }

    public static Integer remotePort(ChannelHandlerContext ctx) {
        return ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
    }

    public static Integer localPort(ChannelHandlerContext ctx) {
        return ((InetSocketAddress) ctx.channel().localAddress()).getPort();
    }

    /**
     * 信息ID生成 后续按照信息ID排序信息
     * @param ctx
     * @return
     */
    public static Long makeId(ChannelHandlerContext ctx) {
        return System.nanoTime();
    }
}
