package ru.tyurin.storage;

import ru.tyurin.fs.FileNode;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

/**
 * User: tyurin
 * Date: 7/10/13
 * Time: 3:54 PM
 */
public class Storage {

	private EntityManagerFactory emf;
	private EntityManager em;

	public Storage() {
		emf = Persistence.createEntityManagerFactory("Standalone");
		em = emf.createEntityManager();
	}

	public void saveFileNode(FileNode node) {

	}

	public FileNode getFileNode(String path) {
		return null;
	}

	public void saveFileNodesList(List<FileNode> nodesList) {

	}

	public List<FileNode> getAllNodes() {
		return null;
	}
}
