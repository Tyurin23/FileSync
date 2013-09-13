package ru.tyurin.filesync.shared;

import java.io.Serializable;

public class BlockTransferPart implements Serializable {

	public static final int BLOCK_MAX_SIZE = 10;

	private String path;
	private byte[] data;
	private int index;
	private long hash;
	private long size;

	public BlockTransferPart(String path, int index, byte[] data) {
		this.path = path;
		this.data = data;
		this.index = index;
	}

	public BlockTransferPart(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
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

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof BlockTransferPart)) {
			return false;
		}
		BlockTransferPart part = (BlockTransferPart) obj;
		boolean equals = true;
		equals &= part.equals(part.path);
		equals &= data.equals(part.data);
		return equals;
	}

	@Override
	public String toString() {
		return String.format("%s[%d]", path, data == null ? 0 : data.length);
	}

}
