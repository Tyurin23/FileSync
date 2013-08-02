package ru.tyurin.filesync.client.fs;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FSVisitor extends SimpleFileVisitor<Path> {

	private FSContainer container;
	private Path root;

	private List<Path> changedNode = new ArrayList<>();

	public FSVisitor(Path root, FSContainer container) {
		if (root == null || container == null) {
			throw new NullPointerException("Null argument expected");
		}
		if (!Files.isDirectory(root)) {
			throw new IllegalArgumentException("Root path is not a directory");
		}
		if (!Files.isReadable(root)) {
			throw new IllegalArgumentException("Root path is not readable");
		}
		this.root = root;
		this.container = container;
	}

	public List<Path> getChanges() throws IOException {
		Files.walkFileTree(root, this);
		findDeletedNode();
		List<Path> changes = new ArrayList<>(changedNode);
		changedNode.clear();
		return changes;
	}


	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		isChanged(file);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		if (!dir.equals(root)) {
			isChanged(dir);
		}
		return FileVisitResult.CONTINUE;
	}

	protected void isChanged(Path file) throws IOException {
		FileNode node = container.get(file.toAbsolutePath());
		if (node == null || !FSUtils.compare(file.toAbsolutePath(), node)) {
			changedNode.add(file.toAbsolutePath());
		}
	}

	protected void findDeletedNode() {
		for (FileNode node : container.getCollection()) {
			Path path = Paths.get(node.getPath()).toAbsolutePath();
			if (Files.notExists(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) {
				changedNode.add(path);
			}
		}
	}


}
