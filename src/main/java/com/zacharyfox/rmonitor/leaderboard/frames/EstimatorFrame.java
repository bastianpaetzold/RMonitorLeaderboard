package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Duration;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.zacharyfox.rmonitor.entities.Competitor;
import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.entities.RaceClass;
import com.zacharyfox.rmonitor.entities.RaceManager;
import com.zacharyfox.rmonitor.utils.DurationUtil;
import com.zacharyfox.rmonitor.utils.Estimator;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class EstimatorFrame extends JFrame {

	private JLabel labelScheduledLapsValue;
	private JLabel labelScheduledTimeValue;
	private JLabel labelLapsCompletedValue;
	private JLabel labelEstimatedLapsByAvgValue;
	private JLabel labelEstimatedTimeByAvgValue;
	private JLabel labelEstimatedLapsByBestValue;
	private JLabel labelEstimatedTimeByBestValue;
	private JLabel labelTopThreeValue;

	private static EstimatorFrame instance;

	private EstimatorFrame() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 200);

		initContent();

		PropertyChangeListener listener = e -> SwingUtilities.invokeLater(() -> updateDisplay(e));
		Estimator.getInstance().addPropertyChangeListener(listener);
		RaceManager.getInstance().addPropertyChangeListener(listener);
	}

	private void initContent() {
		Estimator estimator = Estimator.getInstance();
		Race race = RaceManager.getInstance().getCurrentRace();

		getContentPane().setLayout(new MigLayout("", "[grow][]", "[100.00,grow][][][][][]"));

		JLabel labelScheduledLaps = new JLabel("Scheduled Laps:");
		getContentPane().add(labelScheduledLaps, "cell 0 0,alignx left,aligny bottom");
		labelScheduledLapsValue = new JLabel(Integer.toString(race.getScheduledLaps()));
		getContentPane().add(labelScheduledLapsValue, "cell 1 0,alignx left,aligny bottom");

		JLabel labelScheduledTime = new JLabel("Scheduled Time:");
		getContentPane().add(labelScheduledTime, "cell 0 1,alignx left,aligny bottom");
		labelScheduledTimeValue = new JLabel(DurationUtil.format(race.getScheduledTime()));
		getContentPane().add(labelScheduledTimeValue, "cell 1 1,alignx left,aligny bottom");

		JLabel labelLapsCompleted = new JLabel("Laps Complete:");
		getContentPane().add(labelLapsCompleted, "cell 0 2,alignx left,aligny bottom");
		labelLapsCompletedValue = new JLabel(Integer.toString(race.getLapsComplete()));
		getContentPane().add(labelLapsCompletedValue, "cell 1 2,alignx left,aligny bottom");

		JLabel labelEstimatedLapsBest = new JLabel("Estimated Laps (by best):");
		getContentPane().add(labelEstimatedLapsBest, "cell 0 3,alignx left,aligny bottom");
		labelEstimatedLapsByBestValue = new JLabel(Integer.toString(estimator.getEstimatedLapsByBest()));
		getContentPane().add(labelEstimatedLapsByBestValue, "cell 1 3,alignx left,aligny bottom");
		labelEstimatedTimeByBestValue = new JLabel(" @ " + DurationUtil.format(estimator.getEstimatedTimeByBest()));
		getContentPane().add(labelEstimatedTimeByBestValue, "cell 1 3,alignx left,aligny bottom");

		JLabel labelEstimatedLapsAvg = new JLabel("Estimated Laps (by average):");
		getContentPane().add(labelEstimatedLapsAvg, "cell 0 4,alignx left,aligny bottom");
		labelEstimatedLapsByAvgValue = new JLabel(Integer.toString(estimator.getEstimatedLapsByAvg()));
		getContentPane().add(labelEstimatedLapsByAvgValue, "cell 1 4,alignx left,aligny bottom");
		labelEstimatedTimeByAvgValue = new JLabel(" @ " + DurationUtil.format(estimator.getEstimatedTimeByAvg()));
		getContentPane().add(labelEstimatedTimeByAvgValue, "cell 1 4,alignx left,aligny bottom");

		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBorder(null);
		getContentPane().add(separator, "cell 0 5 2 1,growx,aligny top");

		JLabel labelTopThree = new JLabel("Top Three:");
		getContentPane().add(labelTopThree, "cell 0 6,alignx left,aligny top");
		labelTopThreeValue = new JLabel(createTopThreeText());
		getContentPane().add(labelTopThreeValue, "cell 1 6,alignx left,aligny top");
	}

	private String createTopThreeText() {
		StringBuilder builder = new StringBuilder();
		builder.append("<html><body>");

		Race race = RaceManager.getInstance().getCurrentRace();
		for (int i = 1; i <= 3; i++) {
			Competitor comp = race.getCompetitorByPosition(i);
			if (comp != null) {
				builder.append(comp.getNumber());
				builder.append(" ");
				builder.append(RaceClass.getClassName(comp.getClassId()));
				builder.append("<br />");
			}
		}

		builder.append("</body></html>");

		return builder.toString();
	}

	private void updateDisplay(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case Estimator.PROPERTY_ESTIMATED_LAPS_BY_AVG:
			labelEstimatedLapsByAvgValue.setText((String) evt.getNewValue());
			break;

		case Estimator.PROPERTY_ESTIMATED_LAPS_BY_BEST:
			labelEstimatedLapsByBestValue.setText((String) evt.getNewValue());
			break;

		case Estimator.PROPERTY_ESTIMATED_TIME_BY_AVG:
			labelEstimatedTimeByAvgValue.setText(" @ " + DurationUtil.format((Duration) evt.getNewValue()));
			break;

		case Estimator.PROPERTY_ESTIMATED_TIME_BY_BEST:
			labelEstimatedTimeByBestValue.setText(" @ " + DurationUtil.format((Duration) evt.getNewValue()));
			break;

		case Race.PROPERTY_SCHEDULED_LAPS:
			labelScheduledLapsValue.setText((String) evt.getNewValue());
			break;

		case Race.PROPERTY_SCHEDULED_TIME:
			labelScheduledTimeValue.setText(DurationUtil.format((Duration) evt.getNewValue()));
			break;

		case Race.PROPERTY_LAPS_COMPLETE:
			labelLapsCompletedValue.setText(evt.getNewValue().toString());
			break;

		default:
			break;
		}

		labelTopThreeValue.setText(createTopThreeText());
	}

	public static EstimatorFrame getInstance() {
		if (instance == null) {
			instance = new EstimatorFrame();
		}

		return instance;
	}
}
