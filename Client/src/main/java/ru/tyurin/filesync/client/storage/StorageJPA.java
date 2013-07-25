package ru.tyurin.filesync.client.storage;

import ru.tyurin.filesync.client.fs.FileNode;
import ru.tyurin.filesync.client.storage.tables.File;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * User: tyurin
 * Date: 7/10/13
 * Time: 3:54 PM
 */
public class StorageJPA implements Storage {

	private EntityManagerFactory emf;
	private EntityManager em;

	public StorageJPA() {
		emf = Persistence.createEntityManagerFactory("Standalone");
		em = emf.createEntityManager();
	}

	public void saveFileNode(FileNode node) {
		File fileEntity = new File(node.getPath(), node.getSpace(), node.getHash());
		save(fileEntity);
	}

	@Override
	public FileNode getFileNode(Path path) {
		File fileEntity = em.find(File.class, path.toString());
		return new FileNode(Paths.get(fileEntity.getPath()), fileEntity.getSize(), fileEntity.getHash());
	}

	@Override
	public List<FileNode> getAllFileNodes() {
//		for(File fileEntity : em.)
		return null;
	}

	public FileNode getFileNode(String path) {
		return null;
	}

	public void saveFileNodesList(List<FileNode> nodesList) {

	}

	public List<FileNode> getAllNodes() {
		return null;
	}

	private void save(Object o){
		em.merge(o);
	}


}
