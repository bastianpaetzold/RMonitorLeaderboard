package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.event.ActionEvent;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import com.zacharyfox.rmonitor.utils.Player;
import com.zacharyfox.rmonitor.utils.Player.State;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class PlayerFrame extends JFrame {

	private static final String ACTION_SELECT = "Select...";
	private static final String ACTION_START = "Start";
	private static final String ACTION_STOP = "Stop";
	private static final String ACTION_PAUSE = "Pause";
	private static final String ACTION_RESUME = "Resume";

	private JTextField textFieldFilePath;
	private JSpinner spinnerSpeedup;
	private JButton buttonSelectFile;
	private JButton buttonStartStop;
	private JButton buttonPauseResume;

	private static PlayerFrame instance;

	private PlayerFrame() {
		setBounds(100, 100, 600, 150);

		initContent();

		Player player = Player.getInstance();
		player.addStateChangeListener(
				(oldState, newState) -> SwingUtilities.invokeLater(() -> handlePlayerState(newState)));
		handlePlayerState(player.getCurrentState());
	}

	private void initContent() {
		Player player = Player.getInstance();

		getContentPane().setLayout(new MigLayout("", "[grow][][][][]", "[][]"));

		textFieldFilePath = new JTextField();
		textFieldFilePath.setText(player.getFilePath().toString());
		textFieldFilePath.setColumns(10);
		getContentPane().add(textFieldFilePath, "cell 0 0,growx");

		buttonSelectFile = new JButton(ACTION_SELECT);
		buttonSelectFile.addActionListener(this::handlePlayerAction);
		getContentPane().add(buttonSelectFile, "cell 1 0");

		SpinnerNumberModel model = new SpinnerNumberModel(player.getSpeedup(), 0, 99, 1);
		spinnerSpeedup = new JSpinner(model);
		getContentPane().add(spinnerSpeedup, "cell 2 0");

		buttonStartStop = new JButton(ACTION_START);
		buttonStartStop.addActionListener(this::handlePlayerAction);
		getContentPane().add(buttonStartStop, "cell 3 0");

		buttonPauseResume = new JButton(ACTION_PAUSE);
		buttonPauseResume.addActionListener(this::handlePlayerAction);
		buttonPauseResume.setEnabled(false);
		getContentPane().add(buttonPauseResume, "cell 4 0");
	}

	private void handlePlayerState(State state) {
		switch (state) {
		case STARTED, WAITING_FOR_CONNECTION:
			buttonStartStop.setText(ACTION_STOP);
			textFieldFilePath.setEnabled(false);
			buttonSelectFile.setEnabled(false);
			buttonPauseResume.setEnabled(true);
			break;

		case PAUSED:
			buttonStartStop.setText(ACTION_STOP);
			textFieldFilePath.setEnabled(false);
			buttonSelectFile.setEnabled(false);
			buttonPauseResume.setText(ACTION_RESUME);
			buttonPauseResume.setEnabled(true);
			break;

		case RUNNING:
			buttonStartStop.setText(ACTION_STOP);
			textFieldFilePath.setEnabled(false);
			buttonSelectFile.setEnabled(false);
			buttonPauseResume.setText(ACTION_PAUSE);
			buttonPauseResume.setEnabled(true);
			break;

		case STOPPED:
			buttonStartStop.setText(ACTION_START);
			textFieldFilePath.setEnabled(true);
			buttonSelectFile.setEnabled(true);
			buttonPauseResume.setText(ACTION_PAUSE);
			buttonPauseResume.setEnabled(false);
			break;

		default:
			break;
		}
	}

	private void handlePlayerAction(ActionEvent evt) {
		Player player = Player.getInstance();

		switch (evt.getActionCommand()) {
		case ACTION_SELECT:
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(player.getFilePath().toFile());
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				textFieldFilePath.setText(chooser.getSelectedFile().toString());
			}
			break;

		case ACTION_START:
			player.setFilePath(Paths.get(textFieldFilePath.getText()));
			player.setSpeedup((int) spinnerSpeedup.getValue());
			player.start();
			break;

		case ACTION_STOP:
			player.stop();
			break;

		case ACTION_PAUSE:
			buttonPauseResume.setText(ACTION_RESUME);
			player.pause();
			break;

		case ACTION_RESUME:
			buttonPauseResume.setText(ACTION_PAUSE);
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
