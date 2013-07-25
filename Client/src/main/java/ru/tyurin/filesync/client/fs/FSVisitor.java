package ru.tyurin.filesync.client.fs;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FSVisitor extends SimpleFileVisitor<Path> {

	private FSContainer container;
	private Path base;

	private List<Path> changedNode = new ArrayList<>();

	public FSVisitor(Path base, FSContainer container) {
		this.base = base;
		this.container = container;
	}

	public List<Path> getChanged(){
		try {
			Files.walkFileTree(base, this);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		List<Path> ret = changedNode;
		changedNode = new ArrayList<>();
		return ret;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		FileNode node = container.getContainer().get(file);
		if(node == null || !FSUtils.compare(file, node)){
			changedNode.add(file);
		}
		return FileVisitResult.CONTINUE;
	}

}
