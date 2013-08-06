package ru.tyurin.filesync.client.util;


import java.nio.file.Path;
import java.nio.file.Paths;

public class Settings {


	private static String directory = "/home/tyurin/tmp/sync/";
	private static int timeToRefresh = 1000;
	private static int blockSize = 1000000;

	/**
	 * Path to program directory
	 */
	private static Path storagePath = Paths.get("");
	private boolean disableUI = false;

	public static Settings getDefaultSettings() {
		return new Settings();
	}


	public String getSyncDirectory() {
		return directory;
	}

	public static synchronized int getTimeToRefresh() {
		return timeToRefresh;
	}

	public static int getBlockSize() {
		return blockSize;
	}

	public Path getStoragePath() {
		return storagePath;
	}

	public boolean isDisableUI() {
		return disableUI;
	}

	public void setDisableUI(boolean disableUI) {
		this.disableUI = disableUI;
	}
}
