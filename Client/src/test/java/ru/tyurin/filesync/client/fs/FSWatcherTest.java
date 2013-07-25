package ru.tyurin.filesync.client.fs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.fail;


public class FSWatcherTest {

	static final String dir = "/home/tyurin/tmp/test";
	Path baseDir;

	@Before
	public void setUp() throws Exception {
		baseDir = Paths.get(dir);
		if(baseDir == null){
			System.out.println("base dir error!");
			System.exit(-1);
		}
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void FSWathcerTest(){
		try {
			FSWatcher watcher = new FSWatcher(baseDir);
			watcher.run();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}

}
