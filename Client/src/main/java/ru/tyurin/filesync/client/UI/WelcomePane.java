package ru.tyurin.filesync.client.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: tyurin
 * Date: 8/16/13
 * Time: 2:28 PM
 */
public class WelcomePane extends JPanel implements Controlled {

	private UIController controller;

	public static final String LOGIN_BUTTON_COMMAND = "LOGIN";
	public static final String REGISTER_BUTTON_COMMAND = "REGISTER";

	private final GridBagConstraints WELCOME_LABEL_CONSTRAINTS = new GridBagConstraints(
			0, 0,
			1, 1,
			0.0, 0.1,
			GridBagConstraints.CENTER, GridBagConstraints.NONE,
			new Insets(40, 0, 0, 0),
			0, 0
	);

	private final GridBagConstraints LOGIN_BUTTON_CONSTRAINTS = new GridBagConstraints(
			0, 1,
			1, 1,
			0, 0.3,
			GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
			new Insets(0, 0, 10, 0),
			150, 10
	);

	private final GridBagConstraints REGISTER_BUTTON_CONSTRAINTS = new GridBagConstraints(
			0, 2,
			1, 1,
			0, 0.5,
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
			new Insets(10, 0, 0, 0),
			150, 10
	);


	private JLabel welcomeLabel;
	private JButton loginButton;
	private JButton registerButton;


	public WelcomePane() {
		super(new GridBagLayout());

		welcomeLabel = new JLabel("Welcome!");
		add(welcomeLabel, WELCOME_LABEL_CONSTRAINTS);

		loginButton = new JButton("Login");
		add(loginButton, LOGIN_BUTTON_CONSTRAINTS);

		registerButton = new JButton("Register");
		add(registerButton, REGISTER_BUTTON_CONSTRAINTS);
	}


	@Override
	public void setController(final UIController controller) {
		UIController old = this.controller;
		this.controller = controller;
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.showLoginPane();
			}
		});
	}

	@Override
	public UIController getController() {
		return controller;
	}


}
