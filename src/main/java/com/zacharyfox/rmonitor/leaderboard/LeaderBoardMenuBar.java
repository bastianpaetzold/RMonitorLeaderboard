package com.zacharyfox.rmonitor.leaderboard;

import java.awt.SystemColor;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

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

	private final JMenuItem aboutMenuItem;
	private final JMenuItem connectMenuItem;
	private final JMenuItem estimatorMenuItem;
	private final JMenu fileMenu;
	private final JMenuItem fullScreenMenuItem;
	private final JMenu helpMenu;
	private final JMenuItem playerMenuItem;
	private final JMenuItem recorderMenuItem;
	private final JMenuItem startSignalMenuItem;
	private final JMenuItem lapCounterMenuItem;
	private final JMenuItem finishLineLogMenuItem;
	private final JMenuItem webServerMenuItem;

	private final JMenu toolsMenu;
	private final JMenu viewMenu;

	public LeaderBoardMenuBar(final MainFrame mainFrame) {
		setBackground(SystemColor.menu);
		setBorder(UIManager.getBorder("Menu.border"));

		fileMenu = new JMenu("File");
		add(fileMenu);

		connectMenuItem = new JMenuItem("Connection");
		connectMenuItem.addActionListener(evt -> {
			ConnectFrame newFrame = ConnectFrame.getInstance();
			newFrame.setVisible(true);
		});

		fileMenu.add(connectMenuItem);

		viewMenu = new JMenu("View");
		add(viewMenu);

		fullScreenMenuItem = new JMenuItem("Full Screen");
		fullScreenMenuItem.addActionListener(e -> mainFrame.goFullScreen());
		viewMenu.add(fullScreenMenuItem);

		toolsMenu = new JMenu("Tools");
		add(toolsMenu);

		recorderMenuItem = new JMenuItem("Recorder");
		recorderMenuItem.addActionListener(evt -> {
			RecorderFrame newFrame = RecorderFrame.getInstance();
			newFrame.setVisible(true);
		});

		toolsMenu.add(recorderMenuItem);

		playerMenuItem = new JMenuItem("Player");
		playerMenuItem.addActionListener(evt -> {
			PlayerFrame newFrame = PlayerFrame.getInstance();
			newFrame.setVisible(true);
		});

		toolsMenu.add(playerMenuItem);

		estimatorMenuItem = new JMenuItem("Estimator");
		estimatorMenuItem.addActionListener(evt -> {
			EstimatorFrame newFrame = EstimatorFrame.getInstance();
			newFrame.setVisible(true);
		});
		toolsMenu.add(estimatorMenuItem);

		startSignalMenuItem = new JMenuItem("Start Signal");
		startSignalMenuItem.addActionListener(evt -> {
			StartSignalFrame newFrame = StartSignalFrame.getInstance();
			newFrame.setVisible(true);
		});
		toolsMenu.add(startSignalMenuItem);

		lapCounterMenuItem = new JMenuItem("Lap Counter");
		lapCounterMenuItem.addActionListener(evt -> {
			LapCounterFrame newFrame = LapCounterFrame.getInstance();
			newFrame.setVisible(true);
		});
		toolsMenu.add(lapCounterMenuItem);

		finishLineLogMenuItem = new JMenuItem("Finish Line Log");
		finishLineLogMenuItem.addActionListener(evt -> {
			FinishLineLogConfigFrame newFrame = FinishLineLogConfigFrame.getInstance();
			newFrame.setVisible(true);
		});
		toolsMenu.add(finishLineLogMenuItem);

		webServerMenuItem = new JMenuItem("Web Server");
		webServerMenuItem.addActionListener(evt -> {
			ServerFrame newFrame = ServerFrame.getInstance();
			newFrame.setVisible(true);
		});
		toolsMenu.add(webServerMenuItem);

		helpMenu = new JMenu("Help");
		helpMenu.setBorder(UIManager.getBorder("MenuItem.border"));
		helpMenu.setBackground(SystemColor.menu);
		add(helpMenu);

		aboutMenuItem = new JMenuItem("About");
		aboutMenuItem.setBorder(UIManager.getBorder("MenuItem.border"));
		aboutMenuItem.setBackground(SystemColor.menu);
		aboutMenuItem.addActionListener(e -> {
			AboutFrame newFrame = new AboutFrame();
			newFrame.setVisible(true);
		});

		helpMenu.add(aboutMenuItem);

		setVisible(true);
	}
}
