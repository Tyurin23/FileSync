package ru.tyurin.filesync.server;

import org.testng.annotations.*;

/**
 * User: tyurin
 * Date: 8/6/13
 * Time: 10:21 AM
 */
public class FileSyncServerTest {

	private final Object monitor = new Object();

	Config cfg;

	FileSyncServer server;


	@BeforeClass
	public void setUp() throws Exception {
		cfg = Config.getDefaultConfig();

		server = new FileSyncServer(cfg);
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {

	}

	@AfterMethod
	public void tearDownMethod() throws Exception {

	}

	@AfterClass
	public void tearDown() throws Exception {

	}

	@Test
	public void testFSServer() throws Exception {
		server.start();
		synchronized (monitor) {
			monitor.wait();
		}

	}
}
