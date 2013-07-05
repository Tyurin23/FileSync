package ru.tyurin.fs;

import org.apache.log4j.Logger;
import ru.tyurin.util.MessageSystem;

import java.io.File;
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
public class FSManager implements Runnable {

	public static Logger LOG = Logger.getLogger(FSManager.class);

	private FSContainer container;
	private Path path;
	private FSVisitor visitor;

	public FSManager(Path path) throws IOException {
		if (!(Files.isDirectory(path) && Files.isWritable(path))) {
			throw new IOException("path is not a directory of not writable");
		}
		this.path = path;
		container = new FSContainer();
		visitor = new FSVisitor(container);
	}


	private boolean refreshFS() throws IOException {
		LOG.info("Refresh");
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
			try{
				if (MessageSystem.getInstance().isRefreshFileSystem()) {
					boolean isChanged = refreshFS();
					if (isChanged) {
						syncFS();
					}
				}
			}catch (Exception e){
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
		LOG.info("FSManager stopped");
	}
}
