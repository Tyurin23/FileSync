package ru.tyurin.filesync.client.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * User: tyurin
 * Date: 8/27/13
 * Time: 12:59 PM
 */
public class RegisterPane extends JPanel implements Controlled {

	private UIController controller;

	private final GridBagConstraints LOGIN_LABEL_CONSTRAINTS = new GridBagConstraints(
			0, 0,
			1, 1,
			0, 0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(10, 0, 0, 0),
			0, 0
	);

	private final GridBagConstraints LOGIN_FIELD_CONSTRAINTS = new GridBagConstraints(
			1, 0,
			1, 1,
			0, 0,
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
			new Insets(10, 0, 0, 0),
			0, 0
	);
	private final GridBagConstraints PASSWORD_LABEL_CONSTRAINTS = new GridBagConstraints(
			0, 1,
			1, 1,
			0, 0,
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
			new Insets(10, 0, 0, 0),
			0, 0
	);
	private final GridBagConstraints PASSWORD_FIELD_CONSTRAINTS = new GridBagConstraints(
			1, 1,
			1, 1,
			0, 0,
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
			new Insets(10, 0, 0, 0),
			0, 0
	);

	private final GridBagConstraints REGISTER_BUTTON_CONSTRAINTS = new GridBagConstraints(
			0, 3,
			1, 1,
			0, 0,
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
			new Insets(10, 0, 0, 10),
			0, 0
	);

	private final String LOGIN_LABEL_TEXT = "Login:";
	private final String LOGIN_FIELD_TEXT = "";
	private final String PASSWORD_LABEL_TEXT = "Password:";
	private final String REGISTER_BUTTON_TEXT = "Register!";


	private JLabel loginLabel;
	private JLabel passwordLabel;
	private JTextField loginField;
	private JPasswordField passwordField;
	private JButton registerButton;

	public RegisterPane() {
		super(new GridBagLayout());

		loginLabel = createLoginLabel();
		passwordLabel = createPasswordLabel();

		loginField = createLoginField();
		passwordField = createPasswordField();

		registerButton = createRegisterButton();
	}

	public JLabel createLoginLabel() {
		JLabel label = new JLabel(LOGIN_LABEL_TEXT);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				loginField.requestFocus();
			}
		});
		add(label, LOGIN_FIELD_CONSTRAINTS);
		return label;
	}

	public JLabel createPasswordLabel() {
		JLabel label = new JLabel(PASSWORD_LABEL_TEXT);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				passwordField.requestFocus();
			}
		});
		add(label, PASSWORD_FIELD_CONSTRAINTS);
		return label;
	}

	public JTextField createLoginField() {
		JTextField field = new JTextField(LOGIN_FIELD_TEXT);
		add(field, LOGIN_FIELD_CONSTRAINTS);
		return field;
	}

	public JPasswordField createPasswordField() {
		JPasswordField field = new JPasswordField();
		add(field, PASSWORD_FIELD_CONSTRAINTS);
		return field;
	}

	public JButton createRegisterButton() {
		JButton button = new JButton(REGISTER_BUTTON_TEXT);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String login = loginField.getText();
				String password = passwordField.getPassword().toString();
				controller.registration(login, password);
			}
		});
		add(button, REGISTER_BUTTON_CONSTRAINTS);
		return button;
	}

	@Override
	public void setController(UIController controller) {
		this.controller = controller;
	}

	@Override
	public UIController getController() {
		return controller;
	}
}
