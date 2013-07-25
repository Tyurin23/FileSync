package ru.tyurin.filesync.client.util;


public class Settings {

	private static String directory = "/home/tyurin/tmp/sync/";
	private static int timeToRefresh = 1000;


	public static synchronized String getDirectory() {
		return directory;
	}

	public static synchronized int getTimeToRefresh() {
		return timeToRefresh;
	}
}
