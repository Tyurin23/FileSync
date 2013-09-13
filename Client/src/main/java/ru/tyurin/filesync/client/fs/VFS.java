package ru.tyurin.filesync.client.fs;


import java.nio.ByteBuffer;
import java.util.Collection;

public interface VFS {

	public long getChecksum() throws Exception;

	public boolean isEquals(VFS other) throws Exception;

	public FileNode getFile(String path) throws Exception;

	public void removeFile(FileNode file) throws Exception;

	public Collection<FileNode> getFiles() throws Exception;

	public BlockNode getBlock(FileNode file, int index) throws Exception;

	public void removeBlock(FileNode file, BlockNode block) throws Exception;

	public Collection<BlockNode> getBlocks(FileNode file) throws Exception;

	public ByteBuffer getBlockData(FileNode file, BlockNode block) throws Exception;

	public void writeBlockData(FileNode file, BlockNode block, ByteBuffer data) throws Exception;


}
