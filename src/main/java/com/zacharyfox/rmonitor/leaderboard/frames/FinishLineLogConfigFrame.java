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

	private JTextField textFieldRowHeight;
	private JButton buttonStartStop;

	private static FinishLineLogConfigFrame instance;

	private FinishLineLogConfigFrame() {
		initContent();
	}

	private void initContent() {
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][]"));
		setBounds(100, 100, 400, 150);

		JLabel labelRowHeight = new JLabel("Row Height:");
		labelRowHeight.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(labelRowHeight, "cell 0 0,alignx trailing");

		textFieldRowHeight = new JTextField();
		textFieldRowHeight.setText(ConfigurationManager.getInstance().getConfig(PROP_ROW_HEIGHT, "24"));
		textFieldRowHeight.setColumns(5);
		getContentPane().add(textFieldRowHeight, "cell 1 0,growx");

		buttonStartStop = new JButton(ACTION_START);
		buttonStartStop.setHorizontalAlignment(SwingConstants.RIGHT);
		buttonStartStop.addActionListener(this::handleAction);
		getContentPane().add(buttonStartStop, "cell 1 1,alignx right");
	}

	private void handleAction(ActionEvent evt) {
		switch (evt.getActionCommand()) {
		case ACTION_START:
			int height = Integer.parseInt(textFieldRowHeight.getText());
			ConfigurationManager.getInstance().setConfig(PROP_ROW_HEIGHT, height);
			FinishLineLogFrame finishLineLogFrame = new FinishLineLogFrame(height);
			finishLineLogFrame.setVisible(true);
			setVisible(false);
			break;

		case ACTION_STOP:
			buttonStartStop.setText(ACTION_START);
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