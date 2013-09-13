package ru.tyurin.filesync.client.fs;

import ru.tyurin.filesync.shared.BlockTransferPart;

import java.io.Serializable;

public class BlockNode implements Serializable {

	public static final int BLOCK_SIZE = BlockTransferPart.BLOCK_MAX_SIZE;

	private int index;
	private long hash;
	private long size;
	private boolean sync = false;
	private boolean deleted = false;
	private FileNode node;//todo

	public BlockNode() {
	}

	public BlockNode(int index, long hash, long size) {
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

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
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

	public FileNode getNode() {
		return node;
	}

	public void setNode(FileNode node) {
		this.node = node;
	}

	@Override
	public String toString() {
		return String.format("%d %d (sync: %b, del: %b", this.getIndex(), this.getHash(), this.isSync(), this.isDeleted());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof BlockNode)) return false;
		BlockNode b = (BlockNode) obj;
		return equals(b);
	}

	public boolean equals(BlockNode block){
		return (
				block.getIndex() == this.getIndex() &&
						block.getHash() == this.getHash() &&
						block.isSync() == this.isSync() &&
						block.isDeleted() == this.isDeleted()
		);
	}


}
