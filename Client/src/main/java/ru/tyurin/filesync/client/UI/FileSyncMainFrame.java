package ru.tyurin.filesync.client.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: tyurin
 * Date: 7/10/13
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileSyncMainFrame extends JFrame {

	private static final String TITLE = "FileSync";
	private static final int WINDOW_WIDHT = 300;
	private static final int WINDOW_HEIGHT = 200;

	private static final String TRAY_ICON_PATH = "/img/tray.png";


	public FileSyncMainFrame() {
		super(TITLE);
		setResizable(false);
		setSize(WINDOW_WIDHT, WINDOW_HEIGHT);
		boolean isHaveTray = createTray();
		if (isHaveTray) {
			setUndecorated(true);
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		} else {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		setContentPane(new FileSyncPane());
		setLocationRelativeTo(null);
//		setVisible(true);
	}

	public void showUI() {
		setVisible(true);
	}

	private boolean createTray() {
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			URL iconUrl = getClass().getResource(TRAY_ICON_PATH);
			Image img = Toolkit.getDefaultToolkit().getImage(iconUrl);

			final JPopupMenu popup = new JPopupMenu();
			JMenuItem exitItem = new JMenuItem("Exit");
			exitItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
//					UIController.getInstance().exit();
				}
			});
			popup.add(exitItem);
			JMenuItem openUIItem = new JMenuItem("Show Status");
			openUIItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(true);
				}
			});


			final TrayIcon icon = new TrayIcon(img, "FileSync");
			icon.setImageAutoSize(true);
			icon.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						Desktop.getDesktop().open(new File(""));//todo
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			icon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						popup.setLocation(e.getX(), e.getY());
						popup.setInvoker(popup);
						popup.setVisible(true);
					}
				}
			});


			try {
				tray.add(icon);
				return true;
			} catch (AWTException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			System.out.println("Tray is not supported");
			return false;
		}
	}

}
