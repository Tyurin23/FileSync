package ru.tyurin.filesync.server;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.server.connector.ConnectionManager;
import ru.tyurin.filesync.server.storage.BlockNode;
import ru.tyurin.filesync.server.storage.StorageManager;


public class FileSyncServer extends Thread {

	public static final Logger LOG = Logger.getLogger(FileSyncServer.class);

	private ConnectionManager connectionManager;
	private StorageManager storageManager;

	public FileSyncServer(Config cfg) throws Exception {
		if (cfg.isEnableSSL()) {
			LOG.info("SSL enabled");
			System.setProperty("javax.net.ssl.keyStore", cfg.getKeyStore());
			System.setProperty("javax.net.ssl.keyStorePassword", cfg.getKeyStorePassword());
			connectionManager = ConnectionManager.getSSLInstance();
		} else {
			connectionManager = ConnectionManager.getDefaultInstance();
		}
		storageManager = new StorageManager(cfg.rootDirectory);

	}

	@Override
	public void run() {
		storageManager.start();
		connectionManager.start();
		while (!interrupted()) {
			while (connectionManager.getDataQueue().size() > 0) {
				BlockNode node = connectionManager.getDataQueue().poll();
				LOG.debug("Node received: " + node.getPath());
			}
		}
		connectionManager.interrupt();
		storageManager.interrupt();
	}

	public static void main(String[] args) {
		LOG.info("Starting server...");
		try {
			FileSyncServer server = new FileSyncServer(new Config());
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error! Message: " + e.getMessage());
		}
		LOG.info("Server stopped");
	}
}
