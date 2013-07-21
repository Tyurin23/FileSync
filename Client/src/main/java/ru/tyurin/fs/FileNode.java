package ru.tyurin.fs;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/3/13
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileNode implements Serializable {

	private String path;
	private long space;
	private long hash;

	public FileNode(Path path) {
		this(path, -1, -1);
	}

	public FileNode(Path path, long space, long hash){
		this.path = path.toString();
		this.space = space;
		this.hash = hash;

	}

	public String getPath() {
		return path;
	}

	public long getSpace() {
		return space;
	}

	public long getHash() {
		return hash;
	}


}
