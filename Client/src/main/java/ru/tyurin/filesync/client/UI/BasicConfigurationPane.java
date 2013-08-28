package ru.tyurin.filesync.client.UI;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * User: tyurin
 * Date: 8/16/13
 * Time: 1:36 PM
 */
public class BasicConfigurationPane extends JPanel {

	private WelcomePane welcomePane;

	private final GridBagConstraints BACK_BUTTON_CONSTRAINTS = new GridBagConstraints(
			0, 0,
			1, 1,
			0.5, 0.1,
			GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
			new Insets(20, 20, 0, 0),
			0, 10
	);

	private final GridBagConstraints NEXT_BUTTON_CONSTRAINTS = new GridBagConstraints(
			1, 0,
			1, 1,
			0.5, 0,
			GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
			new Insets(20, 0, 0, 20),
			0, 10
	);

	private final GridBagConstraints PROGRESS_BAR_CONSTRAINTS = new GridBagConstraints(
			0, 1,
			2, 1,
			0, 0.1,
			GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
			new Insets(0, 20, 0, 20),
			0, 0
	);

	private final GridBagConstraints CONTENT_CONSTRAINTS = new GridBagConstraints(
			0, 2,
			2, 1,
			0, 0.8,
			GridBagConstraints.NORTH, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0),
			0, 0
	);

	public BasicConfigurationPane() {
		super(new GridBagLayout());

		welcomePane = new WelcomePane();
		welcomePane.setBorder(new LineBorder(Color.BLACK));

//		JButton back = new JButton("Back");
//		add(back, BACK_BUTTON_CONSTRAINTS);

//		JButton next = new JButton("Next");
//		add(next, NEXT_BUTTON_CONSTRAINTS);

//		JProgressBar progressBar = new JProgressBar();
//		add(progressBar, PROGRESS_BAR_CONSTRAINTS);

		add(welcomePane, CONTENT_CONSTRAINTS);


	}
}
