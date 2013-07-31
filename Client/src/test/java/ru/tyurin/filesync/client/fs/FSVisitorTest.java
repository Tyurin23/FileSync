package ru.tyurin.filesync.client.fs;

import org.testng.annotations.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
/*
TODO
file created
file modified
file deleted
folder created
folder deleted
not readable
hidden files
links

 */

public class FSVisitorTest {

	Queue<Path> createdFiles = new ArrayDeque<>();

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

	@Test(testName = "createFile")
	public void testCreateFile() throws Exception {
		final String file = "test";
		visitor = new FSVisitor(rootPath, container);
		testGetChanges(visitor, 0);

		Path createdFile = createFile(rootPath.resolve(file).toString());
		testGetChanges(visitor, 1, createdFile);

		when(container.get(rootPath.toAbsolutePath().resolve(file).toString())).thenReturn(FSUtils.createFileNode(rootPath.toAbsolutePath().resolve(file)));
		createdFile = createFile(rootPath.resolve(file + "1").toString());
		testGetChanges(visitor, 1, createdFile);
	}


	protected void testGetChanges(FSVisitor visitor, int expectedSize, Path... paths) throws Exception {
		List<Path> result = visitor.getChanges();
		assertNotNull(result);
		assertEquals(expectedSize, result.size());
		assertEquals(paths, result.toArray());
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

	protected Path createFile(String path) throws IOException {
		Path p = Paths.get(path);
		Files.createFile(p);
		createdFiles.offer(p);
		return p;
	}

	protected void deleteFiles() throws IOException {
		Path cf;
		while ((cf = createdFiles.poll()) != null) {
			Files.delete(cf);
		}
	}
}
