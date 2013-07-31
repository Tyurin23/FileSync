package ru.tyurin.filesync.server.connector;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.tyurin.filesync.client.connector.ClientSocketConnector;
import ru.tyurin.filesync.shared.FileTransferPart;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


public class ConnectorTest {

	public static Logger LOG = Logger.getLogger(ConnectorTest.class);


	class ServerWrapper extends Thread {

		BlockingQueue<Object> input = new ArrayBlockingQueue<Object>(100);
		BlockingQueue<Object> output = new ArrayBlockingQueue<Object>(100);

		Connector server;

		ServerSocket serverSocket;


		public ServerWrapper(ServerSocket sevrerSocket) {
			this.serverSocket = sevrerSocket;
		}

		@Override
		public void run() {
			while (!this.isInterrupted()) {
				try {
					server = new ServerSocketConnector(serverSocket.accept());
				} catch (IOException e) {
					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
				try {
					LOG.debug("Server waiting connection...");
					while (!server.isClose()) {
						if (output.size() > 0) {
							Object writeObj = output.poll();
							server.sendObject(writeObj);
							LOG.debug("Send object " + writeObj);
						}
						Object obj = null;
						try {
							obj = server.getObject();
						} catch (IOException e) {
							server.close();
						}

						if (obj != null) {
							input.add(obj);
							LOG.debug("Get object " + obj);
						}

					}
				} catch (IOException e) {
					e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
				LOG.debug("Server stopped");
			}

		}
	}

	final String host = "localhost";
	final int port = 4444;

	ServerSocket serverSocket;

	ru.tyurin.filesync.client.connector.Connector client;
	Connector server;

	ServerWrapper srv;

	@BeforeClass
	public void setUp() throws Exception {
		serverSocket = new ServerSocket(port);
		srv = new ServerWrapper(serverSocket);
		srv.start();
	}

	@AfterClass
	public void tearDown() throws Exception {
		srv.interrupt();
	}

	@Test(
			dataProvider = "getData"
	)
	public void testConnection(FileTransferPart part) throws Exception {
		client = new ClientSocketConnector(host, port);
		client.sendObject(part);
		Thread.sleep(1);
		FileTransferPart rcv = (FileTransferPart) srv.input.poll();
		try {
			Assert.assertNotNull(rcv);
			Assert.assertEquals(part.getName(), rcv.getName());
		} finally {
			client.close();
		}

	}

	@Test(
			dataProvider = "getDataArray"
	)
	public void testManySend(FileTransferPart[] parts) throws Exception {
		client = new ClientSocketConnector(host, port);
		try {
			for (int i = 0; i < parts.length; i++) {
				client.sendObject(parts[i]);
				Thread.sleep(1);
				FileTransferPart rcv = (FileTransferPart) srv.input.poll();
				Assert.assertNotNull(rcv);
				Assert.assertEquals(parts[i].getName(), rcv.getName());
			}
		} finally {
			client.close();
		}
	}

	@DataProvider
	public Object[][] getData() {
		return new Object[][]{
				new Object[]{new FileTransferPart("TEST")},
				new Object[]{new FileTransferPart("VASYA")},
				new Object[]{new FileTransferPart("PETYA")}
		};
	}

	@DataProvider
	public Object[][] getDataArray() {
		return new Object[][]{
				new Object[]{
						new FileTransferPart[]{
								new FileTransferPart("TEST"),
								new FileTransferPart("PETYA"),
								new FileTransferPart("Vasya")
						}
				}
		};
	}


}
