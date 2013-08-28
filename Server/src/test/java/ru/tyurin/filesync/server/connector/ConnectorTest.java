package ru.tyurin.filesync.server.connector;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.tyurin.filesync.client.connector.ClientSocketConnector;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;


public class ConnectorTest {

	final String HOST = "localhost";
	final int PORT = 4444;
	ClientSocketConnector clientConnector;
	Connector serverConnector;

	Server server;

	class Server extends Thread {

		Connector serverConnector;
		SSLServerSocket serverSocket;

		Server() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
			System.setProperty("javax.net.ssl.keyStore", "/home/tyurin/code/FileSync/Server/src/main/resources/sslKeyStore.jks");
			System.setProperty("javax.net.ssl.keyStorePassword", "password");
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream(System.getProperty("javax.net.ssl.keyStore")), System.getProperty("javax.net.ssl.keyStorePassword").toCharArray());
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keyStore, "password".toCharArray());
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), new TrustManager[]{}, null);
			SSLServerSocketFactory factory = sslContext.getServerSocketFactory();
//			keyStore.load(new FileInputStream("/home/tyurin/code/FileSync/Server/src/main/resources/SSLCert"), "123456".toCharArray());

//			SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			serverSocket = (SSLServerSocket) factory.createServerSocket(PORT);
		}

		@Override
		public void run() {
			while (!interrupted()) {
				try {
					serverConnector = new ServerSocketConnector(serverSocket.accept());
					while (!serverConnector.isClose()) {
						Object o = serverConnector.getObject();
						serverConnector.sendObject(o);
					}
				} catch (IOException e) {
					e.printStackTrace();
					try {
						serverConnector.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	@BeforeMethod
	public void setUp() throws Exception {
		server = new Server();
		server.start();
	}

	@AfterMethod
	public void tearDown() throws Exception {
		server.interrupt();
	}


	@Test
	public void testConnector() throws Exception {
//		clientConnector = new ClientSSLConnector(HOST, PORT);
//		String s = "TEST";
//		clientConnector.sendObject(s);
//		Object obj = clientConnector.receiveObject();
//		Assert.assertTrue(obj.equals(s));

	}
}
