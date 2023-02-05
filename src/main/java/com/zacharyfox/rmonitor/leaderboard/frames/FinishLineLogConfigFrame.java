package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.zacharyfox.rmonitor.config.ConfigurationManager;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class FinishLineLogConfigFrame extends JFrame {

	private static final String PROP_ROW_HEIGHT = "finishLineLog.rowHeight";

	private static final String ACTION_START = "Start";
	private static final String ACTION_STOP = "Stop";

	private static FinishLineLogConfigFrame instance;

	private JButton startButton;
	private JTextField rowHeight;
	private JLabel rowHeightLabel;

	private FinishLineLogConfigFrame() {
		ConfigurationManager configManager = ConfigurationManager.getInstance();

		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][]"));
		setBounds(100, 100, 400, 150);

		rowHeightLabel = new JLabel("Row Height:");
		rowHeightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(rowHeightLabel, "cell 0 0,alignx trailing");

		rowHeight = new JTextField();
		rowHeight.setText(configManager.getConfig(PROP_ROW_HEIGHT, "24"));
		getContentPane().add(rowHeight, "cell 1 0,growx");
		rowHeight.setColumns(5);

		startButton = new JButton(ACTION_START);
		startButton.setHorizontalAlignment(SwingConstants.RIGHT);
		startButton.addActionListener(this::handleAction);
		getContentPane().add(startButton, "cell 1 1,alignx right");
	}

	private void handleAction(ActionEvent evt) {
		switch (evt.getActionCommand()) {
		case ACTION_START:
			int height = Integer.parseInt(rowHeight.getText());
			ConfigurationManager.getInstance().setConfig(PROP_ROW_HEIGHT, height);
			FinishLineLogFrame newFrame = new FinishLineLogFrame(height);
			newFrame.setVisible(true);
			this.setVisible(false);
			break;

		case ACTION_STOP:
			startButton.setText(ACTION_START);
			break;

		default:
			break;
		}
	}

	public static FinishLineLogConfigFrame getInstance() {
		if (instance == null) {
			instance = new FinishLineLogConfigFrame();
		}

		return instance;
	}
}