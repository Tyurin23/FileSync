package ru.tyurin.filesync.server.storage;

import java.util.Date;

/**
 * User: tyurin
 * Date: 8/6/13
 * Time: 9:18 AM
 */
public class BlockNode {

	public static final int BLOCK_SIZE = 1000000;

	private String id;
	private int userId;
	private String path;
	private int index;
	private byte[] data;
	private long hash;
	private long size;
	private Date dateModified;

	public BlockNode(String path, int index, int userId) {
		setPath(path);
		setUserId(userId);
		setIndex(index);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
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

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}
}
