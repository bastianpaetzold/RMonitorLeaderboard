package com.zacharyfox.rmonitor.leaderboard.frames;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.zacharyfox.rmonitor.client.RMonitorClient;
import com.zacharyfox.rmonitor.config.ConfigurationManager;

import net.miginfocom.swing.MigLayout;

public class ConnectFrame extends JFrame {

	private static final long serialVersionUID = -7099004415134104465L;

	private static final String PROP_IP = "connect.ip";
	private static final String PROP_PORT = "connect.port";

	private static final String ACTION_DISCONNECT = "Disconnect";
	private static final String ACTION_CONNECT = "Connect";

	private static ConnectFrame instance;

	private JButton connectButton;
	private JTextField ip;
	private JTextField port;
	private JLabel ipLabel;
	private JLabel portLabel;

	private ConnectFrame() {
		ConfigurationManager configManager = ConfigurationManager.getInstance();
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][][]"));

		ipLabel = new JLabel("Scoreboard IP:");
		ipLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(ipLabel, "cell 0 0,alignx trailing");
		setBounds(100, 100, 400, 150);

		ip = new JTextField();
		ip.setText(configManager.getConfig(PROP_IP, RMonitorClient.DEFAULT_HOST));
		getContentPane().add(ip, "cell 1 0,growx");
		ip.setColumns(10);

		portLabel = new JLabel("Scoreboard Port:");
		portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(portLabel, "cell 0 1,alignx trailing");

		port = new JTextField();
		port.setText(configManager.getConfig(PROP_PORT, Integer.toString(RMonitorClient.DEFAULT_PORT)));
		getContentPane().add(port, "cell 1 1,growx");
		port.setColumns(10);

		connectButton = new JButton(ACTION_CONNECT);
		connectButton.setHorizontalAlignment(SwingConstants.RIGHT);
		RMonitorClient client = RMonitorClient.getInstance();
		connectButton.addActionListener(e -> {
			switch (e.getActionCommand()) {
			case ACTION_CONNECT:
				configManager.setConfig(PROP_IP, ip.getText());
				configManager.setConfig(PROP_PORT, port.getText());
				configManager.saveConfig();

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
		});
		client.addStateChangeListener((oldState, newState) -> {
			switch (newState) {
			case STARTED:
				connectButton.setText("Connecting...");
				connectButton.setEnabled(false);
				break;

			case RUNNING:
				connectButton.setText(ACTION_DISCONNECT);
				ip.setEnabled(false);
				port.setEnabled(false);
				connectButton.setEnabled(true);
				ConnectFrame.this.setVisible(false);
				break;

			case STOPPED:
				connectButton.setText(ACTION_CONNECT);
				ip.setEnabled(true);
				port.setEnabled(true);
				connectButton.setEnabled(true);
				break;
			}
		});
		getContentPane().add(connectButton, "cell 1 2,alignx right");
	}

	public static ConnectFrame getInstance() {
		if (instance == null) {
			instance = new ConnectFrame();
		}

		return instance;
	}
}