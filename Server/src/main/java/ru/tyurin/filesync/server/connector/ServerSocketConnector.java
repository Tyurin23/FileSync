package ru.tyurin.filesync.server.connector;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.shared.ConnectionStatus;
import ru.tyurin.filesync.shared.Request;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerSocketConnector {

	public static Logger LOG = Logger.getLogger(ServerSocketConnector.class);

	Socket socket;
	ObjectInputStream input;
	ObjectOutputStream output;
	private Integer userID;

	public ServerSocketConnector(Socket socket) throws IOException {
		this.socket = socket;
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
		LOG.debug("Connection created");
	}

	public Request getRequest() throws IOException {
		Request req = (Request) getObject();
		LOG.debug("Request " + req);
		return req;
	}

	public void sendStatus(ConnectionStatus status) throws IOException {
		sendObject(status);
	}


	public Object getObject() throws IOException {
		Object obj = null;
		try {
			obj = input.readObject();
			LOG.debug("Object receaved " + obj);
		} catch (ClassNotFoundException | EOFException e) {
//			LOG.debug("ERROR " + e.getMessage());
		}
		return obj;
	}

	public void sendObject(Object obj) throws IOException {
		if (obj != null) {
			output.writeObject(obj);
			output.flush();
			LOG.debug("Send object " + obj);
		}
	}

	public void close() throws IOException {
		input.close();
		output.close();
		socket.close();
		LOG.debug("Connection close");
	}

	public boolean isClose() {
		return socket.isClosed();
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}
}
