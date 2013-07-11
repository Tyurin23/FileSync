package ru.tyurin.fs;

import org.apache.log4j.Logger;
import ru.tyurin.util.MessageSystem;
import ru.tyurin.util.event.Event;
import ru.tyurin.util.event.EventListener;
import ru.tyurin.util.event.EventType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/3/13
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class FSManager implements Runnable, EventListener {

	public static Logger LOG = Logger.getLogger(FSManager.class);

	private FSContainer container;
	private Path path;
	private FSVisitor visitor;
	private MessageSystem messages;

	private boolean refresh;

	public FSManager(Path path) throws IOException {
		this(path, MessageSystem.getInstance());
		this.path = path;
	}

	public FSManager(Path path, MessageSystem messages) throws IOException {
		if (!verifyPath(path)) {
			throw new IOException("path is not a directory of not writable");
		}
		container = new FSContainer();
		visitor = new FSVisitor(container);
		this.messages = messages;
		this.messages.addListener(this, EventType.REFRESH);
		this.path = path;
	}

	private boolean verifyPath(Path path) {
		if (!(Files.isDirectory(path) && Files.isWritable(path))) {
			return false;
		} else {
			return true;
		}

	}

	private boolean refreshFS() throws IOException {
//		LOG.info("Refresh");
		refreshDirectory(path);
		return false;
	}

	private void syncFS() {

	}

	private void refreshDirectory(Path path) throws IOException {
		Files.walkFileTree(path, visitor);
	}

	@Override
	public void run() {
		LOG.info("Starting FSManager...");
		while (!Thread.currentThread().isInterrupted()) {
			try {
				runTick();
			} catch (Exception e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
		LOG.info("FSManager stopped");
	}

	public void runTick() throws IOException {
		if (refresh) {
			boolean isChanged = refreshFS();
			if (isChanged) {
				syncFS();
			}
		}
	}

	@Override
	public void listen(Event e) {
		refresh = true;
	}
}
