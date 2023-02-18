package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Duration;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.zacharyfox.rmonitor.config.ConfigurationManager;
import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.entities.Race.FlagStatus;
import com.zacharyfox.rmonitor.entities.RaceManager;
import com.zacharyfox.rmonitor.utils.DurationUtil;

@SuppressWarnings("serial")
public class LapCounterFrame extends JFrame {

	private static final String FONT_NAME = "Tahoma";

	private static final String PROP_LAP_SWITCH_DELAY = "lapCounter.lapSwitchDelay";

	private JPanel contentPanel;
	private JTextField tfLaps;
	private JButton cancelButton;
	private int lapSwitchDelay;
	private transient PropertyChangeListener propertyChangeListener = e -> SwingUtilities
			.invokeLater(() -> updateDisplay(e));
	private JTextField tfElapsedTime;
	private JTextField tfFlag;
	private JPanel infoPanel;
	private JLabel lblElapsedTime;
	private JLabel lblDelay;
	private JTextField tfDelay;
	private JCheckBox chckbxCountUpwards;

	private Timer lapUpdateDelayTimer;

	private RaceManager raceManager;
	private ConfigurationManager configManager;

	private static LapCounterFrame instance;

	public LapCounterFrame() {
		raceManager = RaceManager.getInstance();
		configManager = ConfigurationManager.getInstance();

		lapSwitchDelay = Integer.parseInt(configManager.getConfig(PROP_LAP_SWITCH_DELAY, "5"));

		setBounds(100, 100, 1446, 840);
		setExtendedState(Frame.MAXIMIZED_BOTH);

		contentPanel = new JPanel();
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));

		tfLaps = new JTextField();
		Font theFont = new Font(FONT_NAME, Font.PLAIN, (int) (getHeight() * .85));// 400
		tfLaps.setFont(theFont);
		tfLaps.setEditable(false);
		tfLaps.setHorizontalAlignment(SwingConstants.CENTER);
		tfLaps.setForeground(Color.WHITE);
		tfLaps.setText(Integer.toString(raceManager.getCurrentRace().getLapsToGo()));
		tfLaps.setBackground(Color.BLACK);
		contentPanel.add(tfLaps, BorderLayout.CENTER);
		tfLaps.setColumns(3);

		contentPanel.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				// Recalculate the variable you mentioned
				Font theFont = new Font(FONT_NAME, Font.PLAIN, (int) (e.getComponent().getHeight() * .85));// 400
				tfLaps.setFont(theFont);
			}
		});

		JPanel buttonPane = new JPanel();
		buttonPane.setBackground(Color.BLACK);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new BorderLayout(0, 0));

		cancelButton = new JButton("X");
		cancelButton.setFont(new Font(FONT_NAME, Font.BOLD, 11));
		cancelButton.setMnemonic('x');
		cancelButton.setBackground(Color.BLACK);
		cancelButton.setForeground(Color.RED);
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(e -> {
			instance.setVisible(false);
			raceManager.removePropertyChangeListener(propertyChangeListener);
			instance.dispose();
		});

		buttonPane.add(cancelButton, BorderLayout.EAST);

		tfFlag = new JTextField();
		tfFlag.setBackground(Color.BLACK);
		buttonPane.add(tfFlag, BorderLayout.CENTER);
		tfFlag.setColumns(30);

		infoPanel = new JPanel();
		infoPanel.setBackground(Color.BLACK);
		FlowLayout flowLayout = (FlowLayout) infoPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		buttonPane.add(infoPanel, BorderLayout.WEST);

		lblElapsedTime = new JLabel("Elapsed Time:");
		lblElapsedTime.setForeground(Color.WHITE);
		infoPanel.add(lblElapsedTime);

		tfElapsedTime = new JTextField();
		tfElapsedTime.setEditable(false);
		tfElapsedTime.setEnabled(false);
		tfElapsedTime.setColumns(8);
		infoPanel.add(tfElapsedTime);

		lblDelay = new JLabel("Delay:");
		lblDelay.setForeground(Color.WHITE);
		infoPanel.add(lblDelay);

		tfDelay = new JTextField();
		tfDelay.setColumns(4);
		tfDelay.setText(Integer.toString(lapSwitchDelay));
		infoPanel.add(tfDelay);

		chckbxCountUpwards = new JCheckBox("Count Laps upwards");
		chckbxCountUpwards.setBackground(Color.BLACK);
		chckbxCountUpwards.setForeground(Color.WHITE);
		chckbxCountUpwards.setSelected(false);
		infoPanel.add(chckbxCountUpwards);

		tfDelay.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateLapSwitchDelay(tfDelay.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateLapSwitchDelay(tfDelay.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateLapSwitchDelay(tfDelay.getText());
			}
		});

		updateFlagColor(raceManager.getCurrentRace().getFlagStatus());
		raceManager.addPropertyChangeListener(propertyChangeListener);
	}

	private void updateLapSwitchDelay(String delayText) {
		if (delayText.matches("\\d+")) {
			int newDelay = Integer.parseInt(delayText);

			if (newDelay != lapSwitchDelay) {
				lapSwitchDelay = newDelay;
				configManager.setConfig(PROP_LAP_SWITCH_DELAY, delayText);
			}
		}
	}

	private void updateDisplay(PropertyChangeEvent e) {
		switch (e.getPropertyName()) {
		case Race.PROPERTY_LAPS_COMPLETE:
			if (chckbxCountUpwards.isSelected() && !e.getOldValue().equals(e.getNewValue())) {
				updateDisplayedLapsCompleted((int) e.getOldValue(), (int) e.getNewValue());
			}
			break;

		case Race.PROPERTY_LAPS_TO_GO:
			if (!chckbxCountUpwards.isSelected() && !e.getOldValue().equals(e.getNewValue())) {
				updateDisplayedLapsToGo((int) e.getOldValue(), (int) e.getNewValue());
			}
			break;

		case Race.PROPERTY_ELAPSED_TIME:
			Duration elapsedTime = raceManager.getCurrentRace().getElapsedTime();
			tfElapsedTime.setText(DurationUtil.format(elapsedTime));
			break;

		case Race.PROPERTY_FLAG_STATUS:
			updateFlagColor((FlagStatus) e.getNewValue());
			break;

		default:
			break;
		}
	}

	private void updateDisplayedLapsToGo(int oldLaps, int newLaps) {
		Race race = raceManager.getCurrentRace();
		FlagStatus flagStatus = race.getFlagStatus();

		if (lapUpdateDelayTimer != null) {
			lapUpdateDelayTimer.stop();
			tfLaps.setText(Integer.toString(oldLaps));
		}

		if (flagStatus == Race.FlagStatus.PURPLE || flagStatus == Race.FlagStatus.NONE) {
			if (newLaps > 0) {
				tfLaps.setText(Integer.toString(newLaps));
			} else {
				tfLaps.setText("-");
			}
		} else {
			System.out.println(lapSwitchDelay);
			lapUpdateDelayTimer = new Timer(lapSwitchDelay * 1000, e -> tfLaps.setText(Integer.toString(newLaps)));
			lapUpdateDelayTimer.setRepeats(false);
			lapUpdateDelayTimer.start();
		}
	}

	private void updateDisplayedLapsCompleted(int oldLaps, int newLaps) {
		if (lapUpdateDelayTimer != null) {
			lapUpdateDelayTimer.stop();
			tfLaps.setText(Integer.toString(oldLaps));
		}

		lapUpdateDelayTimer = new Timer(lapSwitchDelay * 1000, e -> {
			if (newLaps >= 0) {
				tfLaps.setText(Integer.toString(newLaps));
			} else {
				tfLaps.setText("-");
			}
		});
		lapUpdateDelayTimer.setRepeats(false);
		lapUpdateDelayTimer.start();
	}

	private void updateFlagColor(FlagStatus flagStatus) {
		Color color = switch (raceManager.getCurrentRace().getFlagStatus()) {
		case GREEN -> Color.GREEN;
		case YELLOW -> Color.YELLOW;
		case RED -> Color.RED;
		case FINISH -> Color.LIGHT_GRAY;
		case PURPLE -> new Color(98, 0, 255);
		default -> Color.BLACK;
		};
		tfFlag.setBackground(color);
	}

	public static LapCounterFrame getInstance() {
		if (instance == null || !instance.isDisplayable()) {
			instance = new LapCounterFrame();
		}

		return instance;
	}
}
