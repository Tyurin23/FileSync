package ru.tyurin.filesync.server.connector.facory;

import ru.tyurin.filesync.server.connector.Connector;
import ru.tyurin.filesync.server.connector.ServerSocketConnector;

import java.io.IOException;

public class ConnectorFactory {

	public Connector createConnector(final int port) throws IOException {
		return new ServerSocketConnector(port);
	}
}
