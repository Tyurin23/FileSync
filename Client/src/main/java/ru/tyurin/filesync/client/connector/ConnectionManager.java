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

	protected ClientSocketConnector connector;

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
		return getInstance();
	}

	public static ConnectionManager createInstance() {
		if (instance == null) {
			instance = new ConnectionManager(SocketFactory.getDefault());
		}
		return getInstance();
	}

	public static ConnectionManager getInstance() {
		return instance;
	}

	public ConnectionManager(SocketFactory factory) {
		this.factory = factory;
		LOG.debug("Connection manager created");
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public boolean testConnection() throws Exception {
		if (getConnector() != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean testAuthorization() throws Exception {
		if (getAuthorizedConnector() != null) {
			return true;
		} else {
			return false;
		}
	}

	public boolean registration(String login, String password) throws IOException {
		ClientSocketConnector connector = getConnector();
		if (connector.sendRequest(Request.REGISTRATION) == ConnectionStatus.OK) {
			UserTransfer userTransfer = new UserTransfer(login, password);
			if (connector.sendObject(userTransfer) == ConnectionStatus.OK) {
				return true;
			}
		}
		return false;
	}

	public List<FileTransferPart> getFileNodes() throws Exception {
		ClientSocketConnector connector = getAuthorizedConnector();
		connector.sendObject(Request.GET_FILE_NODES);
		List<FileTransferPart> nodes = (List<FileTransferPart>) connector.receiveObject();
		return nodes;
	}

	public boolean sendBlock(FileNode node, FileBlock block, byte[] data) throws Exception {
		ClientSocketConnector connector = getAuthorizedConnector();
		BlockTransferPart part = new BlockTransferPart(node.getPath(), block.getIndex(), data);
		if (connector.sendRequest(Request.SAVE_BLOCK) == ConnectionStatus.OK) {
			if (connector.sendObject(part) == ConnectionStatus.OK) {
				return true;
			}
		}
		return false;
	}

	public boolean sendFileInfo(FileNode file) throws Exception {
		ClientSocketConnector connector = getAuthorizedConnector();
		FileTransferPart transfer = new FileTransferPart();
		transfer.setPath(file.getPath());
		transfer.setHash(file.getHash());
		transfer.setSize(file.getSpace());
		if (connector.sendRequest(Request.UPDATE_FILE_INFO) == ConnectionStatus.OK) {
			if (connector.sendObject(transfer) == ConnectionStatus.OK) {
				return true;
			}
		}
		return false;
	}

	protected ClientSocketConnector getAuthorizedConnector() throws Exception {
		if (connector == null) {
			connector = getConnector();
			if (connector == null) {
				return null;
			}
			LOG.debug("Client Connection created");
			if (!authorization(connector)) {
				connector.close();
				throw new Exception("Authorization fail");
			}
		}
		return connector;
	}

	private ClientSocketConnector getConnector() throws IOException {
		ClientSocketConnector connector = null;
		try {
			connector = new ClientSocketConnector(factory.createSocket(host, port));
		} catch (ConnectException e) {
			LOG.debug(e.getMessage());
		}
		return connector;
	}

	private boolean authorization(ClientSocketConnector connector) throws IOException {
		LOG.debug("Trying auth: " + login + " " + pass);
		if (connector.sendRequest(Request.AUTH) == ConnectionStatus.OK) {
			UserTransfer userTransfer = new UserTransfer(login, pass);
			if (connector.sendObject(userTransfer) == ConnectionStatus.OK) {
				return true;
			}
		}
		return false;
	}
}
