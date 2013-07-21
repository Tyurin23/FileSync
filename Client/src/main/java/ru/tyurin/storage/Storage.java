package ru.tyurin.storage;


import ru.tyurin.fs.FileNode;

import java.nio.file.Path;
import java.util.List;

public interface Storage {

	public void saveFileNode(FileNode node);

	public FileNode getFileNode(Path path);

	public List<FileNode> getAllFileNodes();

}
