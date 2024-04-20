package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Duration;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.zacharyfox.rmonitor.entities.Competitor;
import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.entities.Race.FlagStatus;
import com.zacharyfox.rmonitor.entities.RaceManager;
import com.zacharyfox.rmonitor.utils.DurationUtil;

@SuppressWarnings("serial")
public class StartSignalFrame extends JFrame {

	private static final String FONT_NAME = "Tahoma";

	private JTextField textFieldRaceName;
	private JTextArea textFieldFlag;
	private JTextField textFieldRaceTime;

	public StartSignalFrame() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 698, 590);
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
		Race race = RaceManager.getInstance().getCurrentRace();
		getContentPane().setLayout(new BorderLayout());

		JPanel panelMain = new JPanel();
		panelMain.setBorder(new EmptyBorder(5, 5, 5, 5));
		panelMain.setLayout(new BorderLayout(0, 0));
		getContentPane().add(panelMain, BorderLayout.CENTER);

		textFieldRaceName = new JTextField();
		textFieldRaceName.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldRaceName.setBackground(Color.BLACK);
		textFieldRaceName.setForeground(Color.WHITE);
		textFieldRaceName.setFont(new Font(FONT_NAME, Font.PLAIN, 80));
		textFieldRaceName.setColumns(50);
		textFieldRaceName.setText(race.getName());
		panelMain.add(textFieldRaceName, BorderLayout.NORTH);

		textFieldFlag = new JTextArea();
		textFieldFlag.setBackground(Color.BLACK);
		textFieldFlag.setForeground(Color.WHITE);
		textFieldFlag.setFont(new Font(FONT_NAME, Font.PLAIN, 80));
		textFieldFlag.setColumns(20);
		textFieldFlag.setRows(5);
		textFieldFlag.setEditable(false);
		textFieldFlag.setLineWrap(true);
		textFieldFlag.setWrapStyleWord(true);
		panelMain.add(textFieldFlag, BorderLayout.CENTER);

		textFieldRaceTime = new JTextField();
		textFieldRaceTime.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldRaceTime.setBackground(Color.BLACK);
		textFieldRaceTime.setForeground(Color.WHITE);
		textFieldRaceTime.setFont(new Font(FONT_NAME, Font.PLAIN, 100));
		textFieldRaceTime.setColumns(10);
		textFieldRaceTime.setText("00:00:00");
		panelMain.add(textFieldRaceTime, BorderLayout.SOUTH);

		updateFlagColor(race.getFlagStatus());
	}

	private void updateDisplay(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case Race.PROPERTY_RACE_NAME:
			textFieldRaceName.setText((String) evt.getNewValue());
			break;

		case Race.PROPERTY_ELAPSED_TIME:
			textFieldRaceTime.setText(DurationUtil.format((Duration) evt.getNewValue()));
			break;

		case Race.PROPERTY_FLAG_STATUS:
			updateFlagColor((FlagStatus) evt.getNewValue());
			textFieldFlag.setText("");
			break;

		case Race.PROPERTY_COMPETITORS_VERSION:
			Race race = RaceManager.getInstance().getCurrentRace();
			if (race.getFlagStatus() == Race.FlagStatus.PURPLE) {
				textFieldFlag.setText(createRegNumberString(race));
			}
			break;

		default:
			break;
		}
	}

	private void updateFlagColor(FlagStatus flagStatus) {
		Color color = switch (flagStatus) {
		case GREEN -> Color.GREEN;
		case YELLOW -> Color.YELLOW;
		case RED -> Color.RED;
		case FINISH -> Color.LIGHT_GRAY;
		case PURPLE -> new Color(98, 0, 255);
		case NONE -> Color.BLACK;
		default -> Color.BLACK;
		};
		textFieldFlag.setBackground(color);
	}

	private String createRegNumberString(Race race) {
		return race.getCompetitors().stream().map(Competitor::getRegNumber).collect(Collectors.joining(", "));
	}
}
