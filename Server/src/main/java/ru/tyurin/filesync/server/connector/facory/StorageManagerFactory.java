package ru.tyurin.filesync.server.connector.facory;

import ru.tyurin.filesync.server.storage.StorageManager;

import java.io.IOException;

/**
 * User: tyurin
 * Date: 8/29/13
 * Time: 9:05 AM
 */
public class StorageManagerFactory {

	private String rootDirectory;

	public StorageManagerFactory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	public StorageManager createStorageManager() throws IOException {
		return new StorageManager(rootDirectory);
	}
}
