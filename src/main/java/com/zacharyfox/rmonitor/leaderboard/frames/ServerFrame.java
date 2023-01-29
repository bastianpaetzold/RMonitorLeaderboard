package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.zacharyfox.rmonitor.utils.JsonServer;

import net.miginfocom.swing.MigLayout;

public class ServerFrame extends JFrame  {
	private static final String PROP_PORT = "jsonServer.port";

	public JButton startStop;
	public JTextField port;
	private final JLabel portLabel;
	private static ServerFrame instance;
	private static final long serialVersionUID = 3848021032174790659L;
	private Properties properties;
	private JsonServer jsonServer;

	private ServerFrame(MainFrame mainFrame) {
		properties = mainFrame.getIni();
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][]"));
		setBounds(100, 100, 400, 150);

		portLabel = new JLabel("JsonServer Port:");
		portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(portLabel, "cell 0 0,alignx trailing");

		port = new JTextField();
		port.setText(properties.getProperty(PROP_PORT, Integer.toString(JsonServer.DEFAULT_PORT)));
		getContentPane().add(port, "cell 1 0,growx");
		port.setColumns(10);

		startStop = new JButton("Start");
		startStop.setHorizontalAlignment(SwingConstants.RIGHT);
		startStop.addActionListener(this::handleStartStopAction);
		getContentPane().add(startStop, "cell 1 1,alignx right");
	}

	public Integer getPort() {
		properties.setProperty(PROP_PORT, port.getText());
		return Integer.parseInt(port.getText());
	}

	public static ServerFrame getInstance(MainFrame mainFrame) {
		if (instance == null) {
			instance = new ServerFrame(mainFrame);
		}

		return instance;
	}

	private void handleStartStopAction(ActionEvent evt) {
		if (evt.getActionCommand().equals("Start")) {
			startStop.setText("Stop");
			jsonServer = new JsonServer();
			jsonServer.setPort(getPort());
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					jsonServer.start();
					return null;
				}
			}.execute();
		} else if (evt.getActionCommand().equals("Stop")) {
			jsonServer.stop();
			jsonServer = null;
			startStop.setText("Start");
		}
	}

}