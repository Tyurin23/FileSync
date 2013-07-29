package ru.tyurin.filesync.server.connector.facory;

import ru.tyurin.filesync.server.connector.Connector;
import ru.tyurin.filesync.server.connector.ServerSocketConnector;

import java.io.IOException;
import java.net.Socket;

public class ConnectorFactory {

	public Connector createConnector(Socket socket) throws IOException {
		return new ServerSocketConnector(socket);
	}
}
