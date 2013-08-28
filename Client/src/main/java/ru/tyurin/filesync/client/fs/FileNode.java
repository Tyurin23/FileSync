package ru.tyurin.filesync.client.fs;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FileNode implements Serializable {

	private String path;
	private String absolutePath;
	private long space;
	private long hash;
	private FileStatus status = FileStatus.NEW;
	private Map<Integer, FileBlock> blocks = new HashMap<>();


	public FileNode(Path path, long space, long hash, List<FileBlock> blocks) {
		this.path = path.toFile().toString();
		this.space = space;
		this.hash = hash;
		for (FileBlock block : blocks) {
			this.blocks.put(block.getIndex(), block);
		}
	}

	public FileNode(Path path, Path absolutePath, long space, long hash, List<FileBlock> blocks) {
		this(path, space, hash, blocks);
		this.absolutePath = absolutePath.toString();
	}

	public String getPath() {
		return path;
	}

	public String getAbsolutePath() {
		return absolutePath;
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

	public Map<Integer, FileBlock> getBlocks() {
		return blocks;
	}

	public void setBlocks(Map<Integer, FileBlock> blocks) {
		this.blocks = blocks;
	}

	public FileStatus getStatus() {
		return status;
	}

	public void setStatus(FileStatus status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof FileNode)) return false;
		FileNode n = (FileNode) obj;
		return (
				n.getPath().equals(this.getPath()) &&
						n.getHash() == this.getHash() &&
						n.getSpace() == this.getSpace() &&
						n.getStatus() == this.getStatus() &&
						(getBlocks() != null ? n.getBlocks().equals(this.getBlocks()) : (n.getBlocks() == this.getBlocks()))
		);
	}

	@Override
	public String toString() {
		return String.format("%s: (%d, %d, %d)[%s]", getPath(), getSpace(), getHash(), (getBlocks() != null ? getBlocks().size() : 0), getStatus());
	}
}
