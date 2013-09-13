package ru.tyurin.filesync.client.fs;



import junit.framework.Assert;
import org.testng.annotations.*;

import java.util.*;
import java.util.zip.CRC32;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;


public class LocalFSTest {

	FSManager manager;
	FSContainer container;
	Collection<FileNode> nodeCollection;
	LocalFS fs;

	static Random rand;


	@BeforeClass
	public static void setUpClass() throws Exception{
		rand = new Random();
	}

	@BeforeMethod
	public void setUp() throws Exception {
		manager = mock(FSManager.class);
		container = mock(FSContainer.class);
		nodeCollection = new ArrayList<>();

		when(manager.getContainer()).thenReturn(container);
	}

	public FileNode getMockedFileNode(String path, long hash, long size){
		FileNode node = mock(FileNode.class);
		when(node.getPath()).thenReturn(path);
		when(node.getHash()).thenReturn(hash);
		when(node.getSpace()).thenReturn(size);
		return node;
	}

	public FileNode getMockedFileNode(){
		return getMockedFileNode(String.valueOf(rand.nextDouble()), rand.nextInt(), rand.nextInt());
	}

	public LocalFS getLocalFs(FileNode... nodes){
		FSManager manager = mock(FSManager.class);
		FSContainer container = mock(FSContainer.class);

		List<FileNode> nodesList = Arrays.asList(nodes);
		List<FileNode> sortedNodesList = new ArrayList<>(nodesList);
		Collections.sort(sortedNodesList);

		when(manager.getContainer()).thenReturn(container);
		when(container.getSortedList()).thenReturn(sortedNodesList);
		when(container.getCollection()).thenReturn(nodesList);

		return new LocalFS(manager);
	}


	@AfterMethod
	public void tearDownMethod() throws Exception {


	}

	@AfterClass
	public static void tearDown() throws Exception {

	}

	@Test
	public void testConstructor() throws Exception {
		LocalFS fs = new LocalFS(manager);

		Assert.assertNotNull(fs);
	}

	@Test(
			expectedExceptions = NullPointerException.class
	)
	public void testConstructorWithNull() throws Exception {
		LocalFS fs = new LocalFS(null);
	}

	@Test
	public void testGetChecksum() throws Exception {
		LocalFS fs = getLocalFs(getMockedFileNode());


		long checksum = fs.getChecksum();


		assertTrue( 0 < checksum );
	}

	@Test
	public void testGetChecksumEmptyContainer() throws Exception {
		LocalFS fs = getLocalFs();

		long checksum = fs.getChecksum();

		assertEquals(checksum, 0);
	}

	@Test
	public void testIsEquals() throws Exception {
		FileNode node = getMockedFileNode();
		LocalFS fs = getLocalFs(node);
		LocalFS other = getLocalFs(node);

		assertTrue(fs.isEquals(other));
	}

	@Test
	public void testIsEqualsFalse() throws Exception {
		LocalFS fs = getLocalFs(getMockedFileNode());
		LocalFS otherFs = getLocalFs(getMockedFileNode());


		assertFalse(fs.isEquals(otherFs));
	}

	@Test(
			expectedExceptions = NullPointerException.class
	)
	public void testIsEqualsNull() throws Exception {
		LocalFS fs = getLocalFs(getMockedFileNode());

		fs.isEquals(null);

		fail();
	}
}
