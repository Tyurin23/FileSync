package ru.tyurin.filesync.server.connector.facory;

import ru.tyurin.filesync.server.connector.Session;

import java.util.Queue;

/**
 * User: tyurin
 * Date: 7/26/13
 * Time: 2:45 PM
 */
public class SessionFactory {

	public Session createSession(Queue queue) {
		return new Session(queue);
	}

}
