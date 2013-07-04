package ru.tyurin.util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/3/13
 * Time: 12:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class FSTimer implements ActionListener {

	private Timer timer;

	public FSTimer(int delay) {
		timer = new Timer(delay, this);
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MessageSystem.getInstance().refreshFileSystem();
	}
}
