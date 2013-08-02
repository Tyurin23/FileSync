package ru.tyurin.filesync.client.fs;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class FSContainer {

	private Map<String, FileNode> container;

	public FSContainer() {
		container = new HashMap<>();
	}

	public int size() {
		return container.size();
	}

	public FileNode get(Path path) {
		return container.get(path.toAbsolutePath().toString());
	}

	public synchronized void set(Path path, FileNode node) {
		container.put(path.toAbsolutePath().toString(), node);
	}

	public Collection<FileNode> getCollection() {
		return container.values();
	}

}
