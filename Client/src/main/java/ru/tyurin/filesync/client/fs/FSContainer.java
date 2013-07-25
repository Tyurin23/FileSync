package ru.tyurin.filesync.client.fs;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/3/13
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
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

	public Map<String, FileNode> getContainer() {
		return container;
	}

	public void setContainer(Map<String, FileNode> container) {
		this.container = container;
	}
}
