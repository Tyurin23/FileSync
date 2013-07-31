package ru.tyurin.filesync.client.fs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class FSUtils {

	public static boolean compare(Path base, FileNode node) {
		if (base == null || node == null) {
			return false;
		}
		boolean equals = true;
		equals = equals && base.toAbsolutePath().toString().equals(node.getPath());
		equals = equals && getSpace(base) == node.getSpace();
		equals = equals && getHash(base) == node.getHash();
		return equals;
	}

	public static long getSpace(Path file) {
		try {
			return Files.size(file);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static long getHash(Path file) {
		InputStream istream = null;
		try {
			istream = Files.newInputStream(file);
			CheckedInputStream ch = new CheckedInputStream(istream, new CRC32());
			ch.read();
			return ch.getChecksum().getValue();
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static FileNode createFileNode(Path path) {
		return new FileNode(path, getSpace(path), getHash(path), getBlocks(path));
	}

	public static List<FileBlock> getBlocks(Path file) {
		List<FileBlock> blocks = new ArrayList<>();
		return blocks;
	}

	protected static FileBlock getBlock(Path file, int index) {
		return null;
	}
}
