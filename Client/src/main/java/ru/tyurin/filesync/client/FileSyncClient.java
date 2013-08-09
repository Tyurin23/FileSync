package ru.tyurin.filesync.client;


import org.apache.log4j.Logger;
import ru.tyurin.filesync.client.UI.FileSyncUI;
import ru.tyurin.filesync.client.connector.ConnectionManager;
import ru.tyurin.filesync.client.fs.FSContainer;
import ru.tyurin.filesync.client.fs.FSManager;
import ru.tyurin.filesync.client.fs.FSStorage;
import ru.tyurin.filesync.client.fs.FSUtils;
import ru.tyurin.filesync.client.util.Settings;
import ru.tyurin.filesync.shared.FileBlock;
import ru.tyurin.filesync.shared.FileNode;
import ru.tyurin.filesync.shared.FileStatus;
import ru.tyurin.filesync.shared.FileTransferPart;

import javax.swing.*;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


public class FileSyncClient extends Thread {

	public static Logger LOG = Logger.getLogger(FileSyncClient.class);

	protected final int QUEUE_CAPACITY = 10;
	protected final int DELAY = 3000;
	public static int status = -1;

	protected Settings settings;

	protected FSManager fsManager;
	protected ConnectionManager connectionManager;
	protected FSContainer container;
	protected FSStorage storage;
	protected Queue<FileTransferPart> inputParts = new ArrayBlockingQueue<FileTransferPart>(QUEUE_CAPACITY);

	public FileSyncClient() throws Exception {
		this(Settings.getDefaultSettings());
	}

	public FileSyncClient(Settings settings) throws Exception {
		super("FileSyncClient");
		LOG.debug("Creating Client");
		this.settings = settings;
		storage = new FSStorage(settings.getProgramPath());
		container = storage.getContainer();

		fsManager = new FSManager(settings.getSyncDirectory(), container, inputParts);

		connectionManager = ConnectionManager.createSSLInstance();


		if (!settings.isDisableUI()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					new FileSyncUI();
				}
			});
		}
		LOG.debug("Client created");

	}

	@Override
	public void run() {
		LOG.debug("Running client");
		fsManager.start();
		connectionManager.start();
		while (!interrupted()) {
			for (FileNode node : container.getCollection()) {
				if (node.getStatus() == FileStatus.NEW) {
					status = 1;
					try {
						sendModifiedBlock(node);
					} catch (Exception e) {
						e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
						interrupt();
					}
				}
			}
			status = 0;
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				interrupt();
			}

		}
		connectionManager.interrupt();
		fsManager.interrupt();
		LOG.debug("Client stopped");
	}

	protected void sendModifiedBlock(FileNode node) throws Exception {
		boolean isSend = false;
		for (FileBlock block : node.getBlocks()) {
			if (!block.isSync()) {
				if (block.isDeleted()) {
					isSend = connectionManager.sendBlock(node, block, new byte[0]);
				} else {
					isSend = connectionManager.sendBlock(node, block, FSUtils.getBlockData(node, block));
				}
			}
			if (isSend) {
				block.setSync(true);
			}
		}
		node.setStatus(FileStatus.NORMAL);
		for (FileBlock block : node.getBlocks()) {
			if (!block.isSync()) {
				node.setStatus(FileStatus.MODIFIED);
			}
		}
	}

	public static void main(String[] args) {
		try {
			FileSyncClient client = new FileSyncClient();
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
