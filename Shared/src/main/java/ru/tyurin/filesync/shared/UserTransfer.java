package ru.tyurin.filesync.shared;

import java.io.Serializable;

/**
 * User: tyurin
 * Date: 8/5/13
 * Time: 3:04 PM
 */
public class UserTransfer implements Serializable {

	private String login;

	private String password;

	public UserTransfer(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}
}
