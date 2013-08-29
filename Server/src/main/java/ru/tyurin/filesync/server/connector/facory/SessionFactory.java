package ru.tyurin.filesync.server.connector.facory;

import ru.tyurin.filesync.server.connector.Session;

import java.io.IOException;
import java.util.Queue;

public class SessionFactory {

	private ControllerFactory controllerFactory;

	public SessionFactory(ControllerFactory controllerFactory) {
		this.controllerFactory = controllerFactory;
	}

	public Session createSession(Queue queue) throws IOException {
		return new Session(queue, controllerFactory.createController());
	}

}
