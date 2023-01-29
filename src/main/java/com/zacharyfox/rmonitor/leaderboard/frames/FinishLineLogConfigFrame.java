package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.zacharyfox.rmonitor.config.ConfigurationManager;

import net.miginfocom.swing.MigLayout;

public class FinishLineLogConfigFrame extends JFrame implements ActionListener {
	private static final String PROP_ROW_HEIGHT = "finishLineLog.rowHeight";

	private JButton startButton;
	private JTextField rowHeight;
	private final JLabel rowHeightLabel;
	private static FinishLineLogConfigFrame instance;
	private static final long serialVersionUID = 3848021032174790659L;

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

		startButton = new JButton("Start");
		startButton.setHorizontalAlignment(SwingConstants.RIGHT);
		startButton.addActionListener(this);
		getContentPane().add(startButton, "cell 1 1,alignx right");
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getActionCommand().equals("Start")) {
			int height = Integer.parseInt(rowHeight.getText());
			ConfigurationManager.getInstance().setConfig(PROP_ROW_HEIGHT, height);
			FinishLineLogFrame newFrame = new FinishLineLogFrame(height);
			newFrame.setVisible(true);
			this.setVisible(false);
		} else if (evt.getActionCommand().equals("Stop")) {
			startButton.setText("Start");
		}
	}

	public static FinishLineLogConfigFrame getInstance() {
		if (instance == null) {
			instance = new FinishLineLogConfigFrame();
		}
	
		return instance;
	}

}