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

	@Override
	public boolean equals(Object obj) {
		boolean equals = true;
		equals = equals && obj.getClass() == this.getClass();
		FileTransferPart part = (FileTransferPart) obj;
		equals = equals && part.name == this.name;
		return equals;
	}

	@Override
	public String toString() {
		return name;
	}
}
