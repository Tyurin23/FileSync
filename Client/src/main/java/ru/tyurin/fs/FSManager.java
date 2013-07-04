package ru.tyurin.fs;

import org.apache.log4j.Logger;
import ru.tyurin.util.MessageSystem;

import java.io.File;
import java.io.IOException;

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
	private File path;

	public FSManager(File path) throws IOException {
		if (!(path.isDirectory() && path.canWrite())) {
			throw new IOException("path is not a directory of not writable");
		}
		this.path = path;
		container = new FSContainer();
	}


	private boolean refreshFS() {
		LOG.info("Refresh");
		refreshDirectory(path);
		return false;
	}

	private void syncFS() {

	}

	private void refreshDirectory(File file) {
		if (file.canWrite() && file.isDirectory()) {
			for (File f : file.listFiles()) {
				if (f.isFile()) {
					FileNode node = new FileNode(f);
					boolean isAdded = container.addNode(node);
					if (isAdded) {
						LOG.info(String.format("%s file added. size - %d", f.getName(), f.length()));

					}
				} else if (f.isDirectory()) {
					refreshDirectory(f);
				} else {
					LOG.info("Not dir or file");
				}
			}
		} else {
			LOG.info("not write");
		}
		LOG.info("count " + container.size());

	}

	@Override
	public void run() {
		LOG.info("Starting FSManager...");
		while (!Thread.currentThread().isInterrupted()) {
			if (MessageSystem.getInstance().isRefreshFileSystem()) {
				boolean isChanged = refreshFS();
				if (isChanged) {
					syncFS();
				}
			}
		}
		LOG.info("FSManager stopped");
	}
}
