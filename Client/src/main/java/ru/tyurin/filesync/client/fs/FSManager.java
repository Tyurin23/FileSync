package ru.tyurin.filesync.client.fs;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.client.util.MessageSystem;
import ru.tyurin.filesync.client.util.event.Event;
import ru.tyurin.filesync.client.util.event.EventListener;
import ru.tyurin.filesync.client.util.event.EventType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
	private FSWatcher watcher;

	private boolean refresh;
	private final boolean disableHidden = true;
	private boolean init = false;

	public FSManager(Path path) throws IOException {
		this(path, MessageSystem.getInstance());
	}

	public FSManager(Path path, MessageSystem messages) throws IOException {
		if (!verifyPath(path)) {
			throw new IOException("path is not a directory of not writable");
		}
		container = new FSContainer();
		this.messages = messages;
		this.messages.addListener(this, EventType.REFRESH);
		this.path = path;
		visitor = new FSVisitor(this.path, this.container);
		watcher = new FSWatcher(this.path);
	}


	private boolean verifyPath(Path path) {
		if (!(Files.isDirectory(path) && Files.isWritable(path))) {
			return false;
		} else {
			return true;
		}

	}


	private List<FileNode> getChanged(List<Path> changed) throws IOException {
		List<FileNode> nodesList = new ArrayList<>();
		for(Path watchedFile : changed){
			if(disableHidden && Files.isHidden(watchedFile)) {
				continue;
			}
			FileNode node = new FileNode(watchedFile);
			nodesList.add(node);
		}
 		return nodesList;
	}




	private void sendChanges(List<FileNode> changed){

	}

	@Override
	public void run() {
		LOG.info("Starting FSManager...");
		while (!Thread.currentThread().isInterrupted()) {
			try {
				runTick();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LOG.info("FSManager stopped");
	}

	public void runTick() throws IOException {
		if(!init){
			sendChanges(getChanged(visitor.getChanged()));
			init = true;
		}
		if (refresh) {
			sendChanges(getChanged(watcher.getWatched()));
		}
	}

	@Override
	public synchronized void listen(Event e) {
		refresh = true;
	}
}
