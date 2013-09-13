package ru.tyurin.filesync.shared;

import java.io.Serializable;

/**
 * User: tyurin
 * Date: 8/29/13
 * Time: 11:11 AM
 */
public class FSTransfer implements Serializable {

	private long hash;

	public long getHash() {
		return hash;
	}

	public void setHash(long hash) {
		this.hash = hash;
	}
}
