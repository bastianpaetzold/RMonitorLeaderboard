package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.zacharyfox.rmonitor.utils.JsonServer;
import com.zacharyfox.rmonitor.utils.JsonServer.State;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class ServerFrame extends JFrame {

	private static final String ACTION_START = "Start";
	private static final String ACTION_STOP = "Stop";

	private JButton startStop;
	private JTextField portField;
	private JLabel portLabel;

	private static ServerFrame instance;

	private ServerFrame() {
		JsonServer jsonServer = JsonServer.getInstance();

		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][]"));
		setBounds(100, 100, 400, 150);

		portLabel = new JLabel("JsonServer Port:");
		portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(portLabel, "cell 0 0,alignx trailing");

		portField = new JTextField();
		portField.setText(Integer.toString(jsonServer.getPort()));
		getContentPane().add(portField, "cell 1 0,growx");
		portField.setColumns(10);

		startStop = new JButton();
		startStop.setHorizontalAlignment(SwingConstants.RIGHT);
		startStop.addActionListener(this::handlerServerAction);
		getContentPane().add(startStop, "cell 1 1,alignx right");
		jsonServer.addStateChangeListener(
				(oldState, newState) -> SwingUtilities.invokeLater(() -> handleServerState(newState)));
		handleServerState(jsonServer.getCurrentState());
	}

	private void handleServerState(State state) {
		switch (state) {
		case STARTED, RUNNING:
			startStop.setText(ACTION_STOP);
			break;

		case STOPPED:
			startStop.setText(ACTION_START);
			break;

		default:
			break;
		}
	}

	private void handlerServerAction(ActionEvent evt) {
		switch (evt.getActionCommand()) {
		case ACTION_START:
			JsonServer server = JsonServer.getInstance();
			server.setPort(Integer.parseInt(portField.getText()));
			new SwingWorker<Void, Void>() {

				@Override
				protected Void doInBackground() throws Exception {
					server.start();
					return null;
				}
			}.execute();
			break;

		case ACTION_STOP:
			JsonServer.getInstance().stop();
			break;

		default:
			break;
		}
	}

	public static ServerFrame getInstance() {
		if (instance == null) {
			instance = new ServerFrame();
		}

		return instance;
	}
}