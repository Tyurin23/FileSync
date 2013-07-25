package ru.tyurin.filesync.server.connector;


import ru.tyurin.filesync.server.connector.impl.FacadeImpl;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;

public class ServerConnector extends AbstractConnector {

	public ServerConnector() {
		if (System.getSecurityManager() == null) {
//			System.setSecurityManager(new RMISecurityManager());
//			System.setSecurityManager(new SecurityManager());
		}

		try {
//			Runtime.getRuntime().exec("rmiregistry 40001");
//			Registry registry = LocateRegistry.getRegistry();

//			System.setProperty("java.rmi.server.hostname", "localhost");

//			Facade facade = new FacadeImpl();
//			Facade stub = (Facade) UnicastRemoteObject.exportObject(facade, 0);
			FacadeImpl stub = new FacadeImpl();
			Naming.rebind("rmi://10.1.6.161:1099/facade", stub);
//			registry.bind("facade", stub);

			System.out.println("OK");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {

	}
}
