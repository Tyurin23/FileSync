package ru.tyurin.filesync.client.connector;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ClientSocketConnector implements Connector {

	public static Logger LOG = Logger.getLogger(ClientSocketConnector.class);

	protected Socket socket;
	ObjectInputStream input;
	ObjectOutputStream output;


	public ClientSocketConnector(String host, int port) throws IOException {
		socket = new Socket(host, port);
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
		LOG.debug(String.format("Socket created with host: %s:%d", host, port));
	}

	protected ClientSocketConnector() {
	}


	@Override
	public void close() throws IOException {
		input.close();
		output.close();
		socket.close();
		LOG.debug("Socket closed");
	}


	@Override
	public void sendObject(Object obj) throws IOException {
		if (obj != null) {
			ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
			output.writeObject(obj);
			output.flush();
			LOG.debug(String.format("Object %s sent", obj));
			output.close();
		}

	}

	@Override
	public Object getObject() throws IOException {
		Object obj = null;
		try {
			obj = input.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		LOG.debug(String.format("Object %s read"));
		return obj;
	}

	@Override
	public boolean isClosed() {
		return socket.isClosed();
	}

}
