package ru.tyurin.filesync.client;


import org.apache.log4j.Logger;
import ru.tyurin.filesync.client.UI.FileSyncUI;
import ru.tyurin.filesync.client.connector.ConnectionManager;
import ru.tyurin.filesync.client.fs.FSContainer;
import ru.tyurin.filesync.client.fs.FSManager;
import ru.tyurin.filesync.client.fs.FSStorage;
import ru.tyurin.filesync.client.util.Settings;
import ru.tyurin.filesync.shared.FileTransferPart;

import javax.swing.*;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


public class FileSyncClient extends Thread {

	public static Logger LOG = Logger.getLogger(FileSyncClient.class);

	protected final int QUEUE_CAPACITY = 10;

	protected FSManager fsManager;
	protected ConnectionManager connectionManager;
	protected FSContainer container;
	protected FSStorage storage;
	protected Queue<FileTransferPart> inputParts = new ArrayBlockingQueue<FileTransferPart>(QUEUE_CAPACITY);

	public FileSyncClient() throws Exception {
		this(Settings.getDefaultSettings());
	}

	public FileSyncClient(Settings settings) throws Exception {
		storage = new FSStorage(settings.getStoragePath());
		container = new FSContainer();
		container.add(storage.load());//todo

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


	}

	@Override
	public void run() {
		fsManager.start();
		connectionManager.start();
		while (!interrupted()) {

		}
		connectionManager.interrupt();
		fsManager.interrupt();
	}

	public static void main(String[] args) {
		try {
			FileSyncClient client = new FileSyncClient();
			client.start();
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

}
