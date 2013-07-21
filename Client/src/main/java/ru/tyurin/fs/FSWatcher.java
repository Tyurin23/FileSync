/*
package ru.tyurin.fs;*/
/*
* Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
*   - Redistributions of source code must retain the above copyright
*     notice, this list of conditions and the following disclaimer.
*
*   - Redistributions in binary form must reproduce the above copyright
*     notice, this list of conditions and the following disclaimer in the
*     documentation and/or other materials provided with the distribution.
*
*   - Neither the name of Oracle nor the names of its
*     contributors may be used to endorse or promote products derived
*     from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
* IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
* THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
* PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
* LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*//*


import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

*/
/**
* Example to watch a directory (or tree) for changes to files.
*//*


class WatchDir {

	private final WatchService watcher;
	private final Map<WatchKey,Path> keys;
	private final boolean recursive;
	private boolean trace = false;

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>)event;
	}

	*/
/**
	 * Register the given directory with the WatchService
	 *//*

	private void register(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		if (trace) {
			Path prev = keys.get(key);
			if (prev == null) {
				System.out.format("register: %s\n", dir);
			} else {
				if (!dir.equals(prev)) {
					System.out.format("update: %s -> %s\n", prev, dir);
				}
			}
		}
		keys.put(key, dir);
	}

	*/
/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 *//*

	private void registerAll(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
					throws IOException
			{
				register(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	*/
/**
	 * Creates a WatchService and registers the given directory
	 *//*

	WatchDir(Path dir, boolean recursive) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey,Path>();
		this.recursive = recursive;

		if (recursive) {
			System.out.format("Scanning %s ...\n", dir);
			registerAll(dir);
			System.out.println("Done.");
		} else {
			register(dir);
		}

		// enable trace after initial registration
		this.trace = true;
	}

	*/
/**
	 * Process all events for keys queued to the watcher
	 *//*

	void processEvents() {
		for (;;) {

			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
				continue;
			}

			for (WatchEvent<?> event: key.pollEvents()) {
				WatchEvent.Kind kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				Path child = dir.resolve(name);

				// print out event
				System.out.format("%s: %s\n", event.kind().name(), child);

				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (recursive && (kind == ENTRY_CREATE)) {
					try {
						if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
							registerAll(child);
						}
					} catch (IOException x) {
						// ignore to keep sample readbale
					}
				}
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}

	static void usage() {
		System.err.println("usage: java WatchDir [-r] dir");
		System.exit(-1);
	}

	public static void main(String[] args) throws IOException {
		// parse arguments
		if (args.length == 0 || args.length > 2)
			usage();
		boolean recursive = false;
		int dirArg = 0;
		if (args[0].equals("-r")) {
			if (args.length < 2)
				usage();
			recursive = true;
			dirArg++;
		}

		// register directory and process its events
		Path dir = Paths.get(args[dirArg]);
		new WatchDir(dir, recursive).processEvents();
	}
}
*/


package ru.tyurin.fs;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class FSWatcher implements Runnable {

	static org.apache.log4j.Logger LOG = Logger.getLogger(FSManager.class);

	WatchService watcher;
	Path dir;
	Map<WatchKey, Path> keys;

	public FSWatcher(Path path) throws IOException {
		this.keys = new HashMap<>();
		this.watcher = FileSystems.getDefault().newWatchService();
		this.dir = path;
		registerTree(this.dir);

	}

	private void registerTree(Path root) throws IOException {
		Files.walkFileTree(root, new SimpleFileVisitor<Path>(){
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				registerFile(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	private void registerFile(Path file) throws IOException {
		WatchKey key = file.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		LOG.info(String.format("%s registred.", file));
		keys.put(key, file);
	}


	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			tick();
		}
	}

	protected void tick(){
		WatchKey key;
		try {
			key = watcher.take();
		} catch (InterruptedException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			return;
		}
		for(WatchEvent<?> event : key.pollEvents()){
			WatchEvent.Kind kind = event.kind();
			if(kind == OVERFLOW){
				continue;
			}
			WatchEvent<Path> ev = (WatchEvent<Path>) event;
			Path name = ev.context();
			Path child = dir.resolve(name);

			LOG.info(String.format("%s: %s\n", event.kind().name(), child));
		}

	}

	public List<Path> getWatched(){
		WatchKey key;
		try {
			key = watcher.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		Path dir = keys.get(key);
		if(dir == null){
			return null;
		}
		List<Path> watched = new ArrayList<>();
		for(WatchEvent<?> event : key.pollEvents()){
			WatchEvent.Kind kind = event.kind();
			if(kind == OVERFLOW){
				continue;
			}
			WatchEvent<Path> ev = (WatchEvent<Path>) event;
			Path name = ev.context();
			Path child = dir.resolve(name);
			LOG.info(String.format("%s: %s\n", event.kind().name(), child));
			watched.add(child);
		}
		return watched;
	}

}
