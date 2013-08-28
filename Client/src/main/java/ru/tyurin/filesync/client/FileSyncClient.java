package ru.tyurin.filesync.client;


import org.apache.log4j.Logger;
import ru.tyurin.filesync.client.UI.FileSyncUI;
import ru.tyurin.filesync.client.connector.ConnectionManager;
import ru.tyurin.filesync.client.fs.*;
import ru.tyurin.filesync.client.util.Settings;
import ru.tyurin.filesync.shared.BlockTransferPart;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

//todo exception handler
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
	protected FileSyncUI ui;
	protected Queue<BlockTransferPart> inputParts = new ArrayBlockingQueue<BlockTransferPart>(QUEUE_CAPACITY);

	public static FileSyncClient loadClient() throws Exception {
		Settings settings;
		if (!Settings.isSettingsExist(null) || !(settings = Settings.loadSettings(null)).isConfigured()) {
			firstStartup();
			settings = Settings.loadSettings(null);
		}
		if (settings == null) {
			return null;
		}
		return new FileSyncClient(settings);
	}

	public static void firstStartup() throws InterruptedException {
		FileSyncUI ui = new FileSyncUI();
		ui.showStartupConfig();
		synchronized (ui.monitor) {
			System.out.println("Block");
			ui.monitor.wait();
			System.out.println("notify!");
		}
	}

	public static boolean isFirstStartup() {
		Settings settings;
		try {
			if (Settings.isSettingsExist(null) && (settings = Settings.loadSettings(null)).isConfigured()) {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}


	public FileSyncClient() throws Exception {
		this(Settings.getDefaultSettings());
	}

	public FileSyncClient(Settings settings) throws Exception {
		super("FileSyncClient");
		LOG.debug("Creating Client");
		this.settings = settings;

		if (!settings.isDisableUI()) {
			ui = new FileSyncUI();

		}

		storage = new FSStorage(settings.getProgramPath());
		container = storage.getContainer();

		fsManager = new FSManager(settings.getSyncDirectory(), container, inputParts);

		connectionManager = ConnectionManager.createSSLInstance();//todo
		connectionManager.setLogin(settings.getLogin());
		connectionManager.setPass(settings.getPassword());
		LOG.debug("Client created");

	}

	@Override
	public void run() {
		LOG.debug("Running client");
		fsManager.start();
		while (!interrupted()) {
			try {
				if (connectionManager.testAuthorization()) {
					for (FileNode node : container.getCollection()) {
						if (node.getStatus() == FileStatus.NEW || node.getStatus() == FileStatus.MODIFIED) {
							status = 1;
							try {
								sendModifiedBlock(node);
							} catch (Exception e) {
								e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
								interrupt();
							}
						}
					}
				} else {
					Thread.sleep(3000);
					continue;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}


			status = 0;
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				interrupt();
			}

		}
		fsManager.interrupt();
		LOG.debug("Client stopped");
	}

	protected void sendModifiedBlock(FileNode node) throws Exception {
		boolean isSend = false;
		for (FileBlock block : node.getBlocks().values()) {
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
		for (FileBlock block : node.getBlocks().values()) {
			if (!block.isSync()) {
				node.setStatus(FileStatus.MODIFIED);
			}
		}
	}

	public static void main(String[] args) {

		try {
			if (FileSyncClient.isFirstStartup()) {
				FileSyncClient.firstStartup();
			}
			FileSyncClient client = FileSyncClient.loadClient();
			if (client != null) {
				client.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
