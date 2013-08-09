package ru.tyurin.filesync.server.connector.facory;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.log4j.Logger;
import ru.tyurin.filesync.server.connector.Session;
import ru.tyurin.filesync.server.storage.BlockNode;

import java.util.Queue;

public class SessionPoolFactory extends BasePoolableObjectFactory<Session> {

	public static Logger LOG = Logger.getLogger(SessionPoolFactory.class);

	private SessionFactory factory;
	private Queue<BlockNode> dataQueue;
	private int count = 0;

	public SessionPoolFactory(Queue<BlockNode> dataQueue, SessionFactory factory) {
		this.factory = factory;
		this.dataQueue = dataQueue;
	}

	@Override
	public Session makeObject() throws Exception {
		Session s = new Session(this.dataQueue, count);
		count++;
		s.start();
		return s;
	}

	@Override
	public void passivateObject(Session session) throws Exception {
		session.waiting();
	}

	@Override
	public void activateObject(Session session) throws Exception {
		session.wakeup();

	}

	@Override
	public boolean validateObject(Session session) {
		if (!session.isInterrupted()) {
			LOG.debug("Session interrupted ");
			return true;
		} else {
			return false;
		}
	}
}
