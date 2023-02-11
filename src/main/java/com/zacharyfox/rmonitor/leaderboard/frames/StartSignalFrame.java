package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.zacharyfox.rmonitor.client.RMonitorClient;
import com.zacharyfox.rmonitor.entities.Competitor;
import com.zacharyfox.rmonitor.entities.Competitors;
import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.entities.Race.FlagStatus;
import com.zacharyfox.rmonitor.utils.DurationUtil;

@SuppressWarnings("serial")
public class StartSignalFrame extends JFrame {

	private static StartSignalFrame instance;

	static GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

	private JPanel contentPanel;
	private JTextField tfRaceName;
	private JTextArea tfFlag;
	private JTextField tfRaceTime;

	private JButton cancelButton;
	private PropertyChangeListener propertyChangeListener = e -> SwingUtilities.invokeLater(() -> updateDisplay(e));

	/**
	 * Create the dialog.
	 */
	public StartSignalFrame() {
		Race race = RMonitorClient.getInstance().getRace();

		setBounds(100, 100, 698, 590);
		setExtendedState(Frame.MAXIMIZED_BOTH);

		contentPanel = new JPanel();
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));

		tfRaceName = new JTextField();
		tfRaceName.setHorizontalAlignment(SwingConstants.CENTER);
		tfRaceName.setForeground(Color.WHITE);
		tfRaceName.setBackground(Color.BLACK);
		tfRaceName.setFont(new Font("Tahoma", Font.PLAIN, 80));
		tfRaceName.setText(race.getName());
		tfRaceName.setColumns(50);
		contentPanel.add(tfRaceName, BorderLayout.NORTH);

		tfFlag = new JTextArea();
		tfFlag.setEditable(false);
		tfFlag.setWrapStyleWord(true);
		tfFlag.setLineWrap(true);
		tfFlag.setForeground(Color.WHITE);
		tfFlag.setFont(new Font("Tahoma", Font.PLAIN, 80));
		tfFlag.setRows(5);
		tfFlag.setBackground(Color.BLACK);
		tfFlag.setColumns(20);
		contentPanel.add(tfFlag, BorderLayout.CENTER);

		setFlagColor(race.getFlagStatus());

		tfRaceTime = new JTextField();
		tfRaceTime.setText("00:00:00");
		tfRaceTime.setHorizontalAlignment(SwingConstants.CENTER);
		tfRaceTime.setBackground(Color.BLACK);
		tfRaceTime.setForeground(Color.WHITE);
		tfRaceTime.setFont(new Font("Tahoma", Font.PLAIN, 100));
		tfRaceTime.setColumns(10);
		contentPanel.add(tfRaceTime, BorderLayout.SOUTH);

		JPanel buttonPane = new JPanel();
		buttonPane.setBackground(Color.BLACK);
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		cancelButton = new JButton("X");
		cancelButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		cancelButton.setMnemonic('x');
		cancelButton.setBackground(Color.BLACK);
		cancelButton.setForeground(Color.RED);
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(e -> {
			instance.setVisible(false);
			race.removePropertyChangeListener(propertyChangeListener);
			instance.dispose();
		});

		buttonPane.add(cancelButton);

		race.addPropertyChangeListener(propertyChangeListener);
	}

	private void updateDisplay(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case Race.PROPERTY_RACE_NAME:
			tfRaceName.setText((String) evt.getNewValue());
			break;

		case Race.PROPERTY_ELAPSED_TIME:
			tfRaceTime.setText(DurationUtil.format((Duration) evt.getNewValue()));
			break;

		case Race.PROPERTY_FLAG_STATUS:
			setFlagColor((FlagStatus) evt.getNewValue());
			tfFlag.setText("");
			break;

		case Race.PROPERTY_COMPETITORS_VERSION:
			if (RMonitorClient.getInstance().getRace().getFlagStatus() == Race.FlagStatus.PURPLE) {
				tfFlag.setText(createRegNumberString(Competitors.getCompetitors()));
			}
			break;

		default:
			break;
		}
	}

	private String createRegNumberString(List<Competitor> competitors) {
		return competitors.stream().map(Competitor::getRegNumber).collect(Collectors.joining(", "));
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

	public static StartSignalFrame getInstance() {
		if (instance == null || !instance.isDisplayable()) {
			instance = new StartSignalFrame();
		}

		return instance;
	}
}
