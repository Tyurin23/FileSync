package ru.tyurin.filesync.server.db;

/**
 * User: tyurin
 * Date: 8/6/13
 * Time: 9:41 AM
 */
public class UserProvider {

	public static final int AUTH_FAIL = -1;


	public static int authentication(String login, String password) {
		if (login == null || password == null) {
			return AUTH_FAIL;
		}
		if (login.equals("test") && password.equals("test")) {
			return 1;
		}
		return AUTH_FAIL;
	}
}
