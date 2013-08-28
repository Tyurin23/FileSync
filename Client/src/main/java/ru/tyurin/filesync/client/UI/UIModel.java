package ru.tyurin.filesync.client.UI;

import ru.tyurin.filesync.client.util.Settings;

import javax.swing.*;

/**
 * User: tyurin
 * Date: 8/19/13
 * Time: 9:54 AM
 */
public class UIModel {

	private Settings settings;

	private JFrame currentFrame;

	public JFrame getCurrentFrame() {
		return currentFrame;
	}

	public void setCurrentFrame(JFrame currentFrame) {
		this.currentFrame = currentFrame;
	}

	public UIModel() {
	}
}
