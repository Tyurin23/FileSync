package ru.tyurin.filesync.server.connector;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.shared.FileTransferPart;

import java.io.IOException;
import java.util.Queue;

public class Session extends Thread {

	public static Logger LOG = Logger.getLogger(Session.class);

	private Connector connector;
	private Queue pool;

	int number = -1;
	private boolean sleep = false;

	public Session(Queue pool) {
		this.pool = pool;
	}

	public Session(Queue pool, int number) {
		this(pool);
		this.number = number;
		LOG.debug(String.format("Session %d created", number));
	}

	public synchronized void setConnector(Connector connector) {
		this.connector = connector;
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
		synchronized (this) {
			this.notify();
		}
		LOG.debug(String.format("Session %d wakeup", number));
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				LOG.debug("session run " + number);
				if (sleep) {
					synchronized (this) {
						this.wait();
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
			FileTransferPart part = (FileTransferPart) connector.getObject();
			pool.add(part);
		}
	}
}
