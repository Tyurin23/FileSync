package ru.tyurin.filesync.client.fs;


import java.io.Serializable;

public enum FileStatus implements Serializable {

	DELETED,
	NEW,
	MODIFIED_SERVER_PRIORITY,
	MODIFIED_CLIENT_PRIORITY,
	NORMAL,
	BAD
}
