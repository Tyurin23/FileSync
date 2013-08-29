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
public class StorageManager {

	public static final Logger LOG = Logger.getLogger(StorageManager.class);
	private Path rootDirectory;


	public StorageManager(String rootDirectory) throws IOException {
		setRootDirectory(Paths.get(rootDirectory));
		LOG.debug("Storage Manager created");
	}

	public void saveBlock(BlockNode node) throws IOException {
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
