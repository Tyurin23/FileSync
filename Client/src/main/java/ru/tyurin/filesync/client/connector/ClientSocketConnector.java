package ru.tyurin.filesync.client.connector;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.shared.ConnectionStatus;
import ru.tyurin.filesync.shared.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ClientSocketConnector {

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


	public void close() throws IOException {
		input.close();
		output.close();
		socket.close();
		LOG.debug("Socket closed");
	}


	public ConnectionStatus sendObject(Object obj) throws IOException {
		sendObj(obj);
		return (ConnectionStatus) receiveObj();
	}


	public Object receiveObject() throws IOException {
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

	public ConnectionStatus sendRequest(Request request) throws IOException {
		if (request == null) {
			throw new NullPointerException("Request is null");
		}
		return sendObject(request);
	}

	public boolean isClosed() {
		return socket.isClosed();
	}

	private void sendObj(Object obj) throws IOException {
		if (obj != null) {
			output.writeObject(obj);
			output.flush();
			LOG.debug(String.format("Object %s sent", obj));
		}
	}

	private Object receiveObj() throws IOException {
		Object obj = null;
		try {
			obj = input.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		LOG.debug(String.format("Object %s read", obj));
		return obj;
	}


}
