package ru.tyurin.filesync.shared;

import java.io.Serializable;

/**
 * User: tyurin
 * Date: 8/5/13
 * Time: 3:17 PM
 */
public enum Request implements Serializable {
	GET_FILE_NODES,
	AUTH,
	GET_BLOCK,
	SAVE_BLOCK
}
