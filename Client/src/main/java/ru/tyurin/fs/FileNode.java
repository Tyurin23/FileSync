package ru.tyurin.fs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/3/13
 * Time: 11:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileNode {

	private String path;
	private long space;
	private long hash;

	public FileNode(File file) {
		path = file.getPath();
		space = file.length();
		try {
			hash = getHash(file);
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
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

	private long getHash(File file) throws IOException {
		InputStream istream = new FileInputStream(file);
		CheckedInputStream ch = new CheckedInputStream(istream, new CRC32());
		ch.read();
		return ch.getChecksum().getValue();
	}
}
