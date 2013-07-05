package ru.tyurin;

import org.apache.log4j.Logger;
import ru.tyurin.connector.AbstractConnector;
import ru.tyurin.connector.ServerConnector;
import ru.tyurin.connector.SocketConnector;

import java.io.IOException;


public class FileSyncServer {

	public static final Logger LOG = Logger.getLogger(FileSyncServer.class);

	public FileSyncServer() {

//		ServerConnector connector = new ServerConnector();
		try {
			AbstractConnector connector = new SocketConnector();
			Thread conn = new Thread(connector);
			conn.start();
			while(conn.getState() != Thread.State.TERMINATED){

			}
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}

	}

	public static void main(String[] args) {
		LOG.info("Starting server...");
		FileSyncServer server = new FileSyncServer();
		LOG.info("Stopping server...");
	}
}
