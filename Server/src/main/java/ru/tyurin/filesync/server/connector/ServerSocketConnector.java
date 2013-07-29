package ru.tyurin.filesync.server.connector;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerSocketConnector implements Connector {

	public static Logger LOG = Logger.getLogger(ServerSocketConnector.class);

	Socket socket;
	ObjectInputStream input;
	ObjectOutputStream output;

	public ServerSocketConnector(Socket socket) throws IOException {
		this.socket = socket;
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
	}

	@Override
	public void waitConnection() throws IOException {
	}

	@Override
	public Object getObject() throws IOException {
		Object obj = null;
		try {
			LOG.debug(String.format("Available: %d, Read: %d", input.available(), input.read()));
			obj = input.readObject();
			LOG.debug("Object readed");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public void sendObject(Object obj) {

	}

	@Override
	public void close() throws IOException {
		input.close();
		output.close();
		socket.close();
	}

	@Override
	public boolean isClose() {
		return socket.isClosed();
	}
}
