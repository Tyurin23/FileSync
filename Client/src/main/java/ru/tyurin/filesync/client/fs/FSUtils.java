package ru.tyurin.filesync.client.fs;

import org.apache.commons.io.FileUtils;
import ru.tyurin.filesync.client.util.Settings;
import ru.tyurin.filesync.shared.FileBlock;
import ru.tyurin.filesync.shared.FileNode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class FSUtils {

	public static boolean compare(Path base, FileNode node) throws IOException {
		if (base == null || node == null) {
			return false;
		}
		boolean equals = true;
		equals = equals && base.toAbsolutePath().toString().equals(node.getPath());
		equals = equals && getSize(base) == node.getSpace();
		equals = equals && getHash(base) == node.getHash();
		return equals;
	}

	public static boolean blockEquals(FileBlock b1, FileBlock b2) {
		if (b1.getHash() != b2.getHash()) {
			return false;
		} else if (b1.getIndex() != b2.getIndex()) {
			return false;
		}
		return true;
	}

	public static long getSize(Path file) throws IOException {
		if (Files.isDirectory(file)) {
			return 0;
		}
		return Files.size(file);
	}

	public static long getHash(Path file) throws IOException {
		if (Files.isDirectory(file)) {
			return 0;
		}
		return FileUtils.checksumCRC32(file.toFile());
	}

	public static long getHash(byte[] bytes) {
		Checksum checksum = new CRC32();
		checksum.update(bytes, 0, bytes.length);
		return checksum.getValue();
	}

	public static FileNode createFileNode(Path path) throws IOException {
		return new FileNode(path.toAbsolutePath(), getSize(path), getHash(path), getBlocks(path));
	}

	public static List<FileBlock> getBlocks(Path file) throws IOException {
		if (Files.isDirectory(file)) {
			return new ArrayList<>(1);
		}
		RandomAccessFile access = new RandomAccessFile(file.toFile(), "r");
		try {
			long fileSize = getSize(file);
			int count = (int) (fileSize / Settings.getBlockSize() + (fileSize % Settings.getBlockSize() != 0 ? 1 : 0));
			List<FileBlock> blocks = new ArrayList<>(count);
			for (int index = 0; index < count; index++) {
				int size = Settings.getBlockSize();
				if (index == count - 1) {
					size = (int) (fileSize - index * size);
				}
				FileBlock block = getBlock(index, size, access);
				blocks.add(block);
			}
			return blocks;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (access != null) {
				access.close();
			}
		}
	}


	protected static FileBlock getBlock(int index, int size, RandomAccessFile input) throws IOException {
		byte[] bytes = new byte[size];
		input.seek(index * size);
		input.read(bytes);
		FileBlock block = new FileBlock(index, getHash(bytes));
		return block;
	}

	protected static int getBlocksCount(Path file) throws IOException {
		long size = getSize(file);
		long count = size / Settings.getBlockSize() + (size % Settings.getBlockSize() != 0 ? 1 : 0);
		return (int) count;
	}
}
