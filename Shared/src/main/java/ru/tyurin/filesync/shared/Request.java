package ru.tyurin.filesync.shared;

import java.io.Serializable;

/**
 * User: tyurin
 * Date: 8/5/13
 * Time: 3:17 PM
 */
public enum Request implements Serializable {
	GET_FILE_NODES,
	GET_FILE,
	SEND_FILE,
	GET_BLOCK_NODES,
	GET_BLOCK,
	GET_BLOCK_DATA,
	SEND_BLOCK_DATA,
	REMOVE_FILE,
	AUTH,
	REGISTRATION,
	GET_FILESYSTEM_CHECKSUM
}
