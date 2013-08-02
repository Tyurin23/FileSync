package ru.tyurin.filesync.client;


import org.apache.log4j.Logger;
import ru.tyurin.filesync.client.UI.FileSyncUI;
import ru.tyurin.filesync.client.fs.FSManager;
import ru.tyurin.filesync.client.util.FSTimer;
import ru.tyurin.filesync.client.util.MessageSystem;
import ru.tyurin.filesync.client.util.Settings;

import javax.swing.*;


public class FileSyncClient {

	public static Logger LOG = Logger.getLogger(FileSyncClient.class);

	private Settings settings;
	private Thread messages;
	private Thread fsManager;
	private FSManager manager;
	private FSTimer timer;

	public FileSyncClient() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new FileSyncUI();
			}
		});

		LOG.info("Starting client...");
		settings = new Settings();
		messages = new Thread(MessageSystem.getInstance());
		messages.setName("Messages");
		messages.start();
//		try {
//			manager = new FSManager(Paths.get(settings.getDirectory()),);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		fsManager = new Thread(manager);
		fsManager.setName("FS");
		fsManager.start();

		timer = new FSTimer(settings.getTimeToRefresh());
		timer.start();

		//git test
	}

	public static void main(String[] args) {
		FileSyncClient client = new FileSyncClient();
	}
}
