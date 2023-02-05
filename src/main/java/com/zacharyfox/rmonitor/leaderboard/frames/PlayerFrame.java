package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.event.ActionEvent;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;

import com.zacharyfox.rmonitor.utils.Player;
import com.zacharyfox.rmonitor.utils.Player.State;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class PlayerFrame extends JFrame {

	private static final String ACTION_OPEN = "Open";
	private static final String ACTION_START = "Start";
	private static final String ACTION_STOP = "Stop";
	private static final String ACTION_PAUSE = "Pause";
	private static final String ACTION_RESUME = "Resume";

	private static PlayerFrame instance;

	private final JTextField playerFile;
	private final JTextField playerSpeedup;
	private final JButton selectFileButton;
	private final JButton startStop;
	private final JButton pauseResume;

	private PlayerFrame() {
		Player player = Player.getInstance();

		getContentPane().setLayout(new MigLayout("", "[grow][][][][]", "[][]"));
		setBounds(100, 100, 600, 150);

		playerFile = new JTextField();
		getContentPane().add(playerFile, "cell 0 0,growx");
		playerFile.setText(player.getFilePath().toString());
		playerFile.setColumns(10);

		selectFileButton = new JButton(ACTION_OPEN);
		selectFileButton.addActionListener(this::handlePlayerAction);
		getContentPane().add(selectFileButton, "cell 1 0");

		playerSpeedup = new JTextField();
		getContentPane().add(playerSpeedup, "cell 2 0");
		playerSpeedup.setText(Integer.toString(player.getSpeedup()));
		playerSpeedup.setColumns(3);

		startStop = new JButton(ACTION_START);
		startStop.setEnabled(true);
		startStop.addActionListener(this::handlePlayerAction);
		getContentPane().add(startStop, "cell 3 0");

		pauseResume = new JButton(ACTION_PAUSE);
		pauseResume.setEnabled(false);
		pauseResume.addActionListener(this::handlePlayerAction);
		getContentPane().add(pauseResume, "cell 4 0");

		player.addStateChangeListener((oldState, newState) -> handlePlayerState(newState));
		handlePlayerState(player.getCurrentState());
	}

	private void handlePlayerState(State state) {
		switch (state) {
		case STARTED, WAITING_FOR_CONNECTION:
			startStop.setText(ACTION_STOP);
			playerFile.setEnabled(false);
			selectFileButton.setEnabled(false);
			pauseResume.setEnabled(true);
			break;

		case PAUSED:
			startStop.setText(ACTION_STOP);
			playerFile.setEnabled(false);
			selectFileButton.setEnabled(false);
			pauseResume.setText(ACTION_RESUME);
			pauseResume.setEnabled(true);
			break;

		case RUNNING:
			startStop.setText(ACTION_STOP);
			playerFile.setEnabled(false);
			selectFileButton.setEnabled(false);
			pauseResume.setText(ACTION_PAUSE);
			pauseResume.setEnabled(true);
			break;

		case STOPPED:
			startStop.setText(ACTION_START);
			playerFile.setEnabled(true);
			selectFileButton.setEnabled(true);
			pauseResume.setText(ACTION_PAUSE);
			pauseResume.setEnabled(false);
			break;

		default:
			break;
		}
	}

	private void handlePlayerAction(ActionEvent evt) {
		Player player = Player.getInstance();

		switch (evt.getActionCommand()) {
		case ACTION_OPEN:
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(player.getFilePath().toFile());
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				playerFile.setText(chooser.getSelectedFile().toString());
			}
			break;

		case ACTION_START:
			player.setFilePath(Paths.get(playerFile.getText()));
			player.setSpeedup(Integer.parseInt(playerSpeedup.getText()));
			player.start();
			break;

		case ACTION_STOP:
			player.stop();
			break;

		case ACTION_PAUSE:
			pauseResume.setText(ACTION_RESUME);
			player.pause();
			break;

		case ACTION_RESUME:
			pauseResume.setText(ACTION_PAUSE);
			player.resume();
			break;

		default:
			break;
		}
	}

	public static PlayerFrame getInstance() {
		if (instance == null) {
			instance = new PlayerFrame();
		}

		return instance;
	}
}
