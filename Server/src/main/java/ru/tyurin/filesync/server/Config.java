package ru.tyurin.filesync.server;

/**
 * User: tyurin
 * Date: 8/6/13
 * Time: 8:14 AM
 */
public class Config {

	protected String host = "localhost";
	protected int port = 4444;
	protected boolean enableSSL = true;
	protected String keyStore = "/home/tyurin/code/FileSync/Server/src/main/resources/sslKeyStore.jks"; //todo
	protected String keyStorePassword = "password";
	protected String storageDirectory = "/home/tyurin/tmp/FSServer/";


	public static Config getDefaultConfig() {
		return new Config();
	}

	protected Config() {
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public boolean isEnableSSL() {
		return enableSSL;
	}

	public String getKeyStore() {
		return keyStore;
	}

	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	public String getStorageDirectory() {
		return storageDirectory;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setEnableSSL(boolean enableSSL) {
		this.enableSSL = enableSSL;
	}

	public void setKeyStore(String keyStore) {
		this.keyStore = keyStore;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	public void setStorageDirectory(String storageDirectory) {
		this.storageDirectory = storageDirectory;
	}
}
