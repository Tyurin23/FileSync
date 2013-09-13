package ru.tyurin.filesync.client.fs;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.shared.BlockTransferPart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FSManager {

	public static Logger LOG = Logger.getLogger(FSManager.class);

	private FSContainer container;
	private Path base;
	private FSVisitor visitor;
	private LocalFS fs;

	private final boolean disableHidden = true;

	public FSManager(String path, FSContainer container) throws IOException {
		if ((base = getVerifyPath(path)) == null) {
			throw new IOException(String.format("path %s is not a directory of not writable", path));
		}
		this.container = container;
		visitor = new FSVisitor(this.base, this.container);
		this.fs = new LocalFS(this);
	}

	public Path getBasePath() {
		return base;
	}

	public void refreshFS() throws IOException {
		checkChanges();
	}

	public VFS getFileSystem() throws IOException {
		refreshFS();
		return fs;
	}

	public FSContainer getContainer(){
		return container;
	}



	private Path getVerifyPath(String path) {
		Path verifiedPath = Paths.get(path);
		if (path != null && Files.isDirectory(verifiedPath) && Files.isWritable(verifiedPath)) {
			return verifiedPath;
		} else {
			return null;
		}
	}


	private List<FileNode> getChangedNodes(List<Path> changed) throws IOException {
		List<FileNode> nodesList = new ArrayList<>(changed.size());
		for (Path watchedFile : changed) {
			FileNode node = container.get(watchedFile);
			if (node == null) {
				node = new FileNode(
						base.relativize(watchedFile),
						watchedFile.toAbsolutePath(),
						FSUtils.getSize(watchedFile),
						FSUtils.getHash(watchedFile),
						FSUtils.getBlocks(watchedFile)
				);
				if (isIncompatibleFile(watchedFile)) {
					node.setStatus(FileStatus.BAD);
				}
			} else {
				if (Files.exists(Paths.get(node.getAbsolutePath()))) {
					node.setStatus(FileStatus.MODIFIED_CLIENT_PRIORITY);
					refreshBlocks(node);
				} else {
					node.setStatus(FileStatus.DELETED);
				}
			}
			nodesList.add(node);
		}
		return nodesList;
	}

	protected void refreshBlocks(FileNode node) throws IOException {
		List<BlockNode> newBlocks = FSUtils.getBlocks(Paths.get(node.getAbsolutePath()));
		LOG.debug(String.format("nb %d, ob %d", newBlocks.size(), node.getBlocks().size()));
		if (newBlocks.size() < node.getBlocks().size()) {

			for (int i = node.getBlocks().size() - newBlocks.size() - 1; i < node.getBlocks().size(); i++) {
				BlockNode block = node.getBlocks().get(i);
				block.setDeleted(true);
				block.setSync(false);
			}
		}
		for (BlockNode block : newBlocks) {
			if (block.getIndex() > node.getBlocks().size() || !FSUtils.blockEquals(block, node.getBlocks().get(block.getIndex()))) {
				node.getBlocks().put(block.getIndex(), block);
			}
		}
	}

	protected void checkChanges() throws IOException {
		List<Path> changedFiles = visitor.getChanges();
		List<FileNode> changedNodes = getChangedNodes(changedFiles);
		for (FileNode node : changedNodes) {
			container.set(node);
		}
	}

	protected boolean isIncompatibleFile(Path file) throws IOException {
		if (Files.isHidden(file)) {
			return true;
		}
		return false;
	}

}
