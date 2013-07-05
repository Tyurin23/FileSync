package ru.tyurin.util;


public class Settings {

	private String directory;
	private int timeToRefresh;

	public Settings() {
		directory = "/home/tyurin/tmp/sync/";
		timeToRefresh = 1000;
	}

	public String getDirectory() {
		return directory;
	}

	public int getTimeToRefresh() {
		return timeToRefresh;
	}
}
