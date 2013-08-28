package ru.tyurin.filesync.client.connector;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.client.fs.FileBlock;
import ru.tyurin.filesync.client.fs.FileNode;
import ru.tyurin.filesync.shared.*;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.security.KeyStore;
import java.util.List;

/**
 * User: tyurin
 * Date: 8/5/13
 * Time: 1:47 PM
 */
public class ConnectionManager {

	public static Logger LOG = Logger.getLogger(ConnectionManager.class);

	private static ConnectionManager instance;


	private String host = "localhost";
	private int port = 4444;

	private String login = "";
	private String pass = "";

	protected SocketFactory factory;

	protected Connector connector;

	public static ConnectionManager createSSLInstance() throws Exception {
		if (instance == null) {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream("/home/tyurin/code/FileSync/Server/src/main/resources/clienttrust.jks"), "password".toCharArray());
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ks);
			sslContext.init(null, tmf.getTrustManagers(), null);
			SSLSocketFactory factory = sslContext.getSocketFactory();
			instance = new ConnectionManager(factory);
		}
		return instance;
	}


	public ConnectionManager() {
		this(SocketFactory.getDefault());
		LOG.debug("Connection manager created");
	}

	public ConnectionManager(SocketFactory factory) {
		this.factory = factory;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public boolean testConnection() throws Exception {
		Connector connector = getAuthorizedConnector();
		if (connector == null) {
			return false;
		} else {
			return true;
		}
	}

	public boolean registration(String login, String password) throws IOException {
		Connector connector = getConnector();
		connector.sendObject(Request.REGISTRATION);
		ConnectionStatus status = (ConnectionStatus) connector.getObject();
		if (status == ConnectionStatus.OK) {
			UserTransfer userTransfer = new UserTransfer(login, password);
			connector.sendObject(userTransfer);
			status = (ConnectionStatus) connector.getObject();
			if (status == ConnectionStatus.OK) {
				return true;
			}
		}
		return false;
	}

	public List<FileTransferPart> getFileNodes() throws Exception {
		Connector connector = getAuthorizedConnector();
		connector.sendObject(Request.GET_FILE_NODES);
		List<FileTransferPart> nodes = (List<FileTransferPart>) connector.getObject();
		return nodes;
	}

	public boolean sendBlock(FileNode node, FileBlock block, byte[] data) throws Exception {
		BlockTransferPart part = new BlockTransferPart(node.getPath(), block.getIndex(), data);
		Connector connector = getAuthorizedConnector();
		connector.sendObject(Request.SAVE_BLOCK);
		ConnectionStatus status = (ConnectionStatus) connector.getObject();
		if (status == ConnectionStatus.OK) {
			connector.sendObject(part);
			status = (ConnectionStatus) connector.getObject();
			if (status == ConnectionStatus.OK) {
				return true;
			}
		}
		return false;
	}

	protected Connector getAuthorizedConnector() throws Exception {
		if (connector == null) {
			connector = getConnector();
			if (connector == null) {
				return null;
			}
			LOG.debug("Client Connection created");
			if (!authorization(connector)) {
				connector.close();
				connector = null;
			}
		}
		return connector;
	}

	private Connector getConnector() throws IOException {
		Connector connector = null;
		try {
			connector = new ClientSocketConnector(factory.createSocket(host, port));
		} catch (ConnectException e) {
			LOG.debug(e.getMessage());
		}
		return connector;
	}

	private boolean authorization(Connector connector) throws IOException {
		connector.sendObject(Request.AUTH);
		ConnectionStatus status = (ConnectionStatus) connector.getObject();
		if (status == ConnectionStatus.OK) {
			UserTransfer userTransfer = new UserTransfer(login, pass);
			connector.sendObject(userTransfer);
			status = (ConnectionStatus) connector.getObject();
			if (status == ConnectionStatus.OK) {
				return true;
			}
		}
		return false;
	}
}
