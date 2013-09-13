package ru.tyurin.filesync.client.connector;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.client.fs.BlockNode;
import ru.tyurin.filesync.client.fs.FileNode;
import ru.tyurin.filesync.client.fs.VFS;
import ru.tyurin.filesync.shared.*;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.security.KeyStore;
import java.util.Collection;
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
		if(connector.sendRequest(Request.GET_FILE_NODES) == ConnectionStatus.OK){
			List<FileTransferPart> nodes = (List<FileTransferPart>) connector.receiveObject();
			return nodes;
		}
		return null;
	}

	public FileTransferPart getFileNode(String path) throws Exception {
		ClientSocketConnector connector = getAuthorizedConnector();
		ConnectionStatus status = connector.sendRequest(Request.GET_FILE);
		if(status == ConnectionStatus.OK){
			 status = connector.sendObject(path);
			if( status == ConnectionStatus.OK){
				FileTransferPart file = (FileTransferPart) connector.receiveObject();
				return file;
			}else if(status == ConnectionStatus.NO_SUCH_FILE_ERROR){
				return null;
			}
		}
		throw new Exception(status.toString());
	}

	public boolean sendBlock(FileNode node, BlockNode block, byte[] data) throws Exception {
		ClientSocketConnector connector = getAuthorizedConnector();
		BlockTransferPart part = new BlockTransferPart(node.getPath(), block.getIndex(), data);
		if (connector.sendRequest(Request.SEND_BLOCK_DATA) == ConnectionStatus.OK) {
			if (connector.sendObject(part) == ConnectionStatus.OK) {
				return true;
			}
		}
		return false;
	}

	public BlockTransferPart receiveBlock(String path, int index) throws Exception {
		ClientSocketConnector connector = getAuthorizedConnector();
		BlockTransferPart part = new BlockTransferPart(path, index, null);
		ConnectionStatus status = connector.sendRequest(Request.GET_BLOCK);
		if ( status == ConnectionStatus.OK) {
			status = connector.sendObject(part);
			if(status == ConnectionStatus.OK){
				return (BlockTransferPart) connector.receiveObject();
			}else if(status == ConnectionStatus.NO_SUCH_FILE_ERROR){
				return null;
			}
		}
		throw new Exception(status.toString());
	}

	public Collection<BlockTransferPart> getBlocks(String path) throws Exception {
		ClientSocketConnector connector = getAuthorizedConnector();
		if (connector.sendRequest(Request.GET_BLOCK_NODES) == ConnectionStatus.OK) {
			if (connector.sendObject(path) == ConnectionStatus.OK) {
				Collection<BlockTransferPart> blocks = (Collection<BlockTransferPart>) connector.receiveObject();
				return blocks;
			}
		}
		return null;
	}

	public boolean sendFileInfo(FileNode file) throws Exception {
		ClientSocketConnector connector = getAuthorizedConnector();
		FileTransferPart transfer = new FileTransferPart();
		transfer.setPath(file.getPath());
		transfer.setHash(file.getHash());
		transfer.setSize(file.getSpace());
		if (connector.sendRequest(Request.SEND_FILE) == ConnectionStatus.OK) {
			if (connector.sendObject(transfer) == ConnectionStatus.OK) {
				return true;
			}
		}
		return false;
	}

	public boolean sendBlockData(String path, int index, DataTransfer data) throws Exception {
		ClientSocketConnector connector = getAuthorizedConnector();
		BlockTransferPart blockTransferPart = new BlockTransferPart(path, index, data.getData());
		if (connector.sendRequest(Request.SEND_BLOCK_DATA) == ConnectionStatus.OK) {
			if(connector.sendObject(blockTransferPart) == ConnectionStatus.OK){
				return true;
			}
		}
		return false;
	}

	public DataTransfer getBlockData(String path, int index) throws Exception {
		ClientSocketConnector connector = getAuthorizedConnector();
		if (connector.sendRequest(Request.GET_BLOCK_DATA) == ConnectionStatus.OK) {
			if (connector.sendObject(path) == ConnectionStatus.OK && connector.sendObject(index) == ConnectionStatus.OK) {
				DataTransfer data = (DataTransfer) connector.receiveObject();
				if(data != null){
					return data;
				}
			}
		}
		return null;
	}

	public long getChecksum() throws Exception {
		ClientSocketConnector connector = getAuthorizedConnector();
		if (connector.sendRequest(Request.GET_FILESYSTEM_CHECKSUM) == ConnectionStatus.OK) {
			Long checksum = (Long) connector.receiveObject();
			if(checksum != null){
				return checksum;
			}
		}
		throw new IOException("Cannot receive checksum");
	}

	public boolean removeFile(FileNode file) throws Exception {
		ClientSocketConnector connector = getAuthorizedConnector();
		if (connector.sendRequest(Request.REMOVE_FILE) == ConnectionStatus.OK) {
			if (connector.sendObject(file.getPath()) == ConnectionStatus.OK){
				return true;
			}
		}
		return false;
	}

	public VFS getFileSystem(){
		return new RemoteFS(this);
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
