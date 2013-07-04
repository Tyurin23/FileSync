package ru.tyurin.connector.impl;

import ru.tyurin.filesync.remote.Facade;

import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/1/13
 * Time: 5:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class FacadeImpl implements Facade {

	public FacadeImpl() {
		super();
	}

	@Override
	public int getStatus() throws RemoteException {
		return 1;
	}
}
