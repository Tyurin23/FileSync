package ru.tyurin.filesync.client.fs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.tyurin.filesync.client.util.MessageSystem;
import ru.tyurin.filesync.client.util.event.Event;
import ru.tyurin.filesync.client.util.event.EventType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Queue;

import static org.mockito.Mockito.*;

/**
 * User: tyurin
 * Date: 7/11/13
 * Time: 11:30 AM
 */
public class FSTest {

	public static final String path = "/home/tyurin/tmp/testSync/";
	Path basicDir;
	MessageSystem mMessageSystem;

	Queue<Path> createdFiles = new ArrayDeque<>();


	@Before
	public void setUp() throws Exception {
		basicDir = Files.createDirectory(Paths.get(path));
		mMessageSystem = mock(MessageSystem.class);
//		when(mMessageSystem.isRefreshFileSystem()).thenReturn(true);
	}

	@After
	public void tearDown() throws Exception {
		Path cf;
		while ((cf = createdFiles.poll()) != null) {
			Files.delete(cf);
		}
		Files.delete(basicDir);

	}

	@Test
	public void FSTest() throws IOException {
		FSManager manager = new FSManager(basicDir, mMessageSystem);
		verify(mMessageSystem).addListener(manager, EventType.REFRESH);

		manager.runTick();
		verify(mMessageSystem, never()).addEvent(any(Event.class));
		createFile("hello");

	}

	protected void createFile(String path) throws IOException {
		Path p = Paths.get(FSTest.path, path);
		Files.createFile(p);
		createdFiles.offer(p);
	}


}
