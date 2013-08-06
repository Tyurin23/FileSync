package ru.tyurin.filesync.server.connector;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.server.db.UserProvider;
import ru.tyurin.filesync.server.storage.BlockNode;
import ru.tyurin.filesync.shared.ConnectionStatus;
import ru.tyurin.filesync.shared.FileTransferPart;
import ru.tyurin.filesync.shared.Request;
import ru.tyurin.filesync.shared.UserTransfer;

import java.io.IOException;
import java.util.Queue;

public class Session extends Thread {

	public static Logger LOG = Logger.getLogger(Session.class);

	private static final Object monitor = new Object();

	private Connector connector;
	private Queue<BlockNode> pool;

	private int userId;

	int number = -1;
	private boolean sleep = true;

	public Session(Queue<BlockNode> pool) {
		this.pool = pool;
	}

	public Session(Queue<BlockNode> pool, int number) {
		this(pool);
		this.number = number;
		LOG.debug(String.format("Session %d created", number));
	}

	public synchronized void setConnector(Connector connector) throws IOException {
		if (authorization(connector)) {
			connector.sendObject(ConnectionStatus.OK);
			this.connector = connector;
			LOG.debug(String.format("Session %d has new connection", number));
		} else {
			connector.sendObject(ConnectionStatus.ERROR);
			connector.close();
		}
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
		synchronized (monitor) {
			monitor.notifyAll();
		}
		LOG.debug(String.format("Session %d wakeup", number));
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				LOG.debug("session run " + number);
				if (sleep) {
					synchronized (monitor) {
						LOG.debug("WAIT " + number);
						monitor.wait();
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
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}
	}

	protected void tick() throws IOException, ClassNotFoundException {
		if (connector != null) {
			processRequest((Request) connector.getObject());
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
			default:
				break;

		}
	}

	protected void getFileNodesRequest() {

	}

	protected void getBlockRequest() {

	}

	protected void saveBlockRequest() throws IOException {
		FileTransferPart part = (FileTransferPart) connector.getObject();
		if (part != null) {
			BlockNode node = new BlockNode(part.getPath(), userId);
			node.setData(part.getData());
			pool.add(node);
			connector.sendObject(ConnectionStatus.OK);
		}
		connector.sendObject(ConnectionStatus.ERROR);
	}


	protected boolean authorization(Connector connector) throws IOException {
		Request req = (Request) connector.getObject();
		if (req == Request.AUTH) {
			UserTransfer userTransfer = (UserTransfer) connector.getObject();
			if (userTransfer == null) {
				return false;
			}
			int id = UserProvider.authentication(userTransfer.getLogin(), userTransfer.getPassword());
			if (id == UserProvider.AUTH_FAIL) {
				return false;
			} else {
				userId = id;
				return true;
			}
		}
		return false;
	}
}
