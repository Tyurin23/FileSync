package ru.tyurin.connector;

import ru.tyurin.fs.FileNode;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/4/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Connector {

	public List<FileNode> getStatus(FileNode node);

}
