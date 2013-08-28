package ru.tyurin.filesync.shared;

import java.io.Serializable;

/**
 * User: tyurin
 * Date: 8/9/13
 * Time: 1:48 PM
 */
public class FileTransferPart implements Serializable {

	private String path;
	private long size;
	private long hash;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getHash() {
		return hash;
	}

	public void setHash(long hash) {
		this.hash = hash;
	}
}
