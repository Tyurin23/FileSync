package ru.tyurin.filesync.client.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * User: tyurin
 * Date: 8/22/13
 * Time: 9:14 AM
 */
public class LoginPanel extends JPanel implements Controlled {

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


	private final GridBagConstraints FORGOT_PASSWORD_LABEL_CONSTRAINTS = new GridBagConstraints(
			1, 2,
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
	private final GridBagConstraints LOGIN_BUTTON_CONSTRAINTS = new GridBagConstraints(
			1, 3,
			1, 1,
			0, 0,
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
			new Insets(10, 10, 0, 0),
			0, 0
	);

	private final String LOGIN_LABEL_TEXT = "Login:";
	private final String LOGIN_FIELD_TEXT = "";
	private final String PASSWORD_LABEL_TEXT = "Password:";
	private final String PASSWORD_FIELD_TEXT = "";
	private final String LOGIN_BUTTON_TEXT = "Login!";
	private final String REGISTER_BUTTON_TEXT = "Register";
	private final String FORGOT_PASSWORD_LABEL_TEXT = "Forgot your password?";


	private JLabel loginLabel;
	private JLabel passwordLabel;
	private JTextField loginField;
	private JPasswordField passwordField;
	private JButton loginButton;
	private JButton registerButton;
	private JLabel forgotPasswordLabel;


	public LoginPanel() {
		super(new GridBagLayout());


		loginLabel = createLoginLabel();

		passwordLabel = new JLabel(PASSWORD_LABEL_TEXT);
		forgotPasswordLabel = new JLabel(FORGOT_PASSWORD_LABEL_TEXT);

		loginField = new JTextField(LOGIN_FIELD_TEXT);
		passwordField = new JPasswordField(PASSWORD_FIELD_TEXT);

		loginButton = new JButton(LOGIN_BUTTON_TEXT);
		registerButton = new JButton(REGISTER_BUTTON_TEXT);


		add(loginField, LOGIN_FIELD_CONSTRAINTS);
		add(passwordLabel, PASSWORD_LABEL_CONSTRAINTS);
		add(passwordField, PASSWORD_FIELD_CONSTRAINTS);
		add(forgotPasswordLabel, FORGOT_PASSWORD_LABEL_CONSTRAINTS);
		add(registerButton, REGISTER_BUTTON_CONSTRAINTS);
		add(loginButton, LOGIN_BUTTON_CONSTRAINTS);


	}

	private JLabel createLoginLabel() {
		JLabel label = new JLabel(LOGIN_LABEL_TEXT);
		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				loginLabelMouseClick();
			}
		});
		add(label, LOGIN_LABEL_CONSTRAINTS);
		return label;
	}


	@Override
	public void setController(final UIController controller) {
		this.controller = controller;
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String login = loginField.getText();
				String password = passwordField.getPassword().toString();
				controller.showSettingsPane();
//				if(login.isEmpty() || password.isEmpty()){
//					return;
//				}
//				boolean isAuth = controller.authorization(login, password);
//				if(isAuth){
////					controller.
//				}
			}
		});
	}

	@Override
	public UIController getController() {
		return controller;
	}


	private void loginLabelMouseClick() {
		loginField.requestFocus();
	}
}
