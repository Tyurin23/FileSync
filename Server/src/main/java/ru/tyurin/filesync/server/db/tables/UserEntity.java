package ru.tyurin.filesync.server.db.tables;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class UserEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "login")
	private String login;

	@Column(name = "password")
	private String password;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FileEntity> files = new ArrayList<>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<FileEntity> getFiles() {
		return files;
	}

	public void setFiles(List<FileEntity> files) {
		this.files = files;
	}

	public FileEntity getFile(String path) {
		for (FileEntity file : getFiles()) {
			if (file.getPath().equals(path)) {
				return file;
			}
		}
		return null;
	}

	public void addFile(FileEntity file) {
		file.setUser(this);
		getFiles().add(file);
	}
}
