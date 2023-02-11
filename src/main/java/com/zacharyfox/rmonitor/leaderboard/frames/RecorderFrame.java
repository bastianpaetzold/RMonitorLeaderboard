package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.event.ActionEvent;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.zacharyfox.rmonitor.utils.Recorder;
import com.zacharyfox.rmonitor.utils.Recorder.State;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class RecorderFrame extends JFrame {

	private static final String ACTION_SAVE_AS = "Save As";
	private static final String ACTION_START = "Start";
	private static final String ACTION_STOP = "Stop";

	private final JTextField recorderFile;
	private final JButton selectFileButton;
	private final JButton startStop;
	private static RecorderFrame instance;

	private RecorderFrame() {
		Recorder recorder = Recorder.getInstance();

		getContentPane().setLayout(new MigLayout("", "[grow][][]", "[][]"));
		setBounds(100, 100, 400, 150);

		recorderFile = new JTextField();
		recorderFile.setText(recorder.getFilePath().toString());
		getContentPane().add(recorderFile, "cell 0 0,growx");
		recorderFile.setColumns(10);

		selectFileButton = new JButton(ACTION_SAVE_AS);
		selectFileButton.addActionListener(this::handleRecorderAction);
		getContentPane().add(selectFileButton, "cell 1 0");

		startStop = new JButton(ACTION_START);
		startStop.setEnabled(true);
		startStop.addActionListener(this::handleRecorderAction);
		getContentPane().add(startStop, "cell 2 0");

		recorder.addStateChangeListener(
				(oldState, newState) -> SwingUtilities.invokeLater(() -> handleRecorderState(newState)));
		handleRecorderState(recorder.getCurrentState());
	}

	private void handleRecorderState(State state) {
		switch (state) {
		case STARTED:
			startStop.setText(ACTION_STOP);
			recorderFile.setEnabled(false);
			selectFileButton.setEnabled(false);
			break;

		case STOPPED:
			startStop.setText(ACTION_START);
			recorderFile.setEnabled(true);
			selectFileButton.setEnabled(true);
			break;

		default:
			break;
		}
	}

	private void handleRecorderAction(ActionEvent evt) {
		Recorder recorder = Recorder.getInstance();

		switch (evt.getActionCommand()) {
		case ACTION_SAVE_AS:
			JFileChooser chooser = new JFileChooser();
			chooser.setSelectedFile(recorder.getFilePath().toFile());
			if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				recorderFile.setText(chooser.getSelectedFile().toString());
			}
			break;

		case ACTION_START:
			recorder.setFilePath(Paths.get(recorderFile.getText()));
			recorder.start();
			break;

		case ACTION_STOP:
			recorder.stop();
			break;

		default:
			break;
		}
	}

	public static RecorderFrame getInstance() {
		if (instance == null) {
			instance = new RecorderFrame();
		}

		return instance;
	}
}
