package ru.tyurin.filesync.client.connector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.tyurin.filesync.shared.FileTransferPart;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 25.07.13
 * Time: 18:52
 * To change this template use File | Settings | File Templates.
 */
public class ClientSocketConnectorTest {

	final String host = "localhost";
	final int port = 4444;

	ClientSocketConnector connector;

	@Before
	public void setUp() throws Exception {
		connector = new ClientSocketConnector(host, port);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testConnector() throws IOException {
		FileTransferPart part = new FileTransferPart("Hello");
		connector.sendObject(part);
		FileTransferPart p2 = new FileTransferPart("World!");
		connector.sendObject(p2);
		connector.close();
	}


}
