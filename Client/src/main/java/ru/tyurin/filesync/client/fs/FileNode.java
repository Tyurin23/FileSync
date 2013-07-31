package ru.tyurin.filesync.client.fs;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;


public class FileNode implements Serializable {

	private String path;
	private long space;
	private long hash;
	private FileStatus status = FileStatus.NEW;
	private List<FileBlock> blocks;


	public FileNode(Path path, long space, long hash, List<FileBlock> blocks) {
		this.path = path.toString();
		this.space = space;
		this.hash = hash;
		this.blocks = blocks;
	}

	public String getPath() {
		return path;
	}

	public long getSpace() {
		return space;
	}

	public void setSpace(long space) {
		this.space = space;
	}

	public long getHash() {
		return hash;
	}

	public void setHash(long hash) {
		this.hash = hash;
	}

	public List<FileBlock> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<FileBlock> blocks) {
		this.blocks = blocks;
	}

	public FileStatus getStatus() {
		return status;
	}

	public void setStatus(FileStatus status) {
		this.status = status;
	}
}
