package ru.tyurin.filesync.server;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.server.connector.ConnectionManager;
import ru.tyurin.filesync.server.connector.facory.Factory;
import ru.tyurin.filesync.server.db.EntityProvider;
import ru.tyurin.filesync.server.db.UserProvider;

import javax.net.ServerSocketFactory;
import java.io.IOException;


public class FileSyncServer {

	public static final Logger LOG = Logger.getLogger(FileSyncServer.class);

	private ConnectionManager connectionManager;

	public FileSyncServer(Config cfg) throws Exception {

		EntityProvider.createInstance(
				cfg.getDbType(),
				cfg.getDbHost(),
				cfg.getDbPort(),
				cfg.getDbName(),
				cfg.getDbUser(),
				cfg.getDbPassword()
		);

		Factory factory = new Factory(cfg.getStorageDirectory(), createServerSocketFactory(cfg));
		connectionManager = new ConnectionManager(factory);
		connectionManager.start();
	}

	public void stop() throws IOException {
		connectionManager.close();
	}

	private ServerSocketFactory createServerSocketFactory(Config cfg) throws Exception {
		if (cfg.isEnableSSL()) {
			LOG.info("SSL enabled");
			System.setProperty("javax.net.ssl.keyStore", cfg.getKeyStore());
			System.setProperty("javax.net.ssl.keyStorePassword", cfg.getKeyStorePassword());
			return ConnectionManager.getSSLServerSocketFactory();
		} else {
			return ConnectionManager.getDefaultServerSocketFactory();
		}
	}



	public static void main(String[] args) {
		LOG.info("Starting server...");
		try {
			FileSyncServer server = new FileSyncServer(new Config());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error! Message: " + e.getMessage());

		}
		LOG.info("Server stopped");
	}
}
