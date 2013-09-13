package ru.tyurin.filesync.client.fs;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.*;
import ru.tyurin.filecreator.FileCreator;
import ru.tyurin.filesync.client.util.Settings;
import ru.tyurin.filesync.shared.BlockTransferPart;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static org.testng.Assert.assertNotNull;


public class FSTest {

	public static final String path = "/home/tyurin/tmp/testSync/";
	Path basicDir;


	FSContainer container;

	FSStorage storage;

	FSManager manager;


	@BeforeClass
	public void setUp() throws Exception {
		if (Files.exists(Paths.get(path))) {
			FileUtils.deleteDirectory(Paths.get(path).toFile());
		}
		basicDir = Files.createDirectory(Paths.get(path));
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
		storage = new FSStorage(Settings.getDefaultSettings().getProgramPath());
		container = storage.getContainer();
		manager = new FSManager(basicDir.toString(), container);
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
		Files.deleteIfExists(storage.getStorageFile().toPath());
		FileUtils.cleanDirectory(basicDir.toFile());
	}

	@AfterClass
	public void tearDown() throws Exception {
		FileUtils.deleteDirectory(basicDir.toFile());
	}


	@Test
	public void testSingleFile() throws Exception {
		File file = FileCreator.createSingleFile(basicDir.toFile());
		Thread.sleep(3000);
		FileNode node = container.get(file.toPath());
		assertNotNull(node);
	}

	@Test(
			dependsOnMethods = "testSingleFile"
	)
	public void FSTest() throws Exception {
//		List<File> files = FileCreator.createTree(basicDir.toFile(), 3, 5);
//		Thread.sleep(3000);
//		for (File file : files) {
//			FileNode node = container.get(file.toPath());
//			assertNotNull(node);
//		}
	}

	@Test
	public void testFSWithStorage() throws Exception {


	}

//	public static void main(String[] args) throws Exception {
//		FSTest test = new FSTest();
//		while (true) {
//			test.setUp();
//			test.setUpMethod();
////			Scanner sc = new Scanner(System.in);
//			System.out.println("pre test");
//			test.FSTest();
//			System.out.println("test end");
//			test.tearDownMethod();
//			test.tearDown();
//		}


//	}


}
