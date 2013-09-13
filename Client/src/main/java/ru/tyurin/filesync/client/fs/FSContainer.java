package ru.tyurin.filesync.client.fs;

import org.apache.commons.io.FileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.CRC32;


public class FSContainer {

	private Map<String, FileNode> container;

	public FSContainer() {
		container = new HashMap<>();
	}

	public int size() {
		return container.size();
	}

	public FileNode get(Path path) {
		return container.get(path);
	}

	public void set(FileNode node) {
		container.put(node.getPath(), node);
	}

	public Collection<FileNode> getCollection() {
		return container.values();
	}

	public List<FileNode> getSortedList(){
		List<FileNode> sortedList = new ArrayList<>(getCollection());
		Collections.sort(sortedList);
		return sortedList;
	}

	public boolean containsFileNode(FileNode fileNode){
		return container.containsKey(fileNode.getPath());
	}

	public void remove(FileNode fileNode){
		if(containsFileNode(fileNode)){
			container.remove(fileNode.getPath());
		}
	}



	public void setCollection(Collection<FileNode> nodes) {
		container.clear();
		for (FileNode node : nodes) {
			set(node);
		}
	}
}
