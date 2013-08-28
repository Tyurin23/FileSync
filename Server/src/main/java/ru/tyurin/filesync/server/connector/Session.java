package ru.tyurin.filesync.server.connector;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.server.db.UserProvider;
import ru.tyurin.filesync.server.db.tables.UserEntity;
import ru.tyurin.filesync.server.storage.BlockNode;
import ru.tyurin.filesync.shared.BlockTransferPart;
import ru.tyurin.filesync.shared.ConnectionStatus;
import ru.tyurin.filesync.shared.Request;
import ru.tyurin.filesync.shared.UserTransfer;

import java.io.IOException;
import java.util.Queue;

public class Session extends Thread {

	public static Logger LOG = Logger.getLogger(Session.class);

	private Connector connector;

	private Queue<BlockNode> pool;

	private UserEntity user;

	int number = -1;
	private boolean sleep = true;

	public Session(Queue<BlockNode> pool) {
		super("Session");
		this.pool = pool;
	}

	public Session(Queue<BlockNode> pool, int number) {
		this(pool);
		this.number = number;
		LOG.debug(String.format("Session %d created", number));
		this.setName("Session " + number);
	}

	public synchronized void setConnector(Connector connector) throws IOException {
		this.connector = connector;
		wakeup();
		LOG.debug(String.format("Session %d has new connection", number));
	}

	public boolean isClose() {
		if (connector == null) {
			return true;
		} else {
			return false;
		}
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
			} catch (IOException e) {
				e.printStackTrace();
				connector = null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void tick() throws IOException, ClassNotFoundException {
		if (connector != null) {
			processRequest((Request) connector.getObject());
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
			case AUTH:
				authorization();
				break;
			default:
				connector.sendObject(ConnectionStatus.ERROR);
				return;
		}
	}

	protected void getFileNodesRequest() {

	}

	protected void getBlockRequest() {

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
