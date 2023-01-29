package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.zacharyfox.rmonitor.config.ConfigurationManager;
import com.zacharyfox.rmonitor.utils.JsonServer;

import net.miginfocom.swing.MigLayout;

public class ServerFrame extends JFrame {
	private static final String PROP_PORT = "jsonServer.port";

	private JButton startStop;
	private JTextField portField;
	private final JLabel portLabel;
	private static ServerFrame instance;
	private static final long serialVersionUID = 3848021032174790659L;
	private JsonServer jsonServer;

	private ServerFrame() {
		ConfigurationManager configManager = ConfigurationManager.getInstance();

		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][]"));
		setBounds(100, 100, 400, 150);

		portLabel = new JLabel("JsonServer Port:");
		portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(portLabel, "cell 0 0,alignx trailing");

		portField = new JTextField();
		portField.setText(configManager.getConfig(PROP_PORT, Integer.toString(JsonServer.DEFAULT_PORT)));
		getContentPane().add(portField, "cell 1 0,growx");
		portField.setColumns(10);

		startStop = new JButton("Start");
		startStop.setHorizontalAlignment(SwingConstants.RIGHT);
		startStop.addActionListener(this::handleStartStopAction);
		getContentPane().add(startStop, "cell 1 1,alignx right");
	}

	private void handleStartStopAction(ActionEvent evt) {
		if (evt.getActionCommand().equals("Start")) {
			startStop.setText("Stop");

			int port = Integer.parseInt(portField.getText());
			ConfigurationManager.getInstance().setConfig(PROP_PORT, port);

			jsonServer = new JsonServer();
			jsonServer.setPort(port);
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

	public static ServerFrame getInstance() {
		if (instance == null) {
			instance = new ServerFrame();
		}

		return instance;
	}

}