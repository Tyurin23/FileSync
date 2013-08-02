package ru.tyurin.filesync.client.fs;

public class FileBlock {

	private int index;
	private long hash;
	private boolean sync = false;
	private boolean deleted = false;

	public FileBlock() {
	}

	public FileBlock(int index, long hash) {
		this.index = index;
		this.hash = hash;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public long getHash() {
		return hash;
	}

	public void setHash(long hash) {
		this.hash = hash;
	}

	public boolean isSync() {
		return sync;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}
}
