package ru.tyurin.filesync.client.UI;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class FileSyncUI {

	public final Object monitor = new Object();

	private UIModel model;
	private UIController controller;
	private UIView view;

	public FileSyncUI() {
		this.model = new UIModel();

		this.view = new UIView(this.model);
		this.controller = new UIController(this.model, view);
	}

	public FileSyncUI(UIModel model, UIView view, UIController controller) {
		this.model = model;
		this.view = view;
		this.controller = controller;
	}

	public void showStartupConfig() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				view.showConfigurationWindow();
				view.getConfigurationFrame().addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						super.windowClosing(e);
						synchronized (monitor) {
							monitor.notifyAll();
							System.out.println("notify");
						}
					}
				});
			}
		});
	}

	public void showUI() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				view.showUI();
			}
		});
	}

	public UIModel getModel() {
		return model;
	}

}
