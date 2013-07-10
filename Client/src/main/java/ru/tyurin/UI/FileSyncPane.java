package ru.tyurin.UI;

import javax.swing.*;
import java.awt.*;

/**
 * User: tyurin
 * Date: 7/10/13
 * Time: 12:32 PM
 */
public class FileSyncPane extends JPanel {

	String[] s = {"A", "B", "C"};

	private static final String STATUS_ICON_PATH = "/img/ok.png";

	private static final double LIST_WY = 0.7;

	private final GridBagConstraints ICON_CONSTRAINTS = new GridBagConstraints(
			0, 0,
			1, 1,
			0, 0,
			GridBagConstraints.WEST, GridBagConstraints.NONE,
			new Insets(5, 5, 5, 5),
			0, 0
	);

	private final GridBagConstraints STATUS_TEXT_CONSTRAINTS = new GridBagConstraints(
			1, 0,
			1, 1,
			1, 0,
			GridBagConstraints.WEST, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0),
			0, 0
	);

	private final GridBagConstraints MENU_BUTTON_CONSTRAINTS = new GridBagConstraints(
			2, 0,
			1, 1,
			0, 0,
			GridBagConstraints.EAST, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0),
			0, 0
	);

	private final GridBagConstraints HIDE_BUTTON_CONSTRAINTS = new GridBagConstraints(
			3, 0,
			1, 1,
			0, 0,
			GridBagConstraints.EAST, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0),
			0, 0
	);

	private final GridBagConstraints LIST_CONSTRAINTS = new GridBagConstraints(
			0, 1,
			4, 1,
			0, LIST_WY,
			GridBagConstraints.EAST, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0),
			0, 0
	);

	public FileSyncPane() {
		super(new GridBagLayout());

		JLabel icon = new JLabel(new ImageIcon(getClass().getResource(STATUS_ICON_PATH)));
		icon.setSize(10, 10);
		add(icon, ICON_CONSTRAINTS);

		JLabel statusText = new JLabel("Status: OK");
		add(statusText, STATUS_TEXT_CONSTRAINTS);

		JButton menu = new JButton("Menu");
		add(menu, MENU_BUTTON_CONSTRAINTS);

		JButton hideButton = new JButton("_");
		add(hideButton, HIDE_BUTTON_CONSTRAINTS);


		JList<String> list = new JList<>(s);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setLayoutOrientation(JList.VERTICAL);
		JScrollPane scroll = new JScrollPane(list);
		add(scroll, LIST_CONSTRAINTS);
	}
}
