package ru.tyurin.filesync.shared;


import java.io.Serializable;

public enum FileStatus implements Serializable {

	DELETED,
	NEW,
	MODIFIED,
	NORMAL,
	BAD
}
