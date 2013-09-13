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

	private Controller controller;
	public String identifier = "";

	private boolean sleep = true;

	public Session(Controller controller) {
		this.controller = controller;
		this.setName("Session " + identifier);
	}

	public synchronized void setConnector(ServerSocketConnector connector) throws IOException {
		this.connector = connector;
		wakeup();
		LOG.debug(String.format("Session %s has new connection", identifier));
	}

	public boolean isClose() {
		return (connector == null);
	}

	public synchronized void waiting() {
		LOG.debug(String.format("Session %s waiting", identifier));
		this.sleep = true;
	}


	public synchronized void wakeup() {
		this.sleep = false;
		notifyAll();
		LOG.debug(String.format("Session %s wakeup", identifier));
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				if (sleep) {
					synchronized (this) {
						LOG.debug("WAIT " + identifier);
						wait();
						LOG.debug("NOTIFY " + identifier);
					}
				}
				tick();
			} catch (Exception e) {
				e.printStackTrace();
				interrupt();
			}
		}
	}

	protected void tick() throws Exception {
		if (connector != null) {
			Request req = connector.getRequest();
			if (req != null) {
				if(!controller.hasController(req)){
					connector.sendStatus(ConnectionStatus.REQUEST_ERROR);
					LOG.info("Error request: " + req);
				}else if(!controller.isAuthRequired(req) || connector.getUserID() != null){
					connector.sendStatus(ConnectionStatus.OK);
					ConnectionStatus status = controller.processRequest(req, connector);
					if (status != null) {
						connector.sendStatus(status);
					}
				}else{
					connector.sendStatus(ConnectionStatus.AUTHENTICATION_ERROR);
				}
			}
		} else {
			sleep = true;
		}
	}
}
