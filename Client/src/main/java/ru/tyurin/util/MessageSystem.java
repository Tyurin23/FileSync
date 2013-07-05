package ru.tyurin.util;

import org.apache.log4j.Logger;
import ru.tyurin.fs.FileNode;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/3/13
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageSystem implements Runnable {

	public static Logger LOG = Logger.getLogger(MessageSystem.class);

	private static MessageSystem system;

	private boolean refreshFileSystem = false;

	private Queue<FileNode> queue;

	private MessageSystem() {
		super();
		queue = new ArrayDeque<>();
	}

	public void refreshFileSystem() {
		refreshFileSystem = true;
	}

	public boolean isRefreshFileSystem() {
		try {
			return refreshFileSystem;
		} finally {
			refreshFileSystem = false;
		}

	}

	public static synchronized MessageSystem getInstance() {
		if (system == null) {
			system = new MessageSystem();
		}
		return system;
	}

	@Override
	public void run() {
		LOG.info("Starting message system...");
		while (!Thread.currentThread().isInterrupted()) {
		}
		LOG.info("Message system stopped");
	}
}
