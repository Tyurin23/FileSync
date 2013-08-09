package ru.tyurin.filesync.server.storage;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.RandomAccessFile;
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


	private Path rootDirectory;


	public StorageManager(String rootDirectory) throws IOException {
		super("StorageManager");
		LOG.debug("Creating Storage Manager...");
		setRootDirectory(Paths.get(rootDirectory));
		LOG.debug("Storage Manager created");
	}

	public synchronized void saveBlock(BlockNode node) throws IOException {
		Path path = rootDirectory.resolve(String.valueOf(node.getUserId())).resolve(node.getPath());
		if (!Files.exists(path)) {
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		}
		RandomAccessFile access = new RandomAccessFile(path.toFile(), "rw");

		access.seek(node.getIndex() * BlockNode.BLOCK_SIZE);
		access.write(node.getData());
		access.close();
	}


	@Override
	public void run() {
		LOG.debug("Starting Storage Manager");
		while (!interrupted()) {
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					interrupt();
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
