package com.netty.customer.rmi;

/**
 * 功能描述：
 * 作者：唐泽齐
 */
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MyRemoteImpl extends UnicastRemoteObject implements MyRemote {

    public MyRemoteImpl() throws RemoteException {}

    public String sayHello() throws RemoteException {
        return "Hello, world!";
    }

    public static void main(String[] args) {
        try {
            MyRemote service = new MyRemoteImpl();
            Naming.rebind("MyRemote", service);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
