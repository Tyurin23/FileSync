package ru.tyurin.filesync.server.connector;

import org.apache.commons.pool.BasePoolableObjectFactory;
import ru.tyurin.filesync.shared.FileTransferPart;

import java.util.Queue;

public class SessionFactory extends BasePoolableObjectFactory<Session> {

	Queue<FileTransferPart> dataQueue;
	int count = 0;

	public SessionFactory(Queue<FileTransferPart> dataQueue) {
		this.dataQueue = dataQueue;
	}

	@Override
	public Session makeObject() throws Exception {
		Session s = new Session(this.dataQueue);
		s.start();
		return s;
	}

	@Override
	public void passivateObject(Session session) throws Exception {
		session.wait();
	}

	@Override
	public void activateObject(Session session) throws Exception {
		session.notify();
	}

	@Override
	public boolean validateObject(Session session) {
		if (!session.isInterrupted()) {
			return true;
		} else {
			return false;
		}
	}
}
