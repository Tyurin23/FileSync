package ru.tyurin.filesync.shared;

import java.io.Serializable;

public class BlockTransferPart implements Serializable {


	private String path;
	private byte[] data;
	private int blockIndex;

	public BlockTransferPart(String path, int blockIndex, byte[] data) {
		this.path = path;
		this.data = data;
		this.blockIndex = blockIndex;
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

	public int getBlockIndex() {
		return blockIndex;
	}

	public void setBlockIndex(int blockIndex) {
		this.blockIndex = blockIndex;
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
		return String.format("%s[%d]", path, data.length);
	}

}