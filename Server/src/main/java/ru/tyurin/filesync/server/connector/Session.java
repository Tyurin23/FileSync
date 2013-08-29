package ru.tyurin.filesync.server.connector;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.server.db.UserProvider;
import ru.tyurin.filesync.server.db.tables.BlockEntity;
import ru.tyurin.filesync.server.db.tables.FileEntity;
import ru.tyurin.filesync.server.db.tables.UserEntity;
import ru.tyurin.filesync.server.storage.BlockNode;
import ru.tyurin.filesync.shared.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Session extends Thread {

	public static Logger LOG = Logger.getLogger(Session.class);

	private ServerSocketConnector connector;

	private Queue<BlockNode> pool;

	private UserEntity user;
	private Controller controller;
	public String identifier = "";

	int number = -1;//Deprecated

	private boolean sleep = true;

	public Session(Queue<BlockNode> pool, Controller controller) {
		super("Session");
		this.pool = pool;
	}

	public Session(Controller controller) {
		this.controller = controller;
		this.setName("Session " + identifier);
	}

	public synchronized void setConnector(ServerSocketConnector connector) throws IOException {
		this.connector = connector;
		wakeup();
		LOG.debug(String.format("Session %d has new connection", number));
	}

	public boolean isClose() {
		return (connector == null);
	}

	public synchronized void waiting() {
		LOG.debug(String.format("Session %d waiting", number));
		this.sleep = true;
	}


	public synchronized void wakeup() {
		this.sleep = false;
		notifyAll();
		LOG.debug(String.format("Session %d wakeup", number));
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				if (sleep) {
					synchronized (this) {
						LOG.debug("WAIT " + number);
						wait();
						LOG.debug("NOTIFY " + number);
					}
				}
				tick();
			} catch (Exception e) {
				interrupt();
			}
		}
	}

	protected void tick() throws Exception {
		if (connector != null) {
//			processRequest((Request) connector.getObject());
			Request req = connector.getRequest();
			if (req != null) {
				if (controller.isAuthRequired(req) && connector.getUserID() == null) {
					connector.sendStatus(ConnectionStatus.AUTHENTICATION_ERROR);
					return;
				} else {
					connector.sendStatus(ConnectionStatus.OK);
				}
				ConnectionStatus status = controller.processRequest(req, connector);
				if (status != null) {
					connector.sendStatus(status);
				}
			}
		} else {
			sleep = true;
		}
	}

	protected void processRequest(Request req) throws IOException {
		if (req == null) {
			return;
		}
		switch (req) {
			case GET_FILE_NODES:
				getFileNodesRequest();
				break;
			case GET_BLOCK:
				getBlockRequest();
				break;
			case SAVE_BLOCK:
				saveBlockRequest();
				break;
			case UPDATE_FILE_INFO:
				updateFileInfo();
				break;
			case AUTH:
				authorization();
				break;
			default:
				connector.sendObject(ConnectionStatus.ERROR);
				return;
		}
	}

	protected void updateFileInfo() throws IOException {
		if (user == null) {
			connector.sendObject(ConnectionStatus.AUTHENTICATION_ERROR);
			return;
		}
		connector.sendObject(ConnectionStatus.OK);
		FileTransferPart transferPart = (FileTransferPart) connector.getObject();
		if (transferPart != null) {
			FileEntity file = user.getFile(transferPart.getPath());
			if (file == null) {
				file = new FileEntity();
				file.setPath(transferPart.getPath());
				user.addFile(file);
			}
			file.setHash(transferPart.getHash());
			file.setSize(transferPart.getSize());
			new UserProvider().updateUser(user);
			connector.sendObject(ConnectionStatus.OK);
		} else {
			connector.sendObject(ConnectionStatus.ERROR);
		}
	}

	protected void getFileNodesRequest() throws IOException {
		if (user == null) {
			connector.sendObject(ConnectionStatus.AUTHENTICATION_ERROR);
			return;
		}
		connector.sendObject(ConnectionStatus.OK);
		List<FileTransferPart> files = new ArrayList<>(user.getFiles().size());
		for (FileEntity file : user.getFiles()) {
			FileTransferPart fileTransfer = new FileTransferPart();
			fileTransfer.setHash(file.getHash());
			fileTransfer.setSize(file.getSize());
			fileTransfer.setPath(file.getPath());
			files.add(fileTransfer);
		}
		connector.sendObject(files);
	}

	protected void getBlockRequest() throws IOException {
		if (user == null) {
			connector.sendObject(ConnectionStatus.AUTHENTICATION_ERROR);
			return;
		}
		connector.sendObject(ConnectionStatus.OK);
		BlockTransferPart blockRequest = (BlockTransferPart) connector.getObject();
		if (blockRequest != null) {
			FileEntity file = user.getFile(blockRequest.getPath());
			if (file != null) {
				BlockEntity block = file.getBlock(blockRequest.getBlockIndex());
				if (block != null) {

				}
			}
		}
	}

	protected void saveBlockRequest() throws IOException {
		if (user == null) {
			connector.sendObject(ConnectionStatus.AUTHENTICATION_ERROR);
			return;
		} else {
			connector.sendObject(ConnectionStatus.OK);
		}
		BlockTransferPart part = (BlockTransferPart) connector.getObject();
		if (part != null) {
			BlockNode node = new BlockNode(part.getPath(), part.getBlockIndex(), user.getId());
			node.setData(part.getData());
			pool.add(node);
			connector.sendObject(ConnectionStatus.OK);
		} else {
			connector.sendObject(ConnectionStatus.ERROR);
		}
	}

	protected void registration() throws IOException {
		connector.sendObject(ConnectionStatus.OK);
		UserTransfer userTransfer = (UserTransfer) connector.getObject();
		UserEntity user = new UserProvider().createUser(userTransfer.getLogin(), userTransfer.getPassword());
		if (user != null) {
			connector.sendObject(ConnectionStatus.OK);
		} else {
			connector.sendObject(ConnectionStatus.ERROR);
		}
	}


	protected void authorization() throws IOException {
		connector.sendObject(ConnectionStatus.OK);
		UserTransfer userTransfer = (UserTransfer) connector.getObject();
		UserEntity user = new UserProvider().findByLoginAndPassword(userTransfer.getLogin(), userTransfer.getPassword());
		if (user != null) {
			this.user = user;
			connector.sendObject(ConnectionStatus.OK);
		} else {
			connector.sendObject(ConnectionStatus.AUTHENTICATION_ERROR);
		}
	}
}
