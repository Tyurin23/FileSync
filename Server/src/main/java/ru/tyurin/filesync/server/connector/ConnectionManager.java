package ru.tyurin.filesync.server.connector;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import ru.tyurin.filesync.shared.FileTransferPart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ConnectionManager implements Runnable {

	final int DATA_QUEUE_CAPACITY = 100;
	final int POOL_INIT_OBJECTS_COUNT = 10;
	final int CONNECTION_PORT = 4444;

	private ObjectPool<Session> connectionPool;
	private Queue<FileTransferPart> dataQueue;
	private List<Session> sessionList;

	public ConnectionManager() throws Exception {
		sessionList = new ArrayList<>();
		dataQueue = new ArrayBlockingQueue<FileTransferPart>(DATA_QUEUE_CAPACITY);
		connectionPool = new GenericObjectPool<>(new SessionFactory(dataQueue));
		initPool();
	}

	public synchronized Queue<FileTransferPart> getDataQueue() {
		return dataQueue;
	}

	protected void initPool() throws Exception {
		for (int i = 0; i < POOL_INIT_OBJECTS_COUNT; i++) {
			connectionPool.addObject();
		}
	}

	@Override
	public void run() {
		try {
			tick();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void tick() throws Exception {
		ServerSocketConnector connector = waitConnection();
		Session session = connectionPool.borrowObject();
		session.setConnector(connector);
		sessionList.add(session);
		returnClosedSession();
	}

	protected ServerSocketConnector waitConnection() throws IOException {
		ServerSocketConnector connector = new ServerSocketConnector(CONNECTION_PORT);
		connector.getConnection();
		return connector;
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
