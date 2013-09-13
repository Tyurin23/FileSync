package ru.tyurin.filesync.client.fs;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;


public class FileNode implements Serializable, Comparable<FileNode> {

	private String path;
	private String absolutePath;
	private long space;
	private long hash;
	private FileStatus status = FileStatus.NEW;
	private Map<Integer, BlockNode> blocks = new HashMap<>();
	private Date modifiedDate;


	public FileNode(String path, long space, long hash, Collection<BlockNode> blocks) {
		this.path = path;
		this.space = space;
		this.hash = hash;
		for (BlockNode block : blocks) {
			this.blocks.put(block.getIndex(), block);
		}
	}

	public FileNode(Path path, Path absolutePath, long space, long hash, Collection<BlockNode> blocks) {
		this(path.toString(), space, hash, blocks);
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

	public Map<Integer, BlockNode> getBlocks() {
		return blocks;
	}

	public void addBlock(BlockNode block) {
		block.setNode(this);
		this.blocks.put(block.getIndex(), block);
	}

	public FileStatus getStatus() {
		return status;
	}

	public void setStatus(FileStatus status) {
		this.status = status;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public boolean isDeleted(){
		return (getStatus() == FileStatus.DELETED);
	}

	public boolean isBad(){
		return (getStatus() == FileStatus.BAD);
	}

	public FileStatus compare(FileNode other){
		if(other == null){
			throw  new NullPointerException();
		}
		if(isDeleted()){
			return FileStatus.DELETED;
		}
		int status = getModifiedDate().compareTo(other.getModifiedDate());
		if(status == 0){
			return FileStatus.NORMAL;
		}else if(status < 0){
			return FileStatus.MODIFIED_SERVER_PRIORITY;
		}else if(status > 0){
			return FileStatus.MODIFIED_CLIENT_PRIORITY;
		}
		return FileStatus.BAD;
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

	@Override
	public int compareTo(FileNode o) {
		return path.compareTo(o.path);
	}
}
