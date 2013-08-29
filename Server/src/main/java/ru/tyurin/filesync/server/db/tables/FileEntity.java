package ru.tyurin.filesync.server.db.tables;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "file")
public class FileEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "path")
	private String path;

	@Column(name = "hash")
	private Long hash;

	@Column(name = "size")
	private Long size;

	@OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BlockEntity> blockList = new ArrayList<>();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	private UserEntity user;

	public List<BlockEntity> getBlockList() {
		return blockList;
	}

	public void setBlockList(List<BlockEntity> blockList) {
		this.blockList = blockList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getHash() {
		return hash;
	}

	public void setHash(Long hash) {
		this.hash = hash;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public BlockEntity getBlock(int index) {
		for (BlockEntity block : getBlockList()) {
			if (block.getIndex() == index) {
				return block;
			}
		}
		return null;
	}

	public void addBlock(BlockEntity block) {
		block.setFile(this);
		getBlockList().add(block);
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}
}
