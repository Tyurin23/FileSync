package ru.tyurin.filesync.client.UI;

/**
 * User: tyurin
 * Date: 8/23/13
 * Time: 10:24 AM
 */
public class UIView {

	private UIModel model;

	private BasicConfigurationFrame configurationFrame = new BasicConfigurationFrame();
	FileSyncMainFrame mainFrame;

	public UIView(UIModel model) {
		this.model = model;
	}

	public void setController(UIController controller) {
		configurationFrame.setController(controller);
	}

	public void showUI() {
		mainFrame = new FileSyncMainFrame();
	}

	public void showConfigurationWindow() {
		configurationFrame.showWelcomePane();
	}

	public BasicConfigurationFrame getConfigurationFrame() {
		return configurationFrame;
	}


}
