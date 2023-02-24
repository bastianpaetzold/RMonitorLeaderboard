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

	private JTextField textFieldPort;
	private JButton buttonStartStop;

	private static ServerFrame instance;

	private ServerFrame() {
		setBounds(100, 100, 400, 150);

		initContent();

		JsonServer jsonServer = JsonServer.getInstance();
		jsonServer.addStateChangeListener(
				(oldState, newState) -> SwingUtilities.invokeLater(() -> handleServerState(newState)));
		handleServerState(jsonServer.getCurrentState());
	}

	private void initContent() {
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][]"));

		JLabel labelPort = new JLabel("Port:");
		labelPort.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(labelPort, "cell 0 0,alignx trailing");

		textFieldPort = new JTextField();
		textFieldPort.setText(Integer.toString(JsonServer.getInstance().getPort()));
		textFieldPort.setColumns(10);
		getContentPane().add(textFieldPort, "cell 1 0,growx");

		buttonStartStop = new JButton();
		buttonStartStop.setHorizontalAlignment(SwingConstants.RIGHT);
		buttonStartStop.addActionListener(this::handlerServerAction);
		getContentPane().add(buttonStartStop, "cell 1 1,alignx right");
	}

	private void handleServerState(State state) {
		switch (state) {
		case STARTED, RUNNING:
			buttonStartStop.setText(ACTION_STOP);
			break;

		case STOPPED:
			buttonStartStop.setText(ACTION_START);
			break;

		default:
			break;
		}
	}

	private void handlerServerAction(ActionEvent evt) {
		switch (evt.getActionCommand()) {
		case ACTION_START:
			JsonServer server = JsonServer.getInstance();
			server.setPort(Integer.parseInt(textFieldPort.getText()));
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