package ru.tyurin.filesync.client.fs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//TODO modify and delete files in tree
public class FileCreator {

	private final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXUZabcdefghijklmnopqrstuvwxyz1234567890.-_";
	private final int maxBlockSize = 1000000;

	private File base;
	protected int maxDepth = 3;
	protected int maxFiles = 20;
	protected int minFiles = 0;
	protected int maxFileSize = 10000000;
	protected int maxNameSize = 30;
	protected double dirProbability = 0.3;

	private Random rand = new Random();
	private List<File> createdFiles = new ArrayList<>();


	public static void createTree(File base) throws IOException {
		new FileCreator(base).createTree();
	}

	public static List<File> createTree(File base, int maxDepth, int maxFiles) throws IOException {
		FileCreator creator = new FileCreator(base);
		creator.maxDepth = maxDepth;
		creator.maxFiles = maxFiles;
		creator.createTree();
		return creator.createdFiles;
	}

	public static File createSingleFile(File base) throws IOException {
		FileCreator creator = new FileCreator(base);
		creator.maxDepth = 0;
		creator.maxFiles = 1;
		creator.minFiles = 1;
		creator.dirProbability = 0.0;
		creator.createTree();
		if (creator.createdFiles.size() < 1 || creator.createdFiles.size() > 1) {
			throw new Error("Fuck((");
		}
		return creator.createdFiles.get(0);
	}

	public FileCreator(File base) {
		this.base = base;
	}

	public void createTree() throws IOException {
		create(base, 0);
	}


	protected void create(File root, int depth) throws IOException {
		int filesCount = minFiles + rand.nextInt(maxFiles - minFiles + 1);
		while ((filesCount--) > 0) {
			if (isCreateDir()) {
				File childDir = createDir(root);
				if (depth < maxDepth) {
					create(childDir, depth + 1);
				}
				continue;
			}
			createFile(root);
		}
	}

	private void createFile(File root) throws IOException {
		int nameLen = 1 + rand.nextInt(maxNameSize);
		int trashSize = rand.nextInt(maxFileSize);
		File childFile = new File(root, generateString(nameLen));
		childFile.createNewFile();
		writeTrash(childFile, trashSize);
		createdFiles.add(childFile);
	}

	private File createDir(File root) {
		int nameLen = 1 + rand.nextInt(maxNameSize);
		File childDir = new File(root, generateString(nameLen));
		childDir.mkdir();
		createdFiles.add(childDir);
		return childDir;
	}

	protected void writeTrash(File file, int trashSize) throws IOException {
		if (file.isDirectory()) {
			return;
		}// bug?
		if (trashSize > maxBlockSize) {
			int blocksCount = trashSize / maxBlockSize + (trashSize % maxBlockSize != 0 ? 1 : 0);
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			byte[] bytes = new byte[maxBlockSize];
			while ((blocksCount--) > 0) {
				if (blocksCount == 0) {
					bytes = new byte[trashSize % maxBlockSize];
				}
				rand.nextBytes(bytes);
				out.write(bytes);
				out.flush();
			}
			out.close();
		} else {
			byte[] bytes = new byte[trashSize];
			rand.nextBytes(bytes);
			FileOutputStream out = new FileOutputStream(file);
			out.write(bytes);
			out.close();
		}
	}


	protected boolean isCreateDir() {
		return (rand.nextDouble() <= dirProbability ? true : false);
	}

	protected String generateString(int length) {
		char[] text = new char[length];
		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rand.nextInt(characters.length()));
		}
		return new String(text);
	}


}
