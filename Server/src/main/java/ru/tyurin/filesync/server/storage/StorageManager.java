package ru.tyurin.filesync.server.storage;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * User: tyurin
 * Date: 8/6/13
 * Time: 8:40 AM
 */
public class StorageManager extends Thread {

	public static final Logger LOG = Logger.getLogger(StorageManager.class);

	private static final Object monitor = new Object();

	private Path rootDirectory;


	public StorageManager(String rootDirectory) throws IOException {
		LOG.debug("Creating Storage Manager...");
		setRootDirectory(Paths.get(rootDirectory));
		LOG.debug("Storage Manager created");
	}


	@Override
	public void run() {
		LOG.debug("Starting Storage Manager");
		while (!interrupted()) {
			synchronized (monitor) {
				try {
					monitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
			}
		}
		LOG.debug("Storage Manager stopped");
	}

	protected void setRootDirectory(Path rootDirectory) throws IOException {
		if (rootDirectory == null) {
			throw new NullPointerException("Root directory can't be null");
		}
		if (!Files.exists(rootDirectory)) {
			Files.createDirectory(rootDirectory);
		}
		if (Files.isWritable(rootDirectory)) {
			this.rootDirectory = rootDirectory;
		} else {
			throw new IOException("Root directory is not writable");
		}

	}
}
