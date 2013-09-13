package ru.tyurin.filesync.client.connector;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.shared.ConnectionStatus;
import ru.tyurin.filesync.shared.Request;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.*;


public class ClientSocketConnector {

	public static Logger LOG = Logger.getLogger(ClientSocketConnector.class);

	protected Socket socket;
	ObjectInputStream input;
	ObjectOutputStream output;


	public ClientSocketConnector(Socket socket) throws IOException {
		this.socket = socket;
		output = new ObjectOutputStream(socket.getOutputStream());
		output.flush();
		input = new ObjectInputStream(socket.getInputStream());
	}

	protected ClientSocketConnector() {
	}


	public void close() throws IOException {
		input.close();
		output.close();
		socket.close();
		LOG.debug("Socket closed");
	}


	public ConnectionStatus sendObject(Object obj) throws IOException {
		sendObj(obj);
		return (ConnectionStatus) receiveObj();
	}


	public Object receiveObject() throws IOException {
		Callable<Object> task = new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				return input.readObject();
			}
		};
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<Object> future = executor.submit(task);
		Object obj = null;
		try {
			obj = future.get(2, TimeUnit.SECONDS);
		} catch (InterruptedException | TimeoutException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e);
		}
		LOG.debug(String.format("Object %s read", obj));
		return obj;
	}



	public ConnectionStatus sendRequest(Request request) throws IOException {
		if (request == null) {
			throw new NullPointerException("Request is null");
		}
		return sendObject(request);
	}

	public boolean isClosed() {
		return socket.isClosed();
	}

	private void sendObj(final Object obj) throws IOException {
		if (obj != null) {
			Callable<Object> task = new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					output.writeObject(obj);
					output.flush();
					return null;
				}
			};
			ExecutorService executor = Executors.newCachedThreadPool();
			Future<Object> future = executor.submit(task);
			try {
				future.get(2, TimeUnit.SECONDS);
			} catch (InterruptedException | TimeoutException e) {
				e.printStackTrace();
				return;
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException(e);
			}
			LOG.debug(String.format("Object %s sent", obj));
		}
	}

	private Object receiveObj() throws IOException {
		Object obj = null;
		try {
			obj = input.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		LOG.debug(String.format("Object %s read", obj));
		return obj;
	}


}
