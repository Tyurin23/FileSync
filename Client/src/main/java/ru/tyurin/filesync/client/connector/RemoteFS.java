package ru.tyurin.filesync.client.connector;

import ru.tyurin.filesync.client.fs.BlockNode;
import ru.tyurin.filesync.client.fs.FileNode;
import ru.tyurin.filesync.client.fs.VFS;
import ru.tyurin.filesync.shared.BlockTransferPart;
import ru.tyurin.filesync.shared.DataTransfer;
import ru.tyurin.filesync.shared.FileTransferPart;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: tyurin
 * Date: 9/3/13
 * Time: 9:26 AM
 */
public class RemoteFS implements VFS {

	private ConnectionManager manager;

	public RemoteFS(ConnectionManager manager) {
		this.manager = manager;
	}

	@Override
	public long getChecksum() throws Exception {
		return manager.getChecksum();
	}

	@Override
	public boolean isEquals(VFS other) throws Exception {
		if(getChecksum() == other.getChecksum()){
			return true;
		}else {
			return false;
		}
	}

	@Override
	public FileNode getFile(String path) throws Exception {
		FileTransferPart transferNode = manager.getFileNode(path);
		if(transferNode == null){
			return null;
		}
		FileNode file = new FileNode(transferNode.getPath(), transferNode.getSize(), transferNode.getHash(), getBlocks(path));
		return file;
	}

	@Override
	public void removeFile(FileNode file) throws Exception {
		manager.removeFile(file);
	}

	@Override
	public Collection<FileNode> getFiles() throws Exception {
		List<FileNode> fileNodes = new ArrayList<>();
		for(FileTransferPart transferPart : manager.getFileNodes()){
			FileNode file = new FileNode(transferPart.getPath(), transferPart.getSize(), transferPart.getHash(), getBlocks(transferPart.getPath()));
			fileNodes.add(file);
		}
		return fileNodes;
	}

	@Override
	public BlockNode getBlock(FileNode file, int index) throws Exception {
		BlockTransferPart transferPart = manager.receiveBlock(file.getPath(), index);
		if(transferPart == null){
			return null;
		}
		BlockNode block = new BlockNode(transferPart.getIndex(), transferPart.getHash(), transferPart.getSize());
		return block;
	}

	@Override
	public void removeBlock(FileNode file, BlockNode block) throws Exception {

	}

	@Override
	public Collection<BlockNode> getBlocks(FileNode file) throws Exception {
		return getBlocks(file.getPath());
	}

	@Override
	public ByteBuffer getBlockData(FileNode file, BlockNode block) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate((int) block.getSize());
		buffer.put(manager.getBlockData(file.getPath(), block.getIndex()).getData());
		return buffer;
	}

	@Override
	public void writeBlockData(FileNode file, BlockNode block, ByteBuffer data) throws Exception {
		DataTransfer dataTransfer = new DataTransfer(data.array());
		manager.sendBlockData(file.getPath(), block.getIndex(), dataTransfer);
	}

	private Collection<BlockNode> getBlocks(String path) throws Exception {
		Collection<BlockTransferPart> transferParts = manager.getBlocks(path);
		List<BlockNode> blocks = new ArrayList<>();
		for (BlockTransferPart transferPart : transferParts){
			BlockNode block = new BlockNode(transferPart.getIndex(), transferPart.getHash(), transferPart.getSize());
			blocks.add(block);
		}
		return blocks;
	}
}
