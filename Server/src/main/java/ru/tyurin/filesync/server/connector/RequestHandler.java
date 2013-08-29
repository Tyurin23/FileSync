package ru.tyurin.filesync.server.connector;

import ru.tyurin.filesync.shared.ConnectionStatus;

public interface RequestHandler {

	public boolean isAuthRequired();

	public ConnectionStatus processRequest(ServerSocketConnector connector) throws Exception;


}
