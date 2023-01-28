package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.zacharyfox.rmonitor.utils.JsonServer;

import net.miginfocom.swing.MigLayout;

public class ServerFrame extends JFrame implements ActionListener
{
	private static final String PROP_PORT = "jsonServer.port";
	
	public JButton startStop;
	public JTextField port;
	private final JLabel portLabel;
	private static ServerFrame instance;
	private static final long serialVersionUID = 3848021032174790659L;
	private Properties properties;
	private static JsonServer jsonServer;
	private MainFrame mainFrame;

	private ServerFrame(MainFrame mainFrame)
	{
		properties = mainFrame.getIni();
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][]"));
		setBounds(100, 100, 400, 150);
		
		portLabel = new JLabel("JsonServer Port:");
		portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		getContentPane().add(portLabel, "cell 0 0,alignx trailing");

		port = new JTextField();
		port.setText(properties.getProperty(PROP_PORT, "8080"));
		getContentPane().add(port, "cell 1 0,growx");
		port.setColumns(10);

		startStop = new JButton("Start");
		startStop.setHorizontalAlignment(SwingConstants.RIGHT);
		startStop.addActionListener(this);
		getContentPane().add(startStop, "cell 1 1,alignx right");
		this.mainFrame = mainFrame;
	}


	public Integer getPort()
	{
		properties.setProperty(PROP_PORT,port.getText());
		return Integer.parseInt(port.getText());
	}

	public static ServerFrame getInstance(MainFrame mainFrame)
	{
		if (instance == null) {
			instance = new ServerFrame(mainFrame);
		}

		return instance;
	}
	
	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if (evt.getActionCommand().equals("Start")) {
			startStop.setText("Stop");
			jsonServer = new JsonServer(getPort(), mainFrame);
			jsonServer.execute();

		} else if (evt.getActionCommand().equals("Stop")) {
			jsonServer.stopServer();
			jsonServer = null;
			startStop.setText("Start");
		}
	}

}