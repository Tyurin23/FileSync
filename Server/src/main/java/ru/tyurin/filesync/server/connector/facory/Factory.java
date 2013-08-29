package ru.tyurin.filesync.server.connector.facory;


import ru.tyurin.filesync.server.connector.Controller;
import ru.tyurin.filesync.server.connector.ServerSocketConnector;
import ru.tyurin.filesync.server.connector.Session;
import ru.tyurin.filesync.server.storage.StorageManager;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Factory {

	private String syncDirectory;
	private ServerSocketFactory serverSocketFactory;

	public Factory(String syncDirectory, ServerSocketFactory serverSocketFactory) {
		this.syncDirectory = syncDirectory;
		this.serverSocketFactory = serverSocketFactory;
	}

	public StorageManager createStorageManager() throws IOException {
		return new StorageManager(syncDirectory);
	}

	public Controller createController() throws IOException {
		return new Controller(createStorageManager());
	}

	public Session createSession() throws IOException {
		return new Session(createController());
	}

	public ServerSocketConnector createConnector(Socket socket) throws IOException {
		return new ServerSocketConnector(socket);
	}

	public ServerSocket createServerSocket(int port) throws IOException {
		return serverSocketFactory.createServerSocket(port);
	}


}
