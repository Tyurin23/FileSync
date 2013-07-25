package ru.tyurin.filesync.server.connector;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.shared.FileTransferPart;

import java.io.IOException;
import java.util.Queue;

public class Session extends Thread {

	public static Logger LOG = Logger.getLogger(Session.class);

	private ServerSocketConnector connector;
	private Queue pool;

	int number = -1;

	public Session(Queue pool) {
		this.pool = pool;
	}

	public Session(Queue pool, int number) {
		this(pool);
		this.number = number;
		LOG.debug(String.format("Session %d created", number));
	}

	public synchronized void setConnector(ServerSocketConnector connector) {
		this.connector = connector;
	}

	public boolean isClose() {
		if (connector == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				tick();
			} catch (IOException e) {
				e.printStackTrace();
				connector = null;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

	}

	protected void tick() throws IOException, ClassNotFoundException {
		if (connector != null) {
			connector.getConnection();
			FileTransferPart part = (FileTransferPart) connector.getObject();
			pool.add(part);
		}
	}
}
