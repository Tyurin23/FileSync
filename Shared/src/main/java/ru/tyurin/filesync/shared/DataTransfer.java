package ru.tyurin.filesync.shared;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class DataTransfer implements Serializable {

	private byte[] data;

	public DataTransfer(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
}
