package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.zacharyfox.rmonitor.client.RMonitorClient;
import com.zacharyfox.rmonitor.config.ConfigurationManager;
import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.entities.Race.FlagState;
import com.zacharyfox.rmonitor.utils.DurationUtil;

@SuppressWarnings("serial")
public class LapCounterFrame extends JFrame {

	private static final String PROP_LAP_SWITCH_DELAY = "lapCounter.lapSwitchDelay";

	private static LapCounterFrame instance;

	static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

	private JPanel contentPanel;
	private JTextField tfLaps;

	private JButton cancelButton;
	private int lastLapCount;
	private int lastLapsComplete;
	private Duration lastLapCountChangeTime;
	private int lapSwitchDelay;
	private PropertyChangeListener propertyChangeListener = this::updateDisplay;
	private JTextField tfElapsedTime;
	private JTextField tfFlag;
	private JPanel infoPanel;
	private JLabel lblElapsedTime;
	private JLabel lblDelay;
	private JTextField tfDelay;
	private JCheckBox chckbxCountUpwards;

	/**
	 * Create the dialog.
	 */
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
		Font theFont = new Font("Tahoma", Font.PLAIN, (int) (getHeight() * .85));// 400
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
		cancelButton.setFont(new Font("Tahoma", Font.BOLD, 11));
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
		lastLapCount = 0;

		RMonitorClient.getInstance().getRace().addPropertyChangeListener(propertyChangeListener);
	}

	class ResizeListener extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {
			// Recalculate the variable you mentioned
			Font theFont = new Font("Tahoma", Font.PLAIN, (int) (e.getComponent().getHeight() * .85));// 400
			tfLaps.setFont(theFont);
		}
	}

	private void updateDisplay(PropertyChangeEvent evt) {
		Duration elapsedTime = RMonitorClient.getInstance().getRace().getElapsedTime();
		if (evt.getPropertyName().equals("lapsToGo")) {
			lastLapCount = ((int) evt.getNewValue());
			lastLapCountChangeTime = elapsedTime;
		}

		if (evt.getPropertyName().equals("lapsComplete")) {
			lastLapsComplete = ((int) evt.getNewValue());
			lastLapCountChangeTime = elapsedTime;
		}

		if (evt.getPropertyName().equals("lapsToGo") || evt.getPropertyName().equals("elapsedTime")
				|| evt.getPropertyName().equals("lapsComplete")) {
			tfElapsedTime.setText(DurationUtil.format(elapsedTime));
			long secondsSinceLastLapCountUpdate = elapsedTime.minus(lastLapCountChangeTime).getSeconds();

			Race.FlagState currentFlagState = RMonitorClient.getInstance().getRace().getCurrentFlagState();

			// Display lastLapsComplete or lastLapCount dependent on checkbox
			if (chckbxCountUpwards.isSelected()) {
				if (lastLapsComplete >= 0) {
					tfLaps.setText(Integer.toString(lastLapsComplete));
				} else {
					tfLaps.setText("-");
				}
			} else // For Purple the LapToGo are shown instantly
			if (currentFlagState == Race.FlagState.PURPLE || currentFlagState == Race.FlagState.NONE) {
				if (lastLapCount > 0) {
					tfLaps.setText(Integer.toString(lastLapCount));
				} else {
					tfLaps.setText("-");
				}
				// for other flags we show the Laps to GO only after the lapSwitchDelay
			} else if (secondsSinceLastLapCountUpdate > lapSwitchDelay) {

				if (lastLapCount > 0) {
					tfLaps.setText(Integer.toString(lastLapCount - 1));
				} else {
					tfLaps.setText("0");
				}
			}
		}

		if (evt.getPropertyName().equals("currentFlagState")) {
			setFlagColor((FlagState) evt.getNewValue());
			tfElapsedTime.setText(DurationUtil.format(elapsedTime));

			// After switch to green we have to trigger the decrease the lap counter
			if ((FlagState) evt.getNewValue() == Race.FlagState.GREEN) {
				lastLapCountChangeTime = elapsedTime;
			}
		}
	}

	private void setFlagColor(Race.FlagState flagState) {
		switch (flagState) {
		case RED:
			tfFlag.setBackground(Color.red);
			break;

		case YELLOW:
			tfFlag.setBackground(Color.YELLOW);
			break;

		case GREEN:
			tfFlag.setBackground(Color.GREEN);
			break;

		case FINISH:
			tfFlag.setBackground(Color.LIGHT_GRAY);
			break;

		case PURPLE:
			tfFlag.setBackground(new Color(98, 0, 255));
			break;

		default:
			tfFlag.setBackground(Color.BLACK);
		}

	}

	public static LapCounterFrame getInstance() {
		if (instance == null || !instance.isDisplayable()) {
			instance = new LapCounterFrame();
		}

		return instance;
	}
}
