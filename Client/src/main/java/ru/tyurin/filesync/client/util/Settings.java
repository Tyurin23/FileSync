package ru.tyurin.filesync.client.util;


public class Settings {

	/**
	 * Path to synchronization directory
	 */
	private String syncDirectory = "/home/tyurin/tmp/sync/";
	private static int timeToRefresh = 1000;

	/**
	 * Path to program directory
	 */
	private String programPath = "";
	private boolean disableUI = false;

	public static Settings getDefaultSettings() {
		return new Settings();
	}


	public String getSyncDirectory() {
		return syncDirectory;
	}

	public void setSyncDirectory(String syncDirectory) {
		this.syncDirectory = syncDirectory;
	}

	public static synchronized int getTimeToRefresh() {
		return timeToRefresh;
	}


	public String getProgramPath() {
		return programPath;
	}

	public boolean isDisableUI() {
		return disableUI;
	}

	public void setDisableUI(boolean disableUI) {
		this.disableUI = disableUI;
	}
}
