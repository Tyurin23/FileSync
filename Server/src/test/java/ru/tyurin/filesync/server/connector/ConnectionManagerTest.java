package ru.tyurin.filesync.server.connector;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * User: tyurin
 * Date: 8/5/13
 * Time: 3:39 PM
 */
public class ConnectionManagerTest {

	ConnectionManager serverManager;

	ru.tyurin.filesync.client.connector.ConnectionManager clientManager;

	@BeforeClass
	public void setUp() throws Exception {
		System.setProperty("javax.net.ssl.keyStore", "/home/tyurin/code/FileSync/Server/src/main/resources/sslKeyStore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "password");
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
		serverManager = ConnectionManager.getSSLInstance();
		serverManager.start();

	}

	@AfterMethod
	public void tearDown() throws Exception {
		serverManager.interrupt();

	}

	@Test
	public void testManagers() throws Exception {
		clientManager = ru.tyurin.filesync.client.connector.ConnectionManager.createSSLInstance();
//		List<FileNode> nodes = clientManager.getFileNodes();
//		assertNotNull(nodes);
	}
}
