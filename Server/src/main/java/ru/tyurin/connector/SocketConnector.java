package ru.tyurin.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/4/13
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SocketConnector extends AbstractConnector {

	ServerSocket socket;

	public SocketConnector() throws IOException {
		socket  = new ServerSocket(4444);
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()){
			try {

				Socket client = socket.accept();
				PrintWriter out = new PrintWriter(client.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				String input, output;
				while((input = in.readLine()) != null){
					System.out.println(input);
				}
			} catch (IOException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				Thread.currentThread().interrupt();
			}
		}
	}
}
