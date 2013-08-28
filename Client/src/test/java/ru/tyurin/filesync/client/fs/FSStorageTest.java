package ru.tyurin.filesync.client.fs;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import ru.tyurin.filesync.client.util.Settings;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FSStorageTest extends FSStorage {

	final String notWritableDir = "/";

	FSStorage storage;

	Path basicPath = Paths.get(Settings.getDefaultSettings().getProgramPath());
	Path storagePath = null;

	@BeforeMethod
	public void setUp() throws Exception {

	}

	@AfterMethod
	public void tearDown() throws Exception {
		if (storagePath != null && Files.exists(storagePath)) {
			Files.delete(storagePath.toAbsolutePath());
		}
	}

	@Test(dataProvider = "fileNodeList")
	public void testSaveLoad(List<FileNode> nodes) throws Exception {
		storage = new FSStorage(basicPath.toString());
		storagePath = storage.getStorageFile().toPath();
//		storage.save(nodes);
//		List<FileNode> loaded = storage.load();
//		assertArrayEquals(nodes.toArray(), loaded.toArray());
	}

	@Test(
			dataProvider = "writablePaths"
	)
	public void testConstructor(Path storagePath) throws Exception {
		storage = new FSStorage(storagePath.toString());
		this.storagePath = storage.getStorageFile().toPath();
		Assert.assertTrue(Files.exists(this.storagePath));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testConstructorExceptions() throws Exception {
		final Path notWritable = Paths.get(notWritableDir);
		storage = new FSStorage(notWritable.toString());
	}

	@DataProvider(name = "writablePaths")
	public Object[][] constructorData() {
		return new Object[][]{
				new Object[]{Settings.getDefaultSettings().getProgramPath()},
				new Object[]{Paths.get(System.getProperty("user.home"))}
		};
	}

	@DataProvider(name = "fileNodeList")
	public Object[][] getFileNodesList() {
		return new Object[][]{
				new Object[]{
						Arrays.asList(new FileNode[]{
								new FileNode(Paths.get("path"), 10, 10, null),
								new FileNode(Paths.get("p"), 100, 100, new ArrayList<FileBlock>())
						})
				}
		};
	}


}
