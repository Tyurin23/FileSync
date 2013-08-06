package ru.tyurin.filesync.server.connector;


import java.io.IOException;

public interface Connector {

	public void waitConnection() throws IOException;

	public Object getObject() throws IOException;

	public void sendObject(Object obj) throws IOException;

	public void close() throws IOException;

	public boolean isClose();
}
