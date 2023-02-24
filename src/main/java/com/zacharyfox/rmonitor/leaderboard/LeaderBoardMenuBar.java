package com.zacharyfox.rmonitor.leaderboard;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.zacharyfox.rmonitor.leaderboard.frames.AboutFrame;
import com.zacharyfox.rmonitor.leaderboard.frames.ConnectFrame;
import com.zacharyfox.rmonitor.leaderboard.frames.EstimatorFrame;
import com.zacharyfox.rmonitor.leaderboard.frames.FinishLineLogConfigFrame;
import com.zacharyfox.rmonitor.leaderboard.frames.LapCounterFrame;
import com.zacharyfox.rmonitor.leaderboard.frames.MainFrame;
import com.zacharyfox.rmonitor.leaderboard.frames.PlayerFrame;
import com.zacharyfox.rmonitor.leaderboard.frames.RecorderFrame;
import com.zacharyfox.rmonitor.leaderboard.frames.ServerFrame;
import com.zacharyfox.rmonitor.leaderboard.frames.StartSignalFrame;

@SuppressWarnings("serial")
public class LeaderBoardMenuBar extends JMenuBar {

	public LeaderBoardMenuBar(MainFrame mainFrame) {
		add(createMenuFile());
		add(createMenuView(mainFrame));
		add(createMenuTools());
		add(createMenuHelp());
	}

	private JMenu createMenuFile() {
		JMenu menuFile = new JMenu("File");

		JMenuItem menuItemConnection = new JMenuItem("Connection");
		menuItemConnection.addActionListener(e -> ConnectFrame.getInstance().setVisible(true));
		menuFile.add(menuItemConnection);

		return menuFile;
	}

	private JMenu createMenuTools() {
		JMenu menuTools = new JMenu("Tools");

		JMenuItem menuItemRecorder = new JMenuItem("Recorder");
		menuItemRecorder.addActionListener(e -> RecorderFrame.getInstance().setVisible(true));
		menuTools.add(menuItemRecorder);

		JMenuItem menuItemPlayer = new JMenuItem("Player");
		menuItemPlayer.addActionListener(e -> PlayerFrame.getInstance().setVisible(true));
		menuTools.add(menuItemPlayer);

		JMenuItem menuItemEstimator = new JMenuItem("Estimator");
		menuItemEstimator.addActionListener(e -> EstimatorFrame.getInstance().setVisible(true));
		menuTools.add(menuItemEstimator);

		JMenuItem menuItemStartSignal = new JMenuItem("Start Signal");
		menuItemStartSignal.addActionListener(e -> new StartSignalFrame().setVisible(true));
		menuTools.add(menuItemStartSignal);

		JMenuItem menuItemLapCounter = new JMenuItem("Lap Counter");
		menuItemLapCounter.addActionListener(e -> new LapCounterFrame().setVisible(true));
		menuTools.add(menuItemLapCounter);

		JMenuItem menuItemFinishLineLog = new JMenuItem("Finish Line Log");
		menuItemFinishLineLog.addActionListener(e -> FinishLineLogConfigFrame.getInstance().setVisible(true));
		menuTools.add(menuItemFinishLineLog);

		JMenuItem menuItemWebServer = new JMenuItem("Web Server");
		menuItemWebServer.addActionListener(e -> ServerFrame.getInstance().setVisible(true));
		menuTools.add(menuItemWebServer);

		return menuTools;
	}

	private JMenu createMenuView(MainFrame mainFrame) {
		JMenu menuView = new JMenu("View");

		JMenuItem menuItemFullScreen = new JMenuItem("Full Screen");
		menuItemFullScreen.addActionListener(e -> mainFrame.enterFullScreen());
		menuView.add(menuItemFullScreen);

		return menuView;
	}

	private JMenu createMenuHelp() {
		JMenu menuHelp = new JMenu("Help");

		JMenuItem menuItemAbout = new JMenuItem("About");
		menuItemAbout.addActionListener(e -> new AboutFrame().setVisible(true));
		menuHelp.add(menuItemAbout);

		return menuHelp;
	}
}
