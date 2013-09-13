package ru.tyurin.filesync.client.fs;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.zip.CRC32;


public class LocalFS implements VFS {

	private FSManager manager;

	public LocalFS(FSManager manager) {
		if(manager == null){
			throw new NullPointerException("FSManager is null");
		}
		this.manager = manager;
	}

	@Override
	public long getChecksum() throws Exception {
		StringBuilder builder = new StringBuilder();
		for(FileNode file : manager.getContainer().getSortedList()){
			builder.append(file.getPath());
			builder.append(file.getHash());
		}
		CRC32 crc32 = new CRC32();
		crc32.update(builder.toString().getBytes());
		return crc32.getValue();
	}

	@Override
	public boolean isEquals(VFS other) throws Exception {
		if(getChecksum() == other.getChecksum()){
			return true;
		}
		return false;
	}

	@Override
	public FileNode getFile(String filePath) throws Exception {
		Path path = getPath(filePath);
		return getFileNode(path);
	}

	@Override
	public void removeFile(FileNode file) throws Exception {
		manager.getContainer().remove(file);
		FSUtils.removeFile(Paths.get(file.getAbsolutePath()));
	}

	@Override
	public Collection<FileNode> getFiles() throws Exception {
		return manager.getContainer().getCollection();
	}

	@Override
	public BlockNode getBlock(FileNode file, int index) throws Exception {
		if(0 > index && index < file.getBlocks().size()){
			return file.getBlocks().get(index);
		}else{
			throw new IllegalArgumentException(String.format("Block with index %d not exist", index));
		}
	}

	@Override
	public void removeBlock(FileNode file, BlockNode block) throws Exception {
		if(file.getBlocks().containsKey(block.getIndex())){
			file.getBlocks().remove(block.getIndex());
		}

	}

	@Override
	public Collection<BlockNode> getBlocks(FileNode file) throws Exception {
		return file.getBlocks().values();
	}

	@Override
	public ByteBuffer getBlockData(FileNode file, BlockNode block) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate((int) block.getSize());
		buffer.put(FSUtils.getBlockData(Paths.get(file.getAbsolutePath()), block.getIndex(), (int) block.getSize()));
		return buffer;
	}

	@Override
	public void writeBlockData(FileNode file, BlockNode block, ByteBuffer data) throws Exception {
		FSUtils.writeBlockData(Paths.get(file.getAbsolutePath()), block.getIndex(), data.array());
	}

	private FileNode getFileNode(Path path) throws IOException {
		FileNode fileNode = manager.getContainer().get(path);
		if(fileNode == null){
			Path absolutePath = getAbsolutePath(path);
			fileNode = new FileNode(path, absolutePath, FSUtils.getSize(absolutePath), FSUtils.getHash(absolutePath), getBlocks(path));
			manager.getContainer().set(fileNode);
		}
		return fileNode;
	}

	private Collection<BlockNode> getBlocks(Path path){
		return null;
	}


	private Path getAbsolutePath(Path path) throws IOException {
		Path filePath = manager.getBasePath().resolve(path);
		if(filePath == null || !Files.exists(filePath) || !Files.isWritable(filePath)){
			throw new IOException("Error to getting file " + path);
		}
		return filePath;
	}

	private Path getPath(String path){
		return Paths.get(path);
	}



}
