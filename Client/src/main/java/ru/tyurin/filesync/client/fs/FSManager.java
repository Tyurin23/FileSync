package ru.tyurin.filesync.client.fs;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.shared.BlockTransferPart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class FSManager extends Thread {

	public static Logger LOG = Logger.getLogger(FSManager.class);

	private FSContainer container;
	private Queue<BlockTransferPart> input;
	private Path base;
	private FSVisitor visitor;

	private long hash = 0;

	private final boolean disableHidden = true;
	private final int TIMER = 1000;

	public FSManager(String path, FSContainer container, Queue<BlockTransferPart> input) throws IOException {
		super("FSManager");
		Path p = Paths.get(path);
		if (!verifyPath(p)) {
			throw new IOException("path is not a directory of not writable");
		}
		this.base = p;
		this.container = container;
		visitor = new FSVisitor(this.base, this.container);
		this.input = input;
	}

	public synchronized long getHash() {
		if (hash == 0) {
			refreshHash();
		}
		return hash;
	}

	private void refreshHash() {

	}

	private boolean verifyPath(Path path) {
		if (path == null && !(Files.isDirectory(path) && Files.isWritable(path))) {
			return false;
		} else {
			return true;
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
					node.setStatus(FileStatus.MODIFIED);
					refreshBlocks(node);
				} else {
					node.setStatus(FileStatus.DELETED);
				}
			}
			nodesList.add(node);
		}
		return nodesList;
	}

	protected FileNode getChange(Path path) {

		return null;
	}

	protected void refreshBlocks(FileNode node) throws IOException {
		List<FileBlock> newBlocks = FSUtils.getBlocks(Paths.get(node.getAbsolutePath()));
		LOG.debug(String.format("nb %d, ob %d", newBlocks.size(), node.getBlocks().size()));
		if (newBlocks.size() < node.getBlocks().size()) {

			for (int i = node.getBlocks().size() - newBlocks.size() - 1; i < node.getBlocks().size(); i++) {
				FileBlock block = node.getBlocks().get(i);
				block.setDeleted(true);
				block.setSync(false);
			}
		}
		for (FileBlock block : newBlocks) {
			if (block.getIndex() > node.getBlocks().size() || !FSUtils.blockEquals(block, node.getBlocks().get(block.getIndex()))) {
				node.getBlocks().put(block.getIndex(), block);
			}
		}
	}


	protected void saveInput() {

	}

	protected void checkChanges() throws IOException {
		List<Path> changedFiles = visitor.getChanges();
		List<FileNode> changedNodes = getChangedNodes(changedFiles);
		for (FileNode node : changedNodes) {
			container.set(node);
		}
	}


	@Override
	public void run() {
		LOG.info("Starting FSManager...");
		while (!this.isInterrupted()) {
			try {
				runTick();
				Thread.sleep(TIMER);
			} catch (InterruptedException e) {
				LOG.info("sleep interrupted");
				this.interrupt();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LOG.info("FSManager stopped");
	}

	protected void runTick() throws IOException {
		saveInput();
		checkChanges();
	}


	protected boolean isIncompatibleFile(Path file) throws IOException {
		if (Files.isHidden(file)) {
			return true;
		}
		return false;
	}

}
