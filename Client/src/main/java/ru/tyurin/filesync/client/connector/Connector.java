package ru.tyurin.filesync.client.connector;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/4/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Connector {

	public void close() throws IOException;

	public void sendObject(Object part) throws IOException;

	public Object getObject() throws IOException;

	public boolean isClosed();

}
