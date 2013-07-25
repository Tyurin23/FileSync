package ru.tyurin.filesync.client.connector;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;


public class ClientSocketConnector {

	public static Logger LOG = Logger.getLogger(ClientSocketConnector.class);

//	SSLSocket socket;
	Socket socket;

	public ClientSocketConnector(String host, int port) throws IOException {
//		SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
//		socket = (SSLSocket) factory.createSocket(host, port);
		socket = new Socket(host, port);
	}

	public void read() throws IOException {
		InputStream inputstream = System.in;
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

		OutputStream outputstream = socket.getOutputStream();
		OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
		BufferedWriter bufferedwriter = new BufferedWriter(outputstreamwriter);
		bufferedwriter.write("Hello");
		bufferedwriter.flush();
//		String string = null;
//		while ((string = bufferedreader.readLine()) != null) {
//			bufferedwriter.write(string + '\n');
//			bufferedwriter.flush();
//		}

		socket.close();
	}

	public void sendObject(Object part) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(part);
	}

	public void close() throws IOException {
		socket.close();
	}

}
