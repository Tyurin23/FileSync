package ru.tyurin.filesync.server.connector;

import org.junit.Ignore;
import org.junit.Test;
import ru.tyurin.filesync.shared.FileTransferPart;

import java.io.IOException;

public class ServerSocketConnectorTest {

	final int port = 4444;

	ServerSocketConnector connector;

	@org.junit.Before
	public void setUp() throws Exception {
//		connector = new ServerSocketConnector(port);
	}

	@org.junit.After
	public void tearDown() throws Exception {

	}

	@Ignore
	@Test()
	public void testConnector() throws IOException, ClassNotFoundException {
//		connector.getConnection();
		FileTransferPart part = (FileTransferPart) connector.getObject();
		System.out.println(part.getName());
		part = (FileTransferPart) connector.getObject();
		System.out.println(part.getName());
		connector.close();
	}
}
