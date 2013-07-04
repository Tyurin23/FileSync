package ru.tyurin.util;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/3/13
 * Time: 11:16 AM
 * To change this template use File | Settings | File Templates.
 */
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
