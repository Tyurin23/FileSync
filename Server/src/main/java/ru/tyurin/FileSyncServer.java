package ru.tyurin;

import org.apache.log4j.Logger;
import ru.tyurin.connector.ServerConnector;


public class FileSyncServer {

	public static final Logger LOG = Logger.getLogger(FileSyncServer.class);

	public FileSyncServer() {
		LOG.info("Starting server...");
		ServerConnector connector = new ServerConnector();
		LOG.info("Stopping server...");
	}

	public static void main(String[] args) {
		FileSyncServer server = new FileSyncServer();
	}
}
