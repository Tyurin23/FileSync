package ru.tyurin.filesync.client.connector;


import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.tyurin.filesync.shared.FileTransferPart;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;


public class ClientSocketConnectorTest extends ClientSocketConnector {

	final String host = "localhost";
	final int port = 4444;

	ClientSocketConnector connector;
	ObjectInputStream input;
	ObjectOutputStream output;

	Thread server;

	@PrepareForTest(ObjectInputStream.class)
	@BeforeClass
	public void setUp() throws Exception {
//		input = mock(ObjectInputStream.class);
		input = PowerMockito.mock(ObjectInputStream.class);
		output = mock(ObjectOutputStream.class);
//		when(input.readObject()).thenReturn(new FileTransferPart());
		PowerMockito.when(input, "readObject").thenReturn(new FileTransferPart());
		server = new Thread() {

			ServerSocket socket = new ServerSocket(port);

			@Override
			public void run() {
				while (true) {
					while (!this.isInterrupted()) {
						try {
							Socket sock = socket.accept();
							ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
							out.flush();

						} catch (IOException e) {
							e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
						}
					}
				}
			}
		};

		server.start();
	}

	@AfterClass
	public void tearDown() throws Exception {
		server.interrupt();
		if (!connector.isClosed()) {
			connector.close();
		}
	}

	@Test
	public void testConnector() throws IOException {
//		connector = new Connector("8.8.8.8");
	}

	@Test
	public void testSendData() throws Exception {
		final String name = "TEST";
		FileTransferPart sentObject = new FileTransferPart(name);
		connector = getMockedConnector();
		connector.sendObject(sentObject);
		verify(output).writeObject(sentObject);
	}

	@Test(
			enabled = false
	)
	public void testGetData() throws Exception {
		final String name = "TEST";
		FileTransferPart sentObject = new FileTransferPart(name);
		connector = getMockedConnector();
		when(connector.input.readObject()).thenReturn(sentObject);
		Object receivedObject = connector.getObject();
		verify(connector.input).readObject();
		assertEquals(sentObject, receivedObject);
	}

	@Test(
			enabled = false
	)
	public void testClose() throws IOException {
		connector = new ClientSocketConnector(host, port);
		connector.close();
		if (!connector.isClosed()) {
			fail();
		}
	}


	@Test(
			enabled = false
	)
	public void testConstructor() throws IOException {
		connector = new ClientSocketConnector(host, port);
		if (connector.isClosed()) {
			fail();
		}
	}


	@Test(dataProvider = "badConstructorData",
			expectedExceptions = {ConnectException.class},
			enabled = false
	)
	public void testBadConstructor(String host, int port) throws IOException {
		connector = new ClientSocketConnector(host, port);
	}

	public ClientSocketConnector getMockedConnector() throws IOException {
		connector = new ClientSocketConnector(host, port);
		connector.output = output;
		connector.input = input;
		return connector;
	}

	@DataProvider
	public Object[][] badConstructorData() {
		return new Object[][]{
				{"", 0},
				{"169.254.0.1", 10},
				{"localhost", 100}
		};
	}


}
