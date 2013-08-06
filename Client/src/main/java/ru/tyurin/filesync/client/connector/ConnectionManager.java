package ru.tyurin.filesync.client.connector;

import ru.tyurin.filesync.shared.ConnectionStatus;
import ru.tyurin.filesync.shared.FileNode;
import ru.tyurin.filesync.shared.Request;
import ru.tyurin.filesync.shared.UserTransfer;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

/**
 * User: tyurin
 * Date: 8/5/13
 * Time: 1:47 PM
 */
public class ConnectionManager extends Thread {

	protected String host = "localhost";
	protected int port = 4444;

	protected String login = "test"; // todo
	protected String pass = "test";

	protected SocketFactory factory;

	protected Connector connector;

	public static ConnectionManager createSSLInstance() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, KeyManagementException {
		SSLContext sslContext = SSLContext.getInstance("TLS");
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new FileInputStream("/home/tyurin/code/FileSync/Server/src/main/resources/clienttrust.jks"), "password".toCharArray());
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ks);
		sslContext.init(null, tmf.getTrustManagers(), null);
		SSLSocketFactory factory = sslContext.getSocketFactory();
		return new ConnectionManager(factory);
	}

	public ConnectionManager() {
		this(SocketFactory.getDefault());
	}

	public ConnectionManager(SocketFactory factory) {
		this.factory = factory;
	}

	public synchronized List<FileNode> getFileNodes() throws Exception {
		Connector connector = getConnector();
		connector.sendObject(Request.GET_FILE_NODES);
		List<FileNode> nodes = (List<FileNode>) connector.getObject();
		return nodes;
	}

	protected Connector getConnector() throws Exception {
		if (connector == null) {
			connector = new ClientSocketConnector(factory.createSocket(host, port));
			UserTransfer userTransfer = new UserTransfer(login, pass);
			connector.sendObject(userTransfer);
			ConnectionStatus status = (ConnectionStatus) connector.getObject();
			if (status == ConnectionStatus.OK) {
				return connector;
			} else {
				throw new Exception("Authorization failed");
			}
		}
		return connector;
	}

	@Override
	public void run() {
		while (!interrupted()) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
	}
}
