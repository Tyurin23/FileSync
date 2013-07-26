package ru.tyurin.filesync.server.connector;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import ru.tyurin.filesync.server.connector.facory.ConnectorFactory;
import ru.tyurin.filesync.server.connector.facory.SessionFactory;
import ru.tyurin.filesync.server.connector.facory.SessionPoolFactory;
import ru.tyurin.filesync.shared.FileTransferPart;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ConnectionManager extends Thread implements ActionListener {

	public static Logger LOG = Logger.getLogger(ConnectionManager.class);

	final int DATA_QUEUE_CAPACITY = 100;
	final int POOL_INIT_OBJECTS_COUNT = 10;
	final int CONNECTION_PORT = 4444;

	protected ObjectPool<Session> connectionPool;
	protected Queue<FileTransferPart> dataQueue;
	protected List<Session> sessionList;

	private Timer timer = new Timer(1000, this);
	protected ConnectorFactory connectorFactory;
	protected SessionFactory sessionFactory;

	public ConnectionManager() throws Exception {
		connectorFactory = new ConnectorFactory();
		sessionFactory = new SessionFactory();
		init();
	}

	public ConnectionManager(ConnectorFactory connectorFactory, SessionFactory sessionFactory) throws Exception {
		this.connectorFactory = connectorFactory;
		this.sessionFactory = sessionFactory;
		init();
	}


	public synchronized Queue<FileTransferPart> getDataQueue() {
		return dataQueue;
	}

	protected void init() throws Exception {
		sessionList = new ArrayList<>(POOL_INIT_OBJECTS_COUNT);
		dataQueue = new ArrayBlockingQueue<FileTransferPart>(DATA_QUEUE_CAPACITY);
		GenericObjectPool.Config cfg = new GenericObjectPool.Config();
		cfg.maxIdle = 5;
		connectionPool = new GenericObjectPool<>(new SessionPoolFactory(dataQueue, sessionFactory), cfg);


		initPool();
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
		Connector connector = waitConnection();
		Session session = connectionPool.borrowObject();
		session.setConnector(connector);
		sessionList.add(session);
		returnClosedSession();
	}

	protected Connector waitConnection() throws IOException {
		Connector connector = connectorFactory.createConnector(CONNECTION_PORT);
		connector.waitConnection();
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

	protected void log() {
		LOG.debug("Active objects: " + connectionPool.getNumActive());
		LOG.debug("Idle objects: " + connectionPool.getNumIdle());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		log();
	}
}
