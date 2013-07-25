package ru.tyurin.filesync.shared;

import java.io.Serializable;

public class FileTransferPart implements Serializable {

	private String name;

	public FileTransferPart() {
	}

	public FileTransferPart(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
