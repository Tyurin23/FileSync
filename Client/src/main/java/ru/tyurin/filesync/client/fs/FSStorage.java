package ru.tyurin.filesync.client.fs;


import ru.tyurin.filesync.shared.FileNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FSStorage {

	private final String fileName = "storage.data";

	private File storageFile;

	public FSStorage(Path storageFile) throws IOException {
		setStorageFile(storageFile);
	}

	protected FSStorage() {
	}

	public void save(List<FileNode> nodes) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(storageFile));
		out.writeObject(nodes);
		out.flush();
		out.close();
	}

	public List<FileNode> load() throws IOException {
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(storageFile)));
		List<FileNode> nodes = null;
		try {
			nodes = (List<FileNode>) in.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
		return nodes;
	}

	protected void setStorageFile(Path path) throws IOException {
		if (!Files.isWritable(path)) {
			throw new IllegalArgumentException("Storage dir not writable");
		}
		Path file = path.resolve(fileName);
		if (!Files.exists(file)) {
			Files.createFile(file);
		}
		storageFile = file.toFile();
	}

	public File getStorageFile() {
		return storageFile;
	}
}
