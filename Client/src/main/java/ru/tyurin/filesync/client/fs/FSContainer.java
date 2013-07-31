package ru.tyurin.filesync.client.fs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class FSContainer {

	private Map<String, FileNode> container;

	public FSContainer() {
		container = new HashMap<>();
	}

	public boolean addNode(FileNode node) {
		String key = node.getPath();
		if (container.containsKey(key)) {
			FileNode old = container.get(key);
			if (old.equals(node)) {
				return false;
			} else {
				container.remove(key);
				container.put(key, node);
				return true;
			}
		}
		container.put(key, node);
		return true;
	}

	public int size() {
		return container.size();
	}

	public FileNode get(String path) {
		return container.get(path);
	}

	public Collection<FileNode> getCollection() {
		return container.values();
	}

	public Map<String, FileNode> getContainer() {
		return container;
	}

	public void setContainer(Map<String, FileNode> container) {
		this.container = container;
	}
}
