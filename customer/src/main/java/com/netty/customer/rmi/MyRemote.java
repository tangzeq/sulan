package com.netty.customer.rmi;

/**
 * 功能描述：
 * 作者：唐泽齐
 */
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MyRemote extends Remote {

    public String sayHello() throws RemoteException;

}
