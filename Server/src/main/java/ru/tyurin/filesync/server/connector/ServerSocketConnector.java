package ru.tyurin.filesync.server.connector;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketConnector implements Connector {

	public static Logger LOG = Logger.getLogger(ServerSocketConnector.class);

	ServerSocket serverSocket;
	Socket socket;

	public ServerSocketConnector(int port) throws IOException {
		LOG.info("Create server connection...");
//		SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
//		serverSocket = factory.createServerSocket(4444);
		serverSocket = new ServerSocket(port);
		LOG.info("Connection created.");
	}

	public void read() throws IOException {
		LOG.info("Waiting connection...");
		Socket socket = serverSocket.accept();
		LOG.info("Connected!");
		InputStream input = socket.getInputStream();
		InputStreamReader reader = new InputStreamReader(input);
		BufferedReader bufferReader = new BufferedReader(reader);

		String message = null;
		while ((message = bufferReader.readLine()) != null) {
			LOG.info("Read line");
			System.out.println(message);
			System.out.flush();
		}
		socket.close();
		serverSocket.close();
		LOG.info("Connection close.");
	}

	@Override
	public void waitConnection() throws IOException {
		LOG.debug("Waiting connection...");
		socket = serverSocket.accept();
		LOG.debug("Connected!");
	}

	@Override
	public Object getObject() throws IOException {
		ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
		Object obj = null;
		try {
			obj = input.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return obj;
	}

	@Override
	public void sendObject(Object obj) {

	}

	@Override
	public void close() throws IOException {
		socket.close();
		serverSocket.close();
	}

}
