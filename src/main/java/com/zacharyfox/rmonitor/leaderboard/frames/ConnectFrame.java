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

	private static ConnectFrame instance;

	private JButton connectButton;
	private JTextField ip;
	private JTextField port;
	private JLabel ipLabel;
	private JLabel portLabel;

	private ConnectFrame() {
		RMonitorClient client = RMonitorClient.getInstance();

		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][][]"));

		ipLabel = new JLabel("Scoreboard IP:");
		ipLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(ipLabel, "cell 0 0,alignx trailing");
		setBounds(100, 100, 400, 150);

		ip = new JTextField();
		ip.setText(client.getHost());
		getContentPane().add(ip, "cell 1 0,growx");
		ip.setColumns(10);

		portLabel = new JLabel("Scoreboard Port:");
		portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(portLabel, "cell 0 1,alignx trailing");

		port = new JTextField();
		port.setText(Integer.toString(client.getPort()));
		getContentPane().add(port, "cell 1 1,growx");
		port.setColumns(10);

		connectButton = new JButton(ACTION_CONNECT);
		connectButton.setHorizontalAlignment(SwingConstants.RIGHT);
		connectButton.addActionListener(e -> handleClientAction(e, client));
		getContentPane().add(connectButton, "cell 1 2,alignx right");

		client.addStateChangeListener(
				(oldState, newState) -> SwingUtilities.invokeLater(() -> handleClientState(newState)));
		handleClientState(client.getCurrentState());
	}

	private void handleClientAction(ActionEvent event, RMonitorClient client) {
		switch (event.getActionCommand()) {
		case ACTION_CONNECT:
			client.setHost(ip.getText());
			client.setPort(Integer.parseInt(port.getText()));
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
			connectButton.setText(ACTION_DISCONNECT);
			ip.setEnabled(false);
			port.setEnabled(false);
			break;

		case CONNECTED:
			setTitle("Connected");
			ip.setEnabled(false);
			port.setEnabled(false);

			ConnectFrame.this.setVisible(false);
			break;

		case STOPPING:
			setTitle("Disconnecting...");
			ip.setEnabled(false);
			port.setEnabled(false);
			break;

		case STOPPED:
			setTitle("Disconnected");
			connectButton.setText(ACTION_CONNECT);
			ip.setEnabled(true);
			port.setEnabled(true);
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