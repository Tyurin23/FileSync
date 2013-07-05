package ru.tyurin;


import org.apache.log4j.Logger;
import ru.tyurin.fs.FSManager;
import ru.tyurin.util.FSTimer;
import ru.tyurin.util.MessageSystem;
import ru.tyurin.util.Settings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;


public class FileSyncClient {

	public static Logger LOG = Logger.getLogger(FileSyncClient.class);

	private Settings settings;
	private Thread messages;
	private Thread fsManager;
	private FSManager manager;
	private FSTimer timer;

	public FileSyncClient() {
		LOG.info("Starting client...");
		settings = new Settings();
		messages = new Thread(MessageSystem.getInstance());
		messages.setName("Messages");
		messages.start();
		try {
			manager = new FSManager(Paths.get(settings.getDirectory()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		fsManager = new Thread(manager);
		fsManager.setName("FS");
		fsManager.start();

		timer = new FSTimer(settings.getTimeToRefresh());
		timer.start();
	}

	public static void main(String[] args) {
		FileSyncClient client = new FileSyncClient();
	}
}
