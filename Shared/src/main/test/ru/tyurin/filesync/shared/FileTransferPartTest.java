package ru.tyurin.filesync.shared;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


public class FileTransferPartTest {

	final String fileName = "test";

	FileOutputStream fileOut;
	FileInputStream fileIn;
	ObjectOutputStream out;
	ObjectInputStream in;

	@BeforeMethod
	public void setUp() throws Exception {
		fileOut = new FileOutputStream(fileName);
		fileIn = new FileInputStream(fileName);

		out = new ObjectOutputStream(fileOut);
		in = new ObjectInputStream(fileIn);
	}

	@AfterMethod
	public void tearDown() throws Exception {
		Files.delete(Paths.get(fileName));
	}

	@Test
	public void testSerialization() throws Exception {
		final String name = "TEST";
		FileTransferPart preSerialize = new FileTransferPart(name);
		out.writeObject(preSerialize);
		out.flush();

		FileTransferPart postSerialize = (FileTransferPart) in.readObject();
		assertNotNull(postSerialize);
		assertEquals(preSerialize.getName(), postSerialize.getName());
		assertEquals(preSerialize.toString(), postSerialize.toString());

	}
}
