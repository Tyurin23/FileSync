package ru.tyurin.filesync.client.connector;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSSLConnector implements Connector {

	protected Socket socket;
	protected ObjectOutputStream output;
	protected ObjectInputStream input;


	public ClientSSLConnector(Socket socket) throws IOException {
//		SSLContext sslContext = SSLContext.getInstance("TLS");
//
//		KeyStore ks = KeyStore.getInstance("JKS");
//		ks.load(new FileInputStream("/home/tyurin/code/FileSync/Server/src/main/resources/clienttrust.jks"), "password".toCharArray());
//		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
//		tmf.init(ks);
//
//		sslContext.init(null, tmf.getTrustManagers(), null);
//		SSLSocketFactory factory = sslContext.getSocketFactory();
//		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
//		socket = (SSLSocket) factory.createSocket(host, port);
//
////		session = socket.getSession();
////		socket.startHandshake();
		this.socket = socket;
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
	}

	@Override
	public void close() throws IOException {
		input.close();
		output.close();
		socket.close();
	}

	@Override
	public void sendObject(Object obj) throws IOException {
		if (obj != null) {
			output.writeObject(obj);
			output.flush();
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
		return obj;
	}

	@Override
	public boolean isClosed() {
		return socket.isClosed();
	}
}
