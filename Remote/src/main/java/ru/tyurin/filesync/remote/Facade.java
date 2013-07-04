package ru.tyurin.filesync.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/1/13
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Facade extends Remote {

	public static final String name = "Facade";

	public int getStatus() throws RemoteException;
}
