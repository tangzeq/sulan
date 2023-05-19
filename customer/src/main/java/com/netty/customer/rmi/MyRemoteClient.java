package com.netty.customer.rmi;

/**
 * 功能描述：
 * 作者：唐泽齐
 */
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MyRemoteClient {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1");
            MyRemote service = (MyRemote) registry.lookup("MyRemote");
            String message = service.sayHello();
            System.out.println(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
