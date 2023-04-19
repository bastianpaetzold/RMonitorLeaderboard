package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Duration;

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

	private JTextField textFieldLaps;
	private JTextField textFieldFlag;
	private JTextField textFieldElapsedTime;
	private JTextField textFieldDelay;
	private JCheckBox checkBoxCountUpwards;

	private Timer lapUpdateDelayTimer;
	private int lapSwitchDelay;

	public LapCounterFrame() {
		lapSwitchDelay = Integer.parseInt(ConfigurationManager.getInstance().getConfig(PROP_LAP_SWITCH_DELAY, "5"));

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1446, 840);
		setExtendedState(Frame.MAXIMIZED_BOTH);

		initContent();

		PropertyChangeListener listener = e -> SwingUtilities.invokeLater(() -> updateDisplay(e));
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				RaceManager.getInstance().removePropertyChangeListener(listener);
			}
		});
		RaceManager.getInstance().addPropertyChangeListener(listener);
	}

	private void initContent() {
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createPanelLaps(), BorderLayout.CENTER);

		JPanel panelSouth = new JPanel(new BorderLayout());
		panelSouth.add(createPanelInfo(), BorderLayout.WEST);

		textFieldFlag = new JTextField();
		textFieldFlag.setBackground(Color.BLACK);
		textFieldFlag.setColumns(30);
		panelSouth.add(textFieldFlag, BorderLayout.CENTER);

		getContentPane().add(panelSouth, BorderLayout.SOUTH);

		updateFlagColor(RaceManager.getInstance().getCurrentRace().getFlagStatus());
	}

	private JPanel createPanelLaps() {
		JPanel panelLaps = new JPanel();
		panelLaps.setLayout(new BorderLayout(0, 0));
		panelLaps.setBorder(new EmptyBorder(5, 5, 5, 5));

		textFieldLaps = new JTextField();
		textFieldLaps.setBackground(Color.BLACK);
		textFieldLaps.setForeground(Color.WHITE);
		textFieldLaps.setFont(new Font(FONT_NAME, Font.PLAIN, (int) (getHeight() * .85)));
		textFieldLaps.setEditable(false);
		textFieldLaps.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldLaps.setColumns(3);
		textFieldLaps.setText(Integer.toString(RaceManager.getInstance().getCurrentRace().getLapsToGo()));
		panelLaps.add(textFieldLaps, BorderLayout.CENTER);
		panelLaps.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				Font font = new Font(FONT_NAME, Font.PLAIN, (int) (e.getComponent().getHeight() * .85));
				textFieldLaps.setFont(font);
			}
		});

		return panelLaps;
	}

	private JPanel createPanelInfo() {
		JPanel panelInfo = new JPanel();
		panelInfo.setBackground(Color.BLACK);
		panelInfo.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel labelElapsedTime = new JLabel("Elapsed Time:");
		labelElapsedTime.setForeground(Color.WHITE);
		panelInfo.add(labelElapsedTime);

		textFieldElapsedTime = new JTextField();
		textFieldElapsedTime.setEditable(false);
		textFieldElapsedTime.setEnabled(false);
		textFieldElapsedTime.setColumns(8);
		panelInfo.add(textFieldElapsedTime);

		JLabel labelDelay = new JLabel("Delay:");
		labelDelay.setForeground(Color.WHITE);
		panelInfo.add(labelDelay);

		textFieldDelay = new JTextField();
		textFieldDelay.setColumns(4);
		textFieldDelay.setText(Integer.toString(lapSwitchDelay));
		textFieldDelay.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateLapSwitchDelay(textFieldDelay.getText());
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateLapSwitchDelay(textFieldDelay.getText());
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				updateLapSwitchDelay(textFieldDelay.getText());
			}
		});
		panelInfo.add(textFieldDelay);

		checkBoxCountUpwards = new JCheckBox("Count Laps upwards");
		checkBoxCountUpwards.setBackground(Color.BLACK);
		checkBoxCountUpwards.setForeground(Color.WHITE);
		checkBoxCountUpwards.setSelected(false);
		panelInfo.add(checkBoxCountUpwards);

		return panelInfo;
	}

	private void updateLapSwitchDelay(String delayText) {
		if (delayText.matches("\\d+")) {
			int newDelay = Integer.parseInt(delayText);

			if (newDelay != lapSwitchDelay) {
				lapSwitchDelay = newDelay;
				ConfigurationManager.getInstance().setConfig(PROP_LAP_SWITCH_DELAY, delayText);
			}
		}
	}

	private void updateDisplay(PropertyChangeEvent e) {
		switch (e.getPropertyName()) {
		case Race.PROPERTY_LAPS_COMPLETE:
			if (checkBoxCountUpwards.isSelected() && !e.getOldValue().equals(e.getNewValue())) {
				updateDisplayedLapsCompleted((int) e.getOldValue(), (int) e.getNewValue());
			}
			break;

		case Race.PROPERTY_LAPS_TO_GO:
			if (!checkBoxCountUpwards.isSelected() && !e.getOldValue().equals(e.getNewValue())) {
				updateDisplayedLapsToGo((int) e.getNewValue());
			}
			break;

		case Race.PROPERTY_ELAPSED_TIME:
			Duration elapsedTime = RaceManager.getInstance().getCurrentRace().getElapsedTime();
			textFieldElapsedTime.setText(DurationUtil.format(elapsedTime));
			break;

		case Race.PROPERTY_FLAG_STATUS:
			updateFlagColor((FlagStatus) e.getNewValue());
			if (!checkBoxCountUpwards.isSelected() && e.getOldValue().equals(FlagStatus.PURPLE)
					&& e.getNewValue().equals(FlagStatus.GREEN)) {
				int lapsToGo = RaceManager.getInstance().getCurrentRace().getLapsToGo();
				textFieldLaps.setText(Integer.toString(lapsToGo > 0 ? lapsToGo - 1 : 0));
			}
			break;

		default:
			break;
		}
	}

	private void updateDisplayedLapsToGo(int lapsToGo) {
		Race race = RaceManager.getInstance().getCurrentRace();
		FlagStatus flagStatus = race.getFlagStatus();

		if (lapUpdateDelayTimer != null) {
			lapUpdateDelayTimer.stop();
			// set the current laps to go just in case the delay is longer than the time
			// since the last update
			textFieldLaps.setText(Integer.toString(lapsToGo));
		}

		// no active race -> total laps to go are shown and laps can always be updated
		// instantly
		if (flagStatus == Race.FlagStatus.PURPLE || flagStatus == Race.FlagStatus.NONE) {
			if (lapsToGo > 0) {
				textFieldLaps.setText(Integer.toString(lapsToGo));
			} else {
				textFieldLaps.setText("-");
			}
			// active race -> we need to show 1 lap less because athletes need to see it
			// before they cross the line
		} else {
			lapUpdateDelayTimer = new Timer(lapSwitchDelay * 1000,
					e -> textFieldLaps.setText(Integer.toString(lapsToGo > 0 ? lapsToGo - 1 : 0)));
			lapUpdateDelayTimer.setRepeats(false);
			lapUpdateDelayTimer.start();
		}
	}

	private void updateDisplayedLapsCompleted(int oldLaps, int newLaps) {
		if (lapUpdateDelayTimer != null) {
			lapUpdateDelayTimer.stop();
			textFieldLaps.setText(Integer.toString(oldLaps));
		}

		lapUpdateDelayTimer = new Timer(lapSwitchDelay * 1000, e -> {
			if (newLaps >= 0) {
				textFieldLaps.setText(Integer.toString(newLaps));
			} else {
				textFieldLaps.setText("-");
			}
		});
		lapUpdateDelayTimer.setRepeats(false);
		lapUpdateDelayTimer.start();
	}

	private void updateFlagColor(FlagStatus flagStatus) {
		Color color = switch (flagStatus) {
		case GREEN -> Color.GREEN;
		case YELLOW -> Color.YELLOW;
		case RED -> Color.RED;
		case FINISH -> Color.LIGHT_GRAY;
		case PURPLE -> new Color(98, 0, 255);
		default -> Color.BLACK;
		};
		textFieldFlag.setBackground(color);
	}
}
