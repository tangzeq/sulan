package com.netty.customer.utils;

import lombok.SneakyThrows;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 功能描述：网络工具
 * 作者：唐泽齐
 */
public class NetUtils {

    @SneakyThrows
    public static String host() {
        return InetAddress.getLocalHost().getHostAddress();
    }

    @SneakyThrows
    public static List<String> hosts() {
        List<String> hosts = new ArrayList<>();
        Enumeration<NetworkInterface> interfaceList = NetworkInterface.getNetworkInterfaces();
        while (interfaceList.hasMoreElements()) {
            NetworkInterface iface = interfaceList.nextElement();
            Enumeration<InetAddress> addrList = iface.getInetAddresses();
            while (addrList.hasMoreElements()) {
                InetAddress address = addrList.nextElement();
                if (!address.isLinkLocalAddress() && !address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                    hosts.add(address.getHostAddress());
                }
            }
        }
        return hosts;
    }

    public static int port() {
        int port = 1024;
        for (; port <= 65535; port++) {
            try {
                MulticastSocket ignored = new MulticastSocket(port);
                ignored.close();
                System.out.println("端口 " + port + " 可用");
                break;
            } catch (IOException ignored) {
                System.out.println("端口 " + port + " 已被占用");
            }
        }
        return port;
    }

    public static void main(String[] args) {
        port();
    }

}
