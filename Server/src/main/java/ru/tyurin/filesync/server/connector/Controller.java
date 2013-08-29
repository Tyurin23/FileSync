package ru.tyurin.filesync.server.connector;


import ru.tyurin.filesync.server.db.UserProvider;
import ru.tyurin.filesync.server.db.tables.BlockEntity;
import ru.tyurin.filesync.server.db.tables.FileEntity;
import ru.tyurin.filesync.server.db.tables.UserEntity;
import ru.tyurin.filesync.server.storage.BlockNode;
import ru.tyurin.filesync.server.storage.StorageManager;
import ru.tyurin.filesync.shared.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Controller {

	private Map<Request, RequestHandler> handlers = new HashMap<>();

	private final StorageManager storageManager;
	private final UserProvider userProvider;

	abstract class HandlerWithAuth implements RequestHandler {
		@Override
		public boolean isAuthRequired() {
			return true;
		}
	}

	abstract class HandlerWithoutAuth implements RequestHandler {
		@Override
		public boolean isAuthRequired() {
			return false;
		}
	}

	public ConnectionStatus processRequest(Request request, ServerSocketConnector connector) throws Exception {
		if (handlers.containsKey(request)) {
			return handlers.get(request).processRequest(connector);
		} else {
			throw new Exception(String.format("Controller %s not found", request));
		}
	}

	public boolean isAuthRequired(Request request) throws Exception {
		if (handlers.containsKey(request)) {
			return handlers.get(request).isAuthRequired();
		} else {
			throw new Exception(String.format("Controller %s not found", request));
		}
	}

	public void addAction(Request req, RequestHandler handler) {
		handlers.put(req, handler);
	}

	public Controller(final StorageManager storageManager) {
		this.storageManager = storageManager;
		this.userProvider = new UserProvider();

		addAction(Request.GET_FILE_NODES, new HandlerWithAuth() {
			@Override
			public ConnectionStatus processRequest(ServerSocketConnector connector) throws Exception {
				UserEntity user = userProvider.findById(connector.getUserID());
				List<FileTransferPart> files = new ArrayList<>(user.getFiles().size());
				for (FileEntity file : user.getFiles()) {
					FileTransferPart fileTransfer = new FileTransferPart();
					fileTransfer.setHash(file.getHash());
					fileTransfer.setSize(file.getSize());
					fileTransfer.setPath(file.getPath());
					files.add(fileTransfer);
				}
				connector.sendObject(files);
				return null;
			}
		});

		addAction(Request.GET_BLOCK, new HandlerWithAuth() {//todo
			@Override
			public ConnectionStatus processRequest(ServerSocketConnector connector) throws Exception {
				UserEntity user = userProvider.findById(connector.getUserID());
				BlockTransferPart blockRequest = (BlockTransferPart) connector.getObject();
				if (blockRequest != null) {
					FileEntity file = user.getFile(blockRequest.getPath());
					if (file != null) {
						BlockEntity block = file.getBlock(blockRequest.getBlockIndex());
						if (block != null) {

						}
					}
				}
				return null;
			}
		});

		addAction(Request.SAVE_BLOCK, new HandlerWithAuth() {
			@Override
			public ConnectionStatus processRequest(ServerSocketConnector connector) throws Exception {
				UserEntity user = userProvider.findById(connector.getUserID());
				BlockTransferPart part = (BlockTransferPart) connector.getObject();
				if (part != null) {
					BlockNode node = new BlockNode(part.getPath(), part.getBlockIndex(), user.getId());
					node.setData(part.getData());
					storageManager.saveBlock(node);
					FileEntity file = user.getFile(node.getPath());
					if (file == null) {
						file = new FileEntity();
						file.setPath(node.getPath());
						user.addFile(file);
					}
					BlockEntity block = file.getBlock(node.getIndex());
					if (block == null) {
						block = new BlockEntity();
						block.setIndex(node.getIndex());
						file.addBlock(block);
					}
					block.setHash(node.getHash());
					block.setSize(node.getSize());
					block.setDateModified(node.getDateModified());
					userProvider.updateUser(user);
					return ConnectionStatus.OK;
				} else {
					return ConnectionStatus.ERROR;
				}
			}
		});

		addAction(Request.UPDATE_FILE_INFO, new HandlerWithAuth() {
			@Override
			public ConnectionStatus processRequest(ServerSocketConnector connector) throws Exception {
				UserEntity user = userProvider.findById(connector.getUserID());
				FileTransferPart transferPart = (FileTransferPart) connector.getObject();
				if (transferPart != null) {
					FileEntity file = user.getFile(transferPart.getPath());
					if (file == null) {
						file = new FileEntity();
						file.setPath(transferPart.getPath());
						user.addFile(file);
					}
					file.setHash(transferPart.getHash());
					file.setSize(transferPart.getSize());
					userProvider.updateUser(user);
					return ConnectionStatus.OK;
				} else {
					return ConnectionStatus.ERROR;
				}
			}
		});

		addAction(Request.AUTH, new HandlerWithoutAuth() {
			@Override
			public ConnectionStatus processRequest(ServerSocketConnector connector) throws Exception {
				UserTransfer userTransfer = (UserTransfer) connector.getObject();
				UserEntity user = userProvider.findByLoginAndPassword(userTransfer.getLogin(), userTransfer.getPassword());
				if (user != null) {
					connector.setUserID(user.getId());
					return ConnectionStatus.OK;
				} else {
					return ConnectionStatus.AUTHENTICATION_ERROR;
				}
			}
		});


	}

}
