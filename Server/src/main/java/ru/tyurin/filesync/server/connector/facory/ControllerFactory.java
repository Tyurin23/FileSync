package ru.tyurin.filesync.server.connector.facory;


import ru.tyurin.filesync.server.connector.Controller;

import java.io.IOException;

public class ControllerFactory {

	private StorageManagerFactory storageManagerFactory;

	public ControllerFactory(StorageManagerFactory storageManagerFactory) {
		this.storageManagerFactory = storageManagerFactory;
	}

	public Controller createController() throws IOException {
		return new Controller(storageManagerFactory.createStorageManager());
	}
}
