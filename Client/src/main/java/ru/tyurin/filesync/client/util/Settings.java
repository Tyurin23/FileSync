package ru.tyurin.filesync.client.util;


public class Settings {

	private static String directory = "/home/tyurin/tmp/sync/";
	private static int timeToRefresh = 1000;
	private static int blockSize = 1000000;


	public static synchronized String getDirectory() {
		return directory;
	}

	public static synchronized int getTimeToRefresh() {
		return timeToRefresh;
	}

	public static int getBlockSize() {
		return blockSize;
	}
}
