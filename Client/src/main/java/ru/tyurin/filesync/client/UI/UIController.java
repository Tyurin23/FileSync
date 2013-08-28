package ru.tyurin.filesync.client.UI;

import ru.tyurin.filesync.client.connector.ConnectionManager;
import ru.tyurin.filesync.client.util.Settings;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User: tyurin
 * Date: 8/19/13
 * Time: 8:50 AM
 */
public class UIController {


	private UIModel model;
	private UIView view;

	public UIController(UIModel model, UIView view) {
		this.model = model;
		this.view = view;
		this.view.setController(this);
	}

	public void showLoginPane() {
		view.getConfigurationFrame().showLoginPane();
	}

	public void showSettingsPane() {
		view.getConfigurationFrame().showSettingsPane();
	}

	public void createAndSaveSettingsFile(File syncDir) throws IOException {
		Settings settings = Settings.getDefaultSettings();
		if (syncDir.exists() && syncDir.isDirectory() && syncDir.canWrite()) {
			settings.setSyncDirectory(syncDir.toString());
			settings.setConfigured(true);
		}
		settings.saveSettings();
		exit();
	}

	//todo
	public boolean authorization(String login, String password) {
		boolean isAuth = false;
		try {
			ConnectionManager manager = ConnectionManager.createSSLInstance();
			manager.setLogin(login);
			manager.setPass(password);
			isAuth = manager.testConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isAuth;
	}

	//todo
	public void registration(String login, String password) {
		try {
			ConnectionManager manager = ConnectionManager.createSSLInstance();
			boolean registered = manager.registration(login, password);
			if (registered) {
				view.getConfigurationFrame().showLoginPane();
			} else {
				System.out.println("Error register");
			}
		} catch (Exception e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public String getPasswordHash(String password) {
		if (password == null) {
			throw new NullPointerException("Password cannot be null");
		}
		String hash = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes("UTF8"));
			byte[] result = md.digest();
			hash = String.valueOf(result);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
		}
		return hash;
	}


	public void exit() {
		view.getConfigurationFrame().dispose();
	}

	public UIModel getModel() {
		return model;
	}
}
