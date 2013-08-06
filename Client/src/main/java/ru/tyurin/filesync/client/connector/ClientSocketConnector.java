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


	public ClientSocketConnector(Socket socket) throws IOException {
		this.socket = socket;
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
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
			output.writeObject(obj);
			output.flush();
			LOG.debug(String.format("Object %s sent", obj));
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
		LOG.debug(String.format("Object %s read", obj));
		return obj;
	}

	@Override
	public boolean isClosed() {
		return socket.isClosed();
	}

}
