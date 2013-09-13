package ru.tyurin.filesync.server.connector;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import ru.tyurin.filesync.server.connector.facory.Factory;
import ru.tyurin.filesync.server.connector.facory.SessionPoolFactory;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

public class ConnectionManager extends Thread {

	public static Logger LOG = Logger.getLogger(ConnectionManager.class);

	final int DATA_QUEUE_CAPACITY = 100;
	final int POOL_INIT_OBJECTS_COUNT = 10;
	final int CONNECTION_PORT = 4444;

	protected ObjectPool<Session> connectionPool;
	protected List<Session> sessionList;

	protected ServerSocket serverSocket;

	protected Factory factory;

	public static ServerSocketFactory getSSLServerSocketFactory() throws Exception {
		KeyStore keyStore = KeyStore.getInstance("JKS");
		keyStore.load(new FileInputStream(System.getProperty("javax.net.ssl.keyStore")), System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(keyStore, System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmf.getKeyManagers(), new TrustManager[]{}, null);
		SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
		return factory;
	}

	public static ServerSocketFactory getDefaultServerSocketFactory() throws Exception {
		return ServerSocketFactory.getDefault();
	}

	public ConnectionManager(Factory factory) throws Exception {
		super("ServerConnectionManager");
		LOG.debug("Creating connection manager...");
		sessionList = new ArrayList<>(POOL_INIT_OBJECTS_COUNT);
		connectionPool = new GenericObjectPool<>(new SessionPoolFactory(factory));
		serverSocket = factory.createServerSocket(CONNECTION_PORT);
		this.factory = factory;
		initPool();
	}

	protected void initPool() throws Exception {
		for (int i = 0; i < POOL_INIT_OBJECTS_COUNT; i++) {
			connectionPool.addObject();
		}
	}

	@Override
	public void run() {
		LOG.debug("Connection Manager started");
		while (!interrupted()) {
			try {
				tick();
			} catch (InterruptedException e) {
				this.interrupt();
			} catch (Exception e) {
				this.interrupt();
			}
		}
		LOG.debug("Connection Manager stopped");
	}

	public void close() throws IOException {
		serverSocket.close();
	}

	protected void tick() throws Exception {
		ServerSocketConnector connector = waitConnection();
		Session session = connectionPool.borrowObject();
		session.setConnector(connector);
		sessionList.add(session);
		returnClosedSession();
	}

	protected ServerSocketConnector waitConnection() throws IOException {
		return factory.createConnector(serverSocket.accept());
	}

	protected void returnClosedSession() throws Exception {
		for (Session session : sessionList) {
			if (session.isClose()) {
				connectionPool.returnObject(session);
				sessionList.remove(session);
			}
		}
	}
}
