package ru.tyurin.filesync.client.UI;

import javax.swing.*;
import java.awt.*;

/**
 * User: tyurin
 * Date: 8/16/13
 * Time: 1:31 PM
 */
public class BasicConfigurationFrame extends JFrame implements Controlled {

	private UIController controller;

	private WelcomePane welcomePane = new WelcomePane();
	private LoginPanel loginPane = new LoginPanel();
	private SettingsPane settingsPane = new SettingsPane();
	private RegisterPane registerPane = new RegisterPane();


	private static final String TITLE = "Hello";
	private static final int WINDOW_WIDTH = 400;
	private static final int WINDOW_HEIGHT = 600;

	public BasicConfigurationFrame() throws HeadlessException {
		super(TITLE);
		setResizable(false);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	public void showWelcomePane() {
		setContentPane(welcomePane);
		setVisible(true);
	}

	public void showLoginPane() {
		setContentPane(loginPane);
		setVisible(true);
	}

	public void showSettingsPane() {
		setContentPane(settingsPane);
		setVisible(true);
	}

	public void showRegisterPane() {
		setContentPane(registerPane);
		setVisible(true);
	}

	@Override
	public void setController(UIController controller) {
		this.controller = controller;
		welcomePane.setController(controller);
		loginPane.setController(controller);
		settingsPane.setController(controller);
	}

	@Override
	public UIController getController() {
		return controller;
	}

}
