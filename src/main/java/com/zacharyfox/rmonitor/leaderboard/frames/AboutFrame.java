package com.zacharyfox.rmonitor.leaderboard.frames;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class AboutFrame extends JFrame {

	private JTextPane textAbout;
	private JButton buttonOk;

	public AboutFrame() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		getContentPane().setLayout(new MigLayout("", "[grow][]", "[100.00,grow][]"));
		setBounds(100, 100, 450, 175);

		textAbout = new JTextPane();
		textAbout.setBorder(null);
		textAbout.setBackground(null);
		textAbout.setContentType("text/html");
		textAbout.setText(
				"""
						<strong>RMonitorLeaderboard</strong>

						<p>copyright &copy; 2013 Zachary Fox, 2017 Kai Höfler, 2023 Bastian Pätzold</p>

						<p>Project: <a href="https://github.com/bastianpaetzold/RMonitorLeaderboard">https://github.com/bastianpaetzold/RMonitorLeaderboard</a></p>
						""");
		getContentPane().add(textAbout, "cell 0 0 2 1,grow");

		buttonOk = new JButton("OK");
		buttonOk.addActionListener(evt -> {
			setVisible(false); // you can't see me!
			dispose();
		});

		getContentPane().add(buttonOk, "cell 1 1");
	}
}
