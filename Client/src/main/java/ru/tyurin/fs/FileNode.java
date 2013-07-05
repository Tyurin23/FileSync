package ru.tyurin.fs;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/3/13
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileNode implements Serializable {

	private String path;
	private long space;
	private long hash;

	public FileNode(Path path) throws IOException {
		this.path = path.toString();
		space = Files.size(path);
		hash = getHash(path);
	}

	public String getPath() {
		return path;
	}

	public long getSpace() {
		return space;
	}

	public long getHash() {
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass().equals(this.getClass())) {
			FileNode node = (FileNode) obj;
			boolean equals = true;
			equals = equals && path.equals(node.getPath());
			equals = equals && space == node.getSpace();
			equals = equals && hash == node.getHash();
			return equals;
		}
		return false;
	}

	private long getHash(Path path) throws IOException {
		InputStream istream = Files.newInputStream(path);
		CheckedInputStream ch = new CheckedInputStream(istream, new CRC32());
		ch.read();
		return ch.getChecksum().getValue();
	}
}
