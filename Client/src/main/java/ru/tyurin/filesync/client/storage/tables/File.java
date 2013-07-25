package ru.tyurin.filesync.client.storage.tables;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "file")
public class File {

	@Id
	private String path;

	@Column(name = "size")
	private Long size;

	@Column(name = "hash")
	private Long hash;

	public File(String path, Long size, Long hash) {
		this.path = path;
		this.size = size;
		this.hash = hash;
	}


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getHash() {
		return hash;
	}

	public void setHash(Long hash) {
		this.hash = hash;
	}
}
