package ru.tyurin.filesync.server.db.tables;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "block")
public class BlockEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "index")
	private Integer index;

	@Column(name = "hash")
	private Long hash;

	@Column(name = "size")
	private Long size;

	@Column(name = "date_modified")
	private Date dateModified;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "file_id")
	private FileEntity file;

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public Long getHash() {
		return hash;
	}

	public void setHash(Long hash) {
		this.hash = hash;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public FileEntity getFile() {
		return file;
	}

	public void setFile(FileEntity file) {
		this.file = file;
	}
}
