package ru.tyurin.filesync.server.connector.facory;

import ru.tyurin.filesync.server.connector.ServerSocketConnector;

import java.io.IOException;
import java.net.Socket;

public class ConnectorFactory {

	public ServerSocketConnector createConnector(Socket socket) throws IOException {
		return new ServerSocketConnector(socket);
	}
}
