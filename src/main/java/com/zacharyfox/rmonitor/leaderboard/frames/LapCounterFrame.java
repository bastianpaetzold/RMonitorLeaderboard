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
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.zacharyfox.rmonitor.client.RMonitorClient;
import com.zacharyfox.rmonitor.config.ConfigurationManager;
import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.entities.Race.FlagStatus;
import com.zacharyfox.rmonitor.utils.DurationUtil;

@SuppressWarnings("serial")
public class LapCounterFrame extends JFrame {

	private static final String FONT_NAME = "Tahoma";

	private static final String PROP_LAP_SWITCH_DELAY = "lapCounter.lapSwitchDelay";

	private JPanel contentPanel;
	private JTextField tfLaps;
	private JButton cancelButton;
	private int lastLapsToGo;
	private int lastLapsComplete;
	private Duration lastLapCountChangeTime;
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

	private static LapCounterFrame instance;

	public LapCounterFrame() {
		ConfigurationManager configManager = ConfigurationManager.getInstance();

		lapSwitchDelay = Integer.parseInt(configManager.getConfig(PROP_LAP_SWITCH_DELAY, "5"));
		boolean countLapsUp = false;
		lastLapsComplete = -1;

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
		tfLaps.setText("999");
		tfLaps.setBackground(Color.BLACK);
		contentPanel.add(tfLaps, BorderLayout.CENTER);
		tfLaps.setColumns(3);

		contentPanel.addComponentListener(new ResizeListener());

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
			RMonitorClient.getInstance().getRace().removePropertyChangeListener(propertyChangeListener);
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
		chckbxCountUpwards.setSelected(countLapsUp);
		infoPanel.add(chckbxCountUpwards);

		tfDelay.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (tfDelay.getText().matches("\\d+") && !Integer.toString(lapSwitchDelay).equals(tfDelay.getText())) {

					lapSwitchDelay = Integer.parseInt(tfDelay.getText());
					configManager.setConfig(PROP_LAP_SWITCH_DELAY, tfDelay.getText());
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// do nothing
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// do nothing
			}
		});

		lastLapCountChangeTime = Duration.ZERO;
		lastLapsToGo = 0;

		RMonitorClient.getInstance().getRace().addPropertyChangeListener(propertyChangeListener);
	}

	class ResizeListener extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {
			// Recalculate the variable you mentioned
			Font theFont = new Font(FONT_NAME, Font.PLAIN, (int) (e.getComponent().getHeight() * .85));// 400
			tfLaps.setFont(theFont);
		}
	}

	private void updateDisplay(PropertyChangeEvent evt) {
		String propertyName = evt.getPropertyName();
		Duration elapsedTime = RMonitorClient.getInstance().getRace().getElapsedTime();

		if (propertyName.equals(Race.PROPERTY_LAPS_COMPLETE)) {
			lastLapsComplete = ((int) evt.getNewValue());
			lastLapCountChangeTime = elapsedTime;
			updateDisplayedLapCount();
		}

		if (propertyName.equals(Race.PROPERTY_LAPS_TO_GO)) {
			lastLapsToGo = ((int) evt.getNewValue());
			lastLapCountChangeTime = elapsedTime;
			updateDisplayedLapCount();
		}

		if (propertyName.equals(Race.PROPERTY_ELAPSED_TIME)) {
			tfElapsedTime.setText(DurationUtil.format(elapsedTime));
			updateDisplayedLapCount();
		}

		if (propertyName.equals(Race.PROPERTY_FLAG_STATUS)) {
			FlagStatus flagStatus = (FlagStatus) evt.getNewValue();
			setFlagColor(flagStatus);

			// After switch to green we have to trigger the decrease of the lap counter
			if (flagStatus == Race.FlagStatus.GREEN) {
				lastLapCountChangeTime = elapsedTime;
			}
		}
	}

	private void updateDisplayedLapCount() {
		FlagStatus flagStatus = RMonitorClient.getInstance().getRace().getFlagStatus();
		Duration elapsedTime = RMonitorClient.getInstance().getRace().getElapsedTime();
		long secondsSinceLastLapCountUpdate = elapsedTime.minus(lastLapCountChangeTime).getSeconds();

		if (chckbxCountUpwards.isSelected()) {
			if (lastLapsComplete >= 0) {
				tfLaps.setText(Integer.toString(lastLapsComplete));
			} else {
				tfLaps.setText("-");
			}
			// For Purple the LapToGo are shown instantly
		} else if (flagStatus == Race.FlagStatus.PURPLE || flagStatus == Race.FlagStatus.NONE) {
			if (lastLapsToGo > 0) {
				tfLaps.setText(Integer.toString(lastLapsToGo));
			} else {
				tfLaps.setText("-");
			}
			// for other flags we show the Laps to Go only after the lapSwitchDelay
		} else if (secondsSinceLastLapCountUpdate > lapSwitchDelay) {
			if (lastLapsToGo > 0) {
				tfLaps.setText(Integer.toString(lastLapsToGo - 1));
			} else {
				tfLaps.setText("0");
			}
		}
	}

	private void setFlagColor(FlagStatus flagStatus) {
		Color color = switch (flagStatus) {
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
