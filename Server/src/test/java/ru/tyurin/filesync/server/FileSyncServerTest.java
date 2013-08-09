package ru.tyurin.filesync.server;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.*;
import ru.tyurin.filecreator.FileCreator;
import ru.tyurin.filecreator.State;
import ru.tyurin.filesync.client.FileSyncClient;
import ru.tyurin.filesync.client.fs.FSStorage;
import ru.tyurin.filesync.client.util.Settings;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * User: tyurin
 * Date: 8/6/13
 * Time: 10:21 AM
 */
public class FileSyncServerTest {

	private final Object monitor = new Object();

	final String clientSyncDirectory = "/home/tyurin/tmp/FS/client";
	final String serverStorageDirectory = "/home/tyurin/tmp/FS/server/storage";
	final String serverProgramDirectory = "/home/tyurin/tmp/FS/server/FSServer";

	State state;

	Config cfg;

	Settings settings;

	FileSyncServer server;

	FileSyncClient client;


	@BeforeClass
	public void setUp() throws Exception {
		cfg = Config.getDefaultConfig();
		cfg.setStorageDirectory(serverStorageDirectory);

		settings = Settings.getDefaultSettings();
		settings.setDisableUI(true);
		settings.setSyncDirectory(clientSyncDirectory);

	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
		FileUtils.cleanDirectory(new File(clientSyncDirectory));
		FileUtils.cleanDirectory(new File(serverStorageDirectory));

		server = new FileSyncServer(cfg);
		client = new FileSyncClient(settings);
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
		client.interrupt();
		server.interrupt();

//		FileUtils.cleanDirectory(new File(clientSyncDirectory));
//		FileUtils.cleanDirectory(new File(serverStorageDirectory));
		Files.deleteIfExists(Paths.get(settings.getProgramPath() + FSStorage.FILENAME));
	}

	@AfterClass
	public void tearDown() throws Exception {

	}

	@Test
	public void testFSServer() throws Exception {


		server.start();
		Thread.sleep(1000);
		client.start();
		Thread.sleep(1000);

//		state = FileCreator.createTree(new File(settings.getSyncDirectory()), 0, 1);
		state = new FileCreator.Builder(new File(settings.getSyncDirectory())).maxDepth(0).maxFiles(1).maxFileSize(5000000).dirProbability(0.0).build().createTree();


		System.out.println("Files created - " + state.getExistingFiles().size());
		Thread.sleep(3000);

		long time = System.currentTimeMillis();
		final long timeout = 10000;
		while (client.status != 0) {
			System.out.println(client.status);
			if (System.currentTimeMillis() - time > timeout) {
				fail("timeout");
			}
		}
		State serverState = new State(new File(serverStorageDirectory));
		long cliHash = state.getHash();
		long srvHash = serverState.getHash();
		System.out.println(srvHash + " = " + cliHash);
//		assertTrue(state.getExistingFiles().equals(serverState.getExistingFiles()));
//		synchronized (monitor) {
//			monitor.wait();
//		}
		assertEquals(cliHash, srvHash);

	}
}
