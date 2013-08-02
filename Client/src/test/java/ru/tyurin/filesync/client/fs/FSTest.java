package ru.tyurin.filesync.client.fs;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.*;
import ru.tyurin.filesync.shared.FileTransferPart;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static org.testng.Assert.assertNotNull;


public class FSTest {

	public static final String path = "/home/tyurin/tmp/testSync/";
	Path basicDir;

	Queue<FileTransferPart> input;

	FSContainer container;

	FSManager manager;


	@BeforeClass
	public void setUp() throws Exception {
		if (Files.exists(Paths.get(path))) {
			FileUtils.deleteDirectory(Paths.get(path).toFile());
		}
		basicDir = Files.createDirectory(Paths.get(path));
		container = new FSContainer();
		input = new ArrayBlockingQueue<FileTransferPart>(100);
	}

	@BeforeMethod
	public void setUpMethod() throws Exception {
		manager = new FSManager(basicDir, container, input);
		manager.start();
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
		manager.interrupt();
	}

	@AfterClass
	public void tearDown() throws Exception {
		FileUtils.cleanDirectory(basicDir.toFile());
	}

	@Test
	public void FSTest() throws Exception {
		List<File> files = FileCreator.createTree(basicDir.toFile(), 3, 5);
		Thread.sleep(3000);
		for (File file : files) {
			FileNode node = container.get(file.toPath());
			assertNotNull(node);
		}
	}


	public static void main(String[] args) throws Exception {
		FSTest test = new FSTest();
		while (true) {
			test.setUp();
			test.setUpMethod();
//			Scanner sc = new Scanner(System.in);
			System.out.println("pre test");
			test.FSTest();
			System.out.println("test end");
			test.tearDownMethod();
			test.tearDown();
		}


	}


}
