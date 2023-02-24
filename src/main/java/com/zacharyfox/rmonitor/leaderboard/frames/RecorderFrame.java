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

	private JTextField textFieldFilePath;
	private JButton buttonSelectFile;
	private JButton buttonStartStop;

	private static RecorderFrame instance;

	private RecorderFrame() {
		setBounds(100, 100, 400, 150);

		initContent();

		Recorder recorder = Recorder.getInstance();
		recorder.addStateChangeListener(
				(oldState, newState) -> SwingUtilities.invokeLater(() -> handleRecorderState(newState)));
		handleRecorderState(recorder.getCurrentState());
	}

	private void initContent() {
		getContentPane().setLayout(new MigLayout("", "[grow][][]", "[][]"));

		textFieldFilePath = new JTextField();
		textFieldFilePath.setText(Recorder.getInstance().getFilePath().toString());
		textFieldFilePath.setColumns(10);
		getContentPane().add(textFieldFilePath, "cell 0 0,growx");

		buttonSelectFile = new JButton(ACTION_SAVE_AS);
		buttonSelectFile.addActionListener(this::handleRecorderAction);
		getContentPane().add(buttonSelectFile, "cell 1 0");

		buttonStartStop = new JButton(ACTION_START);
		buttonStartStop.addActionListener(this::handleRecorderAction);
		getContentPane().add(buttonStartStop, "cell 2 0");
	}

	private void handleRecorderState(State state) {
		switch (state) {
		case STARTED:
			buttonStartStop.setText(ACTION_STOP);
			textFieldFilePath.setEnabled(false);
			buttonSelectFile.setEnabled(false);
			break;

		case STOPPED:
			buttonStartStop.setText(ACTION_START);
			textFieldFilePath.setEnabled(true);
			buttonSelectFile.setEnabled(true);
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
				textFieldFilePath.setText(chooser.getSelectedFile().toString());
			}
			break;

		case ACTION_START:
			recorder.setFilePath(Paths.get(textFieldFilePath.getText()));
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
