package ru.tyurin.filesync.shared;

import java.io.Serializable;

public class FileBlock implements Serializable {

	public static final int BLOCK_SIZE = 1000000;

	private int index;
	private long hash;
	private int size;
	private boolean sync = false;
	private boolean deleted = false;

	public FileBlock() {
	}

	public FileBlock(int index, long hash, int size) {
		this.index = index;
		this.hash = hash;
		this.size = size;
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

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
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

	@Override
	public String toString() {
		return String.format("%d %d (sync: %b, del: %b", this.getIndex(), this.getHash(), this.isSync(), this.isDeleted());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof FileBlock)) return false;
		FileBlock b = (FileBlock) obj;
		return (
				b.getIndex() == this.getIndex() &&
						b.getHash() == this.getHash() &&
						b.isSync() == this.isSync() &&
						b.isDeleted() == this.isDeleted()
		);
	}


}
