package ru.tyurin.filesync.server;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.*;
import ru.tyurin.filecreator.FileCreator;
import ru.tyurin.filecreator.State;
import ru.tyurin.filesync.client.FileSyncClient;
import ru.tyurin.filesync.client.fs.FSStorage;
import ru.tyurin.filesync.client.util.Settings;
import ru.tyurin.filesync.server.db.EntityProvider;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

/**
 * User: tyurin
 * Date: 8/6/13
 * Time: 10:21 AM
 */
public class FileSyncServerTest {

	static final String DB_NAME = "db1";
	final String USER_NAME = "test";
	final String PASSWORD = "test";

	final String clientSyncDirectory = "/home/tyurin/tmp/FS/client";
	final String serverStorageDirectory = "/home/tyurin/tmp/FS/server/storage";
	final String OTHER_CLIENT_SYNC_PATH = "/home/tyurin/tmp/FS/otherClient";
	final String serverProgramDirectory = "/home/tyurin/tmp/FS/server/FSServer";

	Config cfg;

	Settings settings;

	FileSyncServer server;

	FileSyncClient client;



	static Connection connection;


	@BeforeClass
	public static void setUp() throws Exception {

	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
		connection = DriverManager.getConnection(String.format("jdbc:h2:mem"), "sa", "sa");
		connection.createStatement().execute("DROP ALL OBJECTS");

		cfg = Config.getDefaultConfig();
		cfg.setStorageDirectory(serverStorageDirectory);
		cfg.setDbType("h2");
		cfg.setDbName("mem");
		cfg.setDbUser("sa");
		cfg.setDbPassword("sa");

		settings = Settings.getDefaultSettings();
		settings.setDisableUI(true);
		settings.setLogin(USER_NAME);
		settings.setPassword(PASSWORD);
		settings.setSyncDirectory(clientSyncDirectory);

		FileUtils.cleanDirectory(new File(clientSyncDirectory));
		FileUtils.cleanDirectory(new File(serverStorageDirectory));
		FileUtils.cleanDirectory(new File(OTHER_CLIENT_SYNC_PATH));
		Files.deleteIfExists(Paths.get(settings.getProgramPath() + FSStorage.FILENAME));

		server = new FileSyncServer(cfg);

		createUser(USER_NAME, PASSWORD);


		client = new FileSyncClient(settings);
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
		client.stopClient();
		Thread.sleep(1000);

		server.stop();

		connection.close();

	}

	@AfterClass
	public static void tearDown() throws Exception {
		connection.close();
		EntityProvider.close();
	}


	@Test(
			enabled = false
	)
	public void testConnectingTwoClients() throws Exception {
		client.start();

		Settings s = Settings.getDefaultSettings();
		s.setDisableUI(true);
		s.setLogin(USER_NAME);
		s.setPassword(PASSWORD);
		s.setSyncDirectory(OTHER_CLIENT_SYNC_PATH);
//		FileSyncClient otherClient = new FileSyncClient(s);
//		otherClient.start();
//		Thread.sleep(15000);\

		synchronized (this){
			this.wait();
		}
	}


	@Test(
			timeOut = 10000L,
			enabled = true
	)
	public void testCreate() throws Exception {

		client.start();
		State state = new FileCreator.Builder(new File(settings.getSyncDirectory())).maxDepth(0).maxFiles(3).maxFileSize(100).dirProbability(0.0).build().createTree();


		System.out.println("Files created - " + state.getExistingFiles().size());
		Thread.sleep(5000);

		State serverState = new State(new File(serverStorageDirectory));
		long cliHash = state.getHash();
		long srvHash = serverState.getHash();
		System.out.println(srvHash + " = " + cliHash);
		assertEquals(cliHash, srvHash);
	}

	@Test(
//			dependsOnMethods = "testCreate",
			enabled = true

	)
	public void testModify() throws Exception {
		client.start();
		State state = new FileCreator.Builder(new File(settings.getSyncDirectory())).maxDepth(0).maxFiles(2).maxFileSize(100).dirProbability(0.0).build().createTree();
		System.out.println("Files created - " + state.getExistingFiles().size());
		Thread.sleep(10000);
//		client.interrupt();

		State serverState = new State(new File(serverStorageDirectory));
		long clHash = state.getHash();
		long srvHash = serverState.getHash();
		System.out.println(srvHash + " = " + clHash);
		assertEquals(clHash, srvHash);


		Settings s = Settings.getDefaultSettings();
		s.setDisableUI(true);
		s.setLogin(USER_NAME);
		s.setPassword(PASSWORD);
		s.setSyncDirectory(OTHER_CLIENT_SYNC_PATH);
		FileSyncClient otherClient = new FileSyncClient(s);

		otherClient.start();
		Thread.sleep(15000);
//
//		State otherClientState = new State(new File(OTHER_CLIENT_SYNC_PATH));
//		long cliHash = state.getHash();
//		long oCliHash = otherClientState.getHash();
//		System.out.println(oCliHash + " = " + cliHash);
//		assertEquals(cliHash, oCliHash);

	}

	private boolean createUser(String login, String password) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("insert into user values (default, ?, ?)");
		ps.setString(1, login);
		ps.setString(2, password);
		int res = ps.executeUpdate();
		return (res > 0);
	}
}
