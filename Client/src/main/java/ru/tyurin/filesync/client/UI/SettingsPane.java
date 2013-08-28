package ru.tyurin.filesync.client.UI;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;


public class SettingsPane extends JPanel implements Controlled {

	private final GridBagConstraints SYNC_DIR_LABEL_CONSTRAINTS = new GridBagConstraints(
			0, 0,
			1, 1,
			0, 0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(10, 0, 0, 0),
			0, 0
	);

	private final GridBagConstraints DIR_PATH_FIELD_CONSTRAINTS = new GridBagConstraints(
			0, 1,
			1, 1,
			0, 0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(10, 0, 0, 0),
			0, 0
	);

	private final GridBagConstraints FILE_CHOOSER_BUTTON_CONSTRAINTS = new GridBagConstraints(
			1, 1,
			1, 1,
			0, 0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(10, 0, 0, 0),
			0, 0
	);


	private final GridBagConstraints SAVE_BUTTON_CONSTRAINTS = new GridBagConstraints(
			1, 2,
			1, 1,
			0, 0,
			GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
			new Insets(10, 0, 0, 0),
			0, 0
	);

	private UIController controller = null;

	private JLabel syncDirectoryLabel;
	private JTextField dirPathField;
	private JButton chooserButton;
	private JFileChooser fileChooser;
	private JButton saveButton;

	private final String SYNC_DIR_LABEL_TEXT = "Sync directory:";
	private final String CHOOSER_BUTTON_TEXT = "...";
	private final String SAVE_BUTTON_TEXT = "Save";


	public SettingsPane() {
		super(new GridBagLayout());

		syncDirectoryLabel = createSyncDirectoryLabel();
		dirPathField = createDirPathField();
		chooserButton = createChooserButton();
		fileChooser = createFileChooser();
		saveButton = createSaveButton();


	}

	private JLabel createSyncDirectoryLabel() {
		JLabel label = new JLabel(SYNC_DIR_LABEL_TEXT);
		add(label, SYNC_DIR_LABEL_CONSTRAINTS);
		return label;
	}

	private JTextField createDirPathField() {
		JTextField textField = new JTextField();
		add(textField, DIR_PATH_FIELD_CONSTRAINTS);
		return textField;
	}

	private JFileChooser createFileChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(FileUtils.getUserDirectory());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		return chooser;
	}

	private JButton createChooserButton() {
		JButton button = new JButton(CHOOSER_BUTTON_TEXT);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chooserButtonAction(e);
			}
		});
		add(button, FILE_CHOOSER_BUTTON_CONSTRAINTS);
		return button;
	}

	private JButton createSaveButton() {
		JButton button = new JButton(SAVE_BUTTON_TEXT);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File syncDir = new File(dirPathField.getText());
				try {
					controller.createAndSaveSettingsFile(syncDir);
				} catch (IOException e1) {
					e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
				}
			}
		});

		add(button, SAVE_BUTTON_CONSTRAINTS);
		return button;
	}

	private void chooserButtonAction(ActionEvent e) {
		int result = fileChooser.showDialog(null, "Open");
		if (result == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			dirPathField.setText(f.getAbsolutePath());
		}
	}

	@Override
	public void setController(UIController controller) {
		this.controller = controller;
	}

	@Override
	public UIController getController() {
		return controller;
	}

}
