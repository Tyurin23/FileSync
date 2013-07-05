package ru.tyurin.fs;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/4/13
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class FSVisitor extends SimpleFileVisitor<Path> {

	private FSContainer container;

	public FSVisitor(FSContainer container) {
		this.container = container;
	}

	public FSContainer getContainer() {
		return container;
	}

	public void setContainer(FSContainer container) {
		this.container = container;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		FileNode node = new FileNode(file);
		boolean isAdded = container.addNode(node);
		if (isAdded) {
			System.out.println(String.format("%s file added.", file.getFileName()));
		}
		return FileVisitResult.CONTINUE;
	}

	/*@Override
	public FileVisitResult visitFile(Object file, BasicFileAttributes attrs) throws IOException {
		System.out.println(file.toString());
		FileNode node = new FileNode(file);
		boolean isAdded = container.addNode(node);
		if (isAdded) {
			LOG.info(String.format("%s file added. size - %d", f.getName(), f.length()));

		}
		return FileVisitResult.CONTINUE;
	}*/

}
