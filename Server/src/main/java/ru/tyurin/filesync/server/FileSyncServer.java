package ru.tyurin.filesync.server;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.server.connector.ConnectionManager;
import ru.tyurin.filesync.server.connector.facory.Factory;
import ru.tyurin.filesync.server.db.EntityProvider;
import ru.tyurin.filesync.server.db.UserProvider;

import javax.net.ServerSocketFactory;


public class FileSyncServer extends Thread {

	public static final Logger LOG = Logger.getLogger(FileSyncServer.class);

	private ConnectionManager connectionManager;
	UserProvider userProvider;

	public FileSyncServer(Config cfg) throws Exception {
		super("FileSyncServer");

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
//		StorageManagerFactory storageManagerFactory = new StorageManagerFactory(cfg.getStorageDirectory());
//		ControllerFactory controllerFactory = new ControllerFactory(storageManagerFactory);
//		SessionFactory sessionFactory = new SessionFactory(controllerFactory);
//		ConnectorFactory connectorFactory = new ConnectorFactory();
//		ServerSocketFactory socketFactory;
//		if (cfg.isEnableSSL()) {
//			LOG.info("SSL enabled");
//			System.setProperty("javax.net.ssl.keyStore", cfg.getKeyStore());
//			System.setProperty("javax.net.ssl.keyStorePassword", cfg.getKeyStorePassword());
//			socketFactory = ConnectionManager.getSSLServerSocketFactory();
//		} else {
//			socketFactory = ConnectionManager.getDefaultServerSocketFactory();
//		}
//		connectionManager = new ConnectionManager(connectorFactory, sessionFactory, socketFactory);


//		userProvider = new UserProvider();

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

	@Override
	public void run() {
		connectionManager.start();
		while (!interrupted()) {
			if (connectionManager.isInterrupted()) {
				interrupt();
				continue;
			}
//			while (connectionManager.getDataQueue().size() > 0) {
//				BlockNode node = connectionManager.getDataQueue().poll();
//				LOG.debug("Node received: " + node.getPath());
//				UserEntity user = userProvider.findById(Integer.valueOf(node.getUserId()));
//				if(user != null){
////					try {
//////						storageManager.saveBlock(node);
////					} catch (IOException e) {
////						e.printStackTrace();
////						interrupt();
////					}
//					FileEntity file = user.getFile(node.getPath());
//					if(file == null){
//						file = new FileEntity();
//						file.setPath(node.getPath());
//						user.addFile(file);
//					}
//					BlockEntity block = file.getBlock(node.getIndex());
//					if(block == null){
//						block = new BlockEntity();
//						block.setIndex(node.getIndex());
//						file.addBlock(block);
//					}
//					block.setHash(node.getHash());
//					block.setSize(node.getSize());
//					block.setDateModified(node.getDateModified());
//					userProvider.updateUser(user);
//				}
//			}
		}
		connectionManager.interrupt();
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
