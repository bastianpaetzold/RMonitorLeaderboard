package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

public class FinishLineLogConfigFrame extends JFrame implements ActionListener
{
	private static final String PROP_ROW_HEIGHT = "finishLineLog.rowHeight";
	
	public JButton startButton;
	public JTextField rowHeight;
	private final JLabel rowHeightLabel;
	private static FinishLineLogConfigFrame instance;
	private static final long serialVersionUID = 3848021032174790659L;
	private Properties properties;
	private MainFrame mainFrame;
	
	private FinishLineLogConfigFrame(MainFrame mainFrame)
	{
		properties = mainFrame.getIni();
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][]"));
		setBounds(100, 100, 400, 150);
		
		rowHeightLabel = new JLabel("Row Height:");
		rowHeightLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(rowHeightLabel, "cell 0 0,alignx trailing");

		rowHeight = new JTextField();
		rowHeight.setText(properties.getProperty(PROP_ROW_HEIGHT, "24"));
		getContentPane().add(rowHeight, "cell 1 0,growx");
		rowHeight.setColumns(5);

		startButton = new JButton("Start");
		startButton.setHorizontalAlignment(SwingConstants.RIGHT);
		startButton.addActionListener(this);
		getContentPane().add(startButton, "cell 1 1,alignx right");
		mainFrame.storeIniFile();
		
		this.mainFrame = mainFrame;
	}


	public Integer getRowHeight()
	{
		properties.setProperty(PROP_ROW_HEIGHT,rowHeight.getText());
		return Integer.parseInt(rowHeight.getText());
	}

	public static FinishLineLogConfigFrame getInstance(MainFrame mainFrame)
	{
		if (instance == null) {
			instance = new FinishLineLogConfigFrame(mainFrame);
		}

		return instance;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getActionCommand().equals("Start")) {
			FinishLineLogFrame newFrame = new FinishLineLogFrame(mainFrame, getRowHeight());
			newFrame.setVisible(true);
			//startButton.setText("Stop");
			mainFrame.storeIniFile();
			this.setVisible(false);
		} else if (evt.getActionCommand().equals("Stop")) {
			startButton.setText("Start");
		}
	}

}