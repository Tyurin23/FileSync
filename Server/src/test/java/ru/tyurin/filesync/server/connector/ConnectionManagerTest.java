package ru.tyurin.filesync.server.connector;


import org.apache.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.tyurin.filesync.server.connector.facory.ConnectorFactory;
import ru.tyurin.filesync.server.connector.facory.SessionFactory;
import ru.tyurin.filesync.shared.FileTransferPart;

import java.io.IOException;
import java.util.Queue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectionManagerTest extends ConnectionManager {

	public static Logger LOG = Logger.getLogger(ConnectionManager.class);

	ConnectionManager manager;
	ConnectorFactory connectorFactory;
	SessionFactory sessionFactory;

	public ConnectionManagerTest() throws Exception {
	}


	@BeforeClass
	public void setUp() throws Exception {
		connectorFactory = new ConnectorFactory() {
			//			@Override
			public Connector createConnector() throws IOException {
				Connector connector = mock(Connector.class);
				FileTransferPart part = new FileTransferPart("TEST");
				when(connector.getObject()).thenReturn(part);
				return connector;
			}
		};

		sessionFactory = new SessionFactory() {
			@Override
			public Session createSession(Queue queue) {
				Session session = mock(Session.class);

				return session;

			}
		};

//		manager = new ConnectionManager(connectorFactory, sessionFactory);

	}

	@AfterClass
	public void tearDown() {

	}

	@Test(
			enabled = false
	)
	public void connectionManagerTest() throws IOException {
//		manager.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		LOG.info(manager.connectionPool.getNumIdle());
	}

}
