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

	final String DB_NAME = "db1";
	final String USER_NAME = "test";
	final String PASSWORD = "test";

	final String clientSyncDirectory = "/home/tyurin/tmp/FS/client";
	final String serverStorageDirectory = "/home/tyurin/tmp/FS/server/storage";
	final String serverProgramDirectory = "/home/tyurin/tmp/FS/server/FSServer";

	State state;

	Config cfg;

	Settings settings;

	FileSyncServer server;

	FileSyncClient client;

	Connection connection;


	@BeforeClass
	public void setUp() throws Exception {
		connection = DriverManager.getConnection(String.format("jdbc:h2:mem:%s", DB_NAME), "sa", "sa");

		cfg = Config.getDefaultConfig();
		cfg.setStorageDirectory(serverStorageDirectory);
		cfg.setDbType("h2");
		cfg.setDbName("mem:" + DB_NAME);
		cfg.setDbUser("sa");
		cfg.setDbPassword("sa");

		settings = Settings.getDefaultSettings();
		settings.setDisableUI(true);
		settings.setLogin(USER_NAME);
		settings.setPassword(PASSWORD);
		settings.setSyncDirectory(clientSyncDirectory);

	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
		FileUtils.cleanDirectory(new File(clientSyncDirectory));
		FileUtils.cleanDirectory(new File(serverStorageDirectory));
		Files.deleteIfExists(Paths.get(settings.getProgramPath() + FSStorage.FILENAME));

		server = new FileSyncServer(cfg);
		server.start();
		Thread.sleep(100);

		createUser(USER_NAME, PASSWORD);


		client = new FileSyncClient(settings);
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
		client.interrupt();
		server.interrupt();
		EntityProvider.close();

		connection.createStatement().execute("DROP ALL OBJECTS");

//		FileUtils.cleanDirectory(new File(clientSyncDirectory));
//		FileUtils.cleanDirectory(new File(serverStorageDirectory));
		Files.deleteIfExists(Paths.get(settings.getProgramPath() + FSStorage.FILENAME));
	}

	@AfterClass
	public void tearDown() throws Exception {
		connection.close();
	}


	@Test(
			timeOut = 10000L
	)
	public void testCreate() throws Exception {

		client.start();
		Thread.sleep(1000);

		state = new FileCreator.Builder(new File(settings.getSyncDirectory())).maxDepth(0).maxFiles(3).maxFileSize(100).dirProbability(0.0).build().createTree();


		System.out.println("Files created - " + state.getExistingFiles().size());
		Thread.sleep(5000);

		State serverState = new State(new File(serverStorageDirectory));
		long cliHash = state.getHash();
		long srvHash = serverState.getHash();
		System.out.println(srvHash + " = " + cliHash);
		assertEquals(cliHash, srvHash);
	}

	@Test(
			dependsOnMethods = "testCreate",
			enabled = false

	)
	public void testModify() throws Exception {


	}

	private boolean createUser(String login, String password) throws SQLException {
		PreparedStatement ps = connection.prepareStatement("insert into user values (default, ?, ?)");
		ps.setString(1, login);
		ps.setString(2, password);
		int res = ps.executeUpdate();
		return (res > 0);
	}
}
