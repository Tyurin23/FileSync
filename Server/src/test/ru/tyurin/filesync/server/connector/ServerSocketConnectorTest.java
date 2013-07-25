package ru.tyurin.filesync.server.connector;

import org.junit.Test;

import java.io.IOException;

public class ServerSocketConnectorTest {

	final int port = 4444;

	ServerSocketConnector connector;

	@org.junit.Before
	public void setUp() throws Exception {
		connector = new ServerSocketConnector(port);
	}

	@org.junit.After
	public void tearDown() throws Exception {

	}

	@Test
	public void testConnector() throws IOException {
		connector.read();
	}
}
