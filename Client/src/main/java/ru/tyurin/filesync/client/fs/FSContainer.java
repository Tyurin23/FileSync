package ru.tyurin.filesync.client.fs;

import ru.tyurin.filesync.shared.FileNode;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class FSContainer {

	private Map<String, FileNode> container;

	public FSContainer() {
		container = new ConcurrentHashMap<>();
	}

	public int size() {
		return container.size();
	}

	public FileNode get(Path path) {
		return container.get(path.toAbsolutePath().toString());
	}

	public synchronized void set(FileNode node) {
		container.put(Paths.get(node.getAbsolutePath()).toString(), node);
	}

	public synchronized Collection<FileNode> getCollection() {
		return container.values();
	}

	public void setCollection(Collection<FileNode> nodes) {
		container.clear();
		for (FileNode node : nodes) {
			set(node);
		}
	}

}
