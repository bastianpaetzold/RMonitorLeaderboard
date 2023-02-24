package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.zacharyfox.rmonitor.client.RMonitorClient;
import com.zacharyfox.rmonitor.client.RMonitorClient.State;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class ConnectFrame extends JFrame {

	private static final String ACTION_DISCONNECT = "Disconnect";
	private static final String ACTION_CONNECT = "Connect";

	private JTextField textFieldIP;
	private JTextField textFieldPort;
	private JButton buttonConnect;

	private static ConnectFrame instance;

	private ConnectFrame() {
		setBounds(100, 100, 400, 150);

		initContent();

		RMonitorClient client = RMonitorClient.getInstance();
		client.addStateChangeListener(
				(oldState, newState) -> SwingUtilities.invokeLater(() -> handleClientState(newState)));
		handleClientState(client.getCurrentState());
	}

	private void initContent() {
		RMonitorClient client = RMonitorClient.getInstance();
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][][]"));

		JLabel labelIP = new JLabel("Scoreboard IP:");
		labelIP.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(labelIP, "cell 0 0,alignx trailing");

		textFieldIP = new JTextField();
		textFieldIP.setText(client.getHost());
		textFieldIP.setColumns(10);
		getContentPane().add(textFieldIP, "cell 1 0,growx");

		JLabel labelPort = new JLabel("Scoreboard Port:");
		labelPort.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(labelPort, "cell 0 1,alignx trailing");

		textFieldPort = new JTextField();
		textFieldPort.setText(Integer.toString(client.getPort()));
		textFieldPort.setColumns(10);
		getContentPane().add(textFieldPort, "cell 1 1,growx");

		buttonConnect = new JButton(ACTION_CONNECT);
		buttonConnect.setHorizontalAlignment(SwingConstants.RIGHT);
		buttonConnect.addActionListener(this::handleClientAction);
		getContentPane().add(buttonConnect, "cell 1 2,alignx right");
	}

	private void handleClientAction(ActionEvent event) {
		RMonitorClient client = RMonitorClient.getInstance();
		switch (event.getActionCommand()) {
		case ACTION_CONNECT:
			client.setHost(textFieldIP.getText());
			client.setPort(Integer.parseInt(textFieldPort.getText()));
			client.start();
			break;

		case ACTION_DISCONNECT:
			client.stop();
			break;

		default:
			break;
		}
	}

	private void handleClientState(State state) {
		switch (state) {
		case STARTED, CONNECTING:
			setTitle("Connecting...");
			buttonConnect.setText(ACTION_DISCONNECT);
			textFieldIP.setEnabled(false);
			textFieldPort.setEnabled(false);
			break;

		case CONNECTED:
			setTitle("Connected");
			textFieldIP.setEnabled(false);
			textFieldPort.setEnabled(false);

			ConnectFrame.this.setVisible(false);
			break;

		case STOPPING:
			setTitle("Disconnecting...");
			textFieldIP.setEnabled(false);
			textFieldPort.setEnabled(false);
			break;

		case STOPPED:
			setTitle("Disconnected");
			buttonConnect.setText(ACTION_CONNECT);
			textFieldIP.setEnabled(true);
			textFieldPort.setEnabled(true);
			break;

		default:
			break;
		}

	}

	public static ConnectFrame getInstance() {
		if (instance == null) {
			instance = new ConnectFrame();
		}

		return instance;
	}
}