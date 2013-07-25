package ru.tyurin.filesync.server.connector;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketConnector {

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
		while((message = bufferReader.readLine()) != null){
			LOG.info("Read line");
			System.out.println(message);
			System.out.flush();
		}
		socket.close();
		serverSocket.close();
		LOG.info("Connection close.");
	}

	public Object getObject() throws IOException, ClassNotFoundException {
		ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
		Object obj = input.readObject();
		return obj;
	}

	public void close() throws IOException {
		socket.close();
		serverSocket.close();
	}

	public void getConnection() throws IOException {
		LOG.info("Waiting connection...");
		socket = serverSocket.accept();
		LOG.info("Connected!");
	}
}
