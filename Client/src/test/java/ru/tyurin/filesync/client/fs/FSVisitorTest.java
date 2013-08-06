package ru.tyurin.filesync.client.fs;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.*;
import ru.tyurin.filesync.shared.FileNode;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
/*
TODO
file modified
file deleted
folder deleted
not readable
hidden files
links

 */

public class FSVisitorTest {
	final String root = "test";

	FSVisitor visitor;
	FSContainer container;
	Path rootPath;

	FileSystem fs = FileSystems.getDefault();


	@BeforeClass
	public void setUp() throws Exception {
		container = mock(FSContainer.class);
		rootPath = Paths.get(root);
	}

	@BeforeMethod
	public void setUpmethod() throws Exception {
		Files.deleteIfExists(rootPath);
		Files.createDirectory(rootPath);
	}

	@AfterMethod
	public void tearDownMethod() throws Exception {
		reset(container);
		deleteFiles();
		Files.delete(rootPath);
	}

	@AfterClass
	public void tearDown() throws Exception {

	}

	@Test(

	)
	public void testGetChanges() throws Exception {
		visitor = new FSVisitor(rootPath, container);
		List<Path> result = visitor.getChanges();
		assertNotNull(result);
	}

	@Test
	public void testCreateFiles() throws Exception {
		final String file = "test";
		visitor = new FSVisitor(rootPath, container);
		testGetChanges(visitor, 0);

		Path createdFile = createFile(rootPath.resolve(file));
		testGetChanges(visitor, 1, createdFile);

		mockContainer(createdFile);
		createdFile = createFile(rootPath.resolve(file + "1"));
		testGetChanges(visitor, 1, createdFile);

		mockContainer(createdFile);
		Path createdDir = createDirectory(rootPath.resolve("dir"));
		testGetChanges(visitor, 1, createdDir);

		mockContainer(createdDir);
		createdFile = createFile(rootPath.resolve(createdDir).resolve("file"));
		testGetChanges(visitor, 1, createdFile);
	}

	@Test
	public void testModifyFiles() throws Exception {
		final String file = "file";
		final String dir = "dir";
		final String subfile = "sub";
		Path filePath = createFile(rootPath.resolve(Paths.get(file)));
		Path dirPath = createDirectory(rootPath.resolve(Paths.get(dir)));
		Path subfilePath = createFile(rootPath.resolve(dirPath).resolve(subfile));

		visitor = new FSVisitor(rootPath, container);
		testGetChanges(visitor, 3, filePath, dirPath, subfilePath);

		mockContainer(filePath, dirPath, subfilePath);
		modifyFile(filePath);
		testGetChanges(visitor, 1, filePath);
		mockContainer(filePath);

//		modifyFile(subfilePath);
//		testGetChanges(visitor, 1, filePath);
	}

	protected void testGetChanges(FSVisitor visitor, int expectedSize, Path... paths) throws Exception {
		List<Path> result = visitor.getChanges();
		assertNotNull(result);
		assertEquals(expectedSize, result.size());

		Arrays.sort(paths);
		Object[] resArray = result.toArray();
		Arrays.sort(resArray);
		assertEquals(paths, resArray);
	}

	@Test
	public void testConstructor() throws Exception {
		visitor = new FSVisitor(rootPath, container);
	}

	@Test(
			dataProvider = "badConstructorData",
			expectedExceptions = {NullPointerException.class, IllegalArgumentException.class}
	)
	public void testBadConstructor(Path root, FSContainer container) throws Exception {
		visitor = new FSVisitor(root, container);
	}

	@DataProvider
	public Object[][] badConstructorData() {
		return new Object[][]{
				{null, null},
				{rootPath, null},
				{null, container},
				{Paths.get(root + fs.getSeparator() + "bad"), container}
		};
	}

	protected void mockContainer(Path file) throws IOException {
		FileNode node = FSUtils.createFileNode(file);
		when(container.get(file)).thenReturn(node);
	}

	protected void mockContainer(Path... files) throws IOException {
		for (Path path : files) {
			mockContainer(path);
		}
	}

	protected Path createFile(Path path) throws IOException {
		Path p = path.toAbsolutePath();
		Files.createFile(p);
		return p;
	}

	protected Path createFileWithTrash(Path path) throws IOException {
		Random rand = new Random();
		path = createFile(path);
		int size = rand.nextInt(1000);
		byte[] bytes = new byte[size];
		rand.nextBytes(bytes);
		FileUtils.writeByteArrayToFile(path.toFile(), bytes);
		return path;
	}

	protected void modifyFile(Path path) throws IOException {
		Random rand = new Random();
		int size = rand.nextInt(1000);
		byte[] bytes = new byte[size];
		rand.nextBytes(bytes);
		FileUtils.writeByteArrayToFile(path.toFile(), bytes);
	}


	protected Path createDirectory(Path path) throws IOException {
		Path p = path.toAbsolutePath();
		Files.createDirectory(p);
		return p;
	}

	protected void deleteFiles() throws IOException {
		FileUtils.cleanDirectory(rootPath.toFile());
	}
}
