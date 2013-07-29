package ru.tyurin.filesync.client.connector;

import ru.tyurin.filesync.client.util.MessageSystem;


public class Receaver implements Runnable {

	private Connector connector;
	private MessageSystem messages;

	public Receaver() {
		messages = MessageSystem.getInstance();
	}

	public Receaver(Connector connector, MessageSystem messages) {
		this.connector = connector;
		this.messages = messages;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {

		}
	}
}
