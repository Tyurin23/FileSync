package ru.tyurin.connector;


import ru.tyurin.connector.impl.FacadeImpl;
import ru.tyurin.filesync.remote.Facade;

import java.io.IOException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerConnector {

	public ServerConnector() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
//			System.setSecurityManager(new SecurityManager());
		}
		Facade facade = new FacadeImpl();
		try {
//			Runtime.getRuntime().exec("rmiregistry 40001");
			Registry registry = LocateRegistry.getRegistry();
			Facade stub = (Facade) UnicastRemoteObject.exportObject(facade, 0);

			registry.rebind("facade", stub);
//			registry.bind("facade", stub);

			System.out.println("OK");
		} catch (RemoteException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
