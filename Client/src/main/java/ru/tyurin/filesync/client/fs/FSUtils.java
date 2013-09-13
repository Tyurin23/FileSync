package ru.tyurin.filesync.client.fs;

import org.apache.commons.io.FileUtils;
import ru.tyurin.filesync.shared.BlockTransferPart;

import java.io.File;
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
		equals = equals && base.toAbsolutePath().toString().equals(node.getAbsolutePath());
		equals = equals && getSize(base) == node.getSpace();
		equals = equals && getHash(base) == node.getHash();
		return equals;
	}

	public static boolean blockEquals(BlockNode b1, BlockNode b2) {
		if (b1 == null || b2 == null) {
			return false;
		}
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

	public static void removeFile(Path file) throws IOException {
		Files.deleteIfExists(file);
	}

	public static void removeBlock(Path file, int index, int blockSize){

	}

	public static FileNode createFileNode(Path path) throws IOException {
		return new FileNode(path, path.toAbsolutePath(), getSize(path), getHash(path), getBlocks(path));
	}

	public static List<BlockNode> getBlocks(Path file) throws IOException {
		if (Files.isDirectory(file)) {
			return new ArrayList<>(1);
		}
		RandomAccessFile access = new RandomAccessFile(file.toFile(), "r");
		try {
			long fileSize = getSize(file);
			int count = (int) (fileSize / BlockNode.BLOCK_SIZE + (fileSize % BlockNode.BLOCK_SIZE != 0 ? 1 : 0));
			List<BlockNode> blocks = new ArrayList<>(count);
			for (int index = 0; index < count; index++) {
				int size = BlockNode.BLOCK_SIZE;
				if (index == count - 1) {
					size = (int) (fileSize - index * size);
				}
				BlockNode block = getBlock(index, size, access);
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

	public static byte[] getBlockData(Path path, int index, int blockSize) throws IOException {
		RandomAccessFile access = new RandomAccessFile(path.toFile(), "r");
		try {
			return getBlockData(index, blockSize, access);
		} finally {
			access.close();
		}
	}

	public static void writeBlockData(Path path, int index, byte[] data) throws IOException {
		RandomAccessFile access = new RandomAccessFile(path.toFile(), "rw");
		access.seek(index * BlockTransferPart.BLOCK_MAX_SIZE);
		access.write(data);
		access.close();
	}


	protected static BlockNode getBlock(int index, int size, RandomAccessFile input) throws IOException {
		byte[] bytes = getBlockData(index, size, input);
		BlockNode block = new BlockNode(index, getHash(bytes), size);
		return block;
	}

	protected static byte[] getBlockData(int index, int size, RandomAccessFile input) throws IOException {
		byte[] bytes = new byte[size];
		input.seek(BlockNode.BLOCK_SIZE * index);
		input.read(bytes);
//		StringBuilder builder = new StringBuilder();
//		for (byte b : bytes) {
//			builder.append(String.format("%02X", b));
//		}
////		System.out.println(index + " " +builder.toString());
		return bytes;
	}

	protected static int getBlocksCount(Path file) throws IOException {
		long size = getSize(file);
		long count = size / BlockNode.BLOCK_SIZE + (size % BlockNode.BLOCK_SIZE != 0 ? 1 : 0);
		return (int) count;
	}
}
