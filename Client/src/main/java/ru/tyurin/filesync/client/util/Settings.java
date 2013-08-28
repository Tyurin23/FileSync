package ru.tyurin.filesync.client.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {

	private static final String SETTINGS_FILE_NAME = "filesync.prop";

	private static final String DEFAULT_PROGRAM_PATH = ".";

	private final String SYNC_FOLDER_NAME = "FileSync";

	private final Properties properties;

	private final String PROGRAM_PATH_KEY = "program_path";

	private final String LOGIN_KEY = "login";

	private final String PASSWORD_KEY = "password";

	private final String SAVE_PASSWORD_KEY = "save_password";

	private final String SYNC_DIRECTORY_KEY = "sync.directory";

	private final String IS_CONFIGURED_KEY = "is_conf";


	/**
	 * Disable user interface
	 */
	private boolean disableUI;

	public static Settings getDefaultSettings() {
		return new Settings(new Properties());
	}

	public static Settings loadSettings(File programPath) throws IOException {
		File settingsFile = getSettingsFilePath(programPath);
		Properties prop = new Properties();
		prop.load(new FileInputStream(settingsFile));
		return new Settings(prop);
	}

	public static boolean isSettingsExist(File programPath) {
		if (getSettingsFilePath(programPath).exists()) {
			return true;
		} else {
			return false;
		}
	}

	public static File getSettingsFilePath(File programPath) {
		if (programPath == null) {
			programPath = new File(DEFAULT_PROGRAM_PATH);
		}
		return new File(programPath.toURI().resolve(SETTINGS_FILE_NAME));
	}

	public Settings(Properties prop) {
		if (prop == null) {
			throw new NullPointerException();
		}
		properties = prop;
	}

	public void saveSettings() throws IOException {
		File settingsFile = getSettingsFilePath(new File(DEFAULT_PROGRAM_PATH));
		properties.store(new FileOutputStream(settingsFile), "");
	}

	public String getSyncDirectory() {
		return properties.getProperty(SYNC_DIRECTORY_KEY, ".");//todo
	}

	public String getProgramPath() {
		String path = properties.getProperty(PROGRAM_PATH_KEY);
		if (path == null) {
			properties.setProperty(PROGRAM_PATH_KEY, DEFAULT_PROGRAM_PATH);
		}
		return properties.getProperty(PROGRAM_PATH_KEY);
	}

	public boolean isDisableUI() {
		return disableUI;
	}

	public boolean isSavePassword() {
		return Boolean.getBoolean(properties.getProperty(SAVE_PASSWORD_KEY, "false"));
	}

	public String getLogin() {
		return properties.getProperty(LOGIN_KEY);
	}

	public String getPassword() {
		return properties.getProperty(PASSWORD_KEY);
	}

	public boolean isConfigured() {
		String isConf = properties.getProperty(IS_CONFIGURED_KEY);
		if (isConf == null) {
			return false;
		}
		return Boolean.parseBoolean(isConf);
	}

	public void setConfigured(boolean configured) {
		properties.setProperty(IS_CONFIGURED_KEY, String.valueOf(configured));
	}

	public void setDisableUI(boolean disableUI) {
		this.disableUI = disableUI;
	}

	public void setSyncDirectory(String syncDirectory) {
		properties.setProperty(SYNC_DIRECTORY_KEY, syncDirectory);
	}

	public void setLogin(String login) {
		properties.setProperty(LOGIN_KEY, login);
	}

	public void setPassword(String password) {
		properties.setProperty(PASSWORD_KEY, password);
	}


}
