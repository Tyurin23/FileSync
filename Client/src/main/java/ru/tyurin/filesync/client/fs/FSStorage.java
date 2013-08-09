package ru.tyurin.filesync.client.fs;


import ru.tyurin.filesync.shared.FileNode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FSStorage {

	public static final String FILENAME = "storage.data";

	private File storageFile;

	public FSStorage(String storageFile) throws IOException {
		setStorageFile(Paths.get(storageFile));
	}

	protected FSStorage() {
	}

	public FSContainer getContainer() throws IOException {
		return load(null);
	}


	public void save(FSContainer container) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(storageFile));
		out.writeObject(container.getCollection());
		out.flush();
		out.close();
	}

	public FSContainer load(FSContainer container) throws IOException {
		if (container == null) {
			container = new FSContainer();
		}
		ObjectInputStream in = null;
		List<FileNode> nodes = new ArrayList<>();
		try {
			in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(storageFile)));
			nodes = (List<FileNode>) in.readObject();
		} catch (ClassNotFoundException | EOFException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				in.close();
		}
		container.setCollection(nodes);
		return container;
	}

	protected void setStorageFile(Path path) throws IOException {
		if (!Files.isWritable(path)) {
			throw new IllegalArgumentException("Storage dir not writable");
		}
		Path file = path.resolve(FILENAME);
		if (!Files.exists(file)) {
			Files.createFile(file);
		}
		storageFile = file.toFile();
	}

	public File getStorageFile() {
		return storageFile;
	}
}
