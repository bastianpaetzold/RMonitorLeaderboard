package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.time.Duration;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;

import com.zacharyfox.rmonitor.entities.Competitor;
import com.zacharyfox.rmonitor.entities.RaceClass;
import com.zacharyfox.rmonitor.utils.DurationUtil;
import com.zacharyfox.rmonitor.utils.Estimator;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class EstimatorFrame extends JFrame {

	private final JLabel estimatedLapsAvg;
	private final JLabel estimatedLapsBest;
	private final JLabel estimatedTimeAvg;
	private final JLabel estimatedTimeBest;
	private final JLabel lapsComplete;
	private final JLabel lblEstimatedLapsAvg;
	private final JLabel lblEstimatedLapsBest;
	private final JLabel lblLapsComplete;
	private final JLabel lblScheduledLaps;
	private final JLabel lblScheduledTime;
	private final JLabel lblTopThree;
	private final JLabel scheduledLaps;
	private final JLabel scheduledTime;
	private final JSeparator separator = new JSeparator();
	private final JLabel topThree;

	private static EstimatorFrame instance;

	private EstimatorFrame() {
		Estimator estimator = Estimator.getInstance();

		getContentPane().setLayout(new MigLayout("", "[grow][]", "[100.00,grow][][][][][]"));

		lblScheduledLaps = new JLabel("Scheduled Laps:");
		getContentPane().add(lblScheduledLaps, "cell 0 0,alignx left,aligny bottom");
		scheduledLaps = new JLabel("");
		getContentPane().add(scheduledLaps, "cell 1 0,alignx left,aligny bottom");

		lblScheduledTime = new JLabel("Scheduled Time:");
		getContentPane().add(lblScheduledTime, "cell 0 1,alignx left,aligny bottom");
		scheduledTime = new JLabel("");
		getContentPane().add(scheduledTime, "cell 1 1,alignx left,aligny bottom");

		lblLapsComplete = new JLabel("Laps Complete:");
		getContentPane().add(lblLapsComplete, "cell 0 2,alignx left,aligny bottom");
		lapsComplete = new JLabel("");
		getContentPane().add(lapsComplete, "cell 1 2,alignx left,aligny bottom");

		lblEstimatedLapsBest = new JLabel("Estimated Laps (by best):");
		getContentPane().add(lblEstimatedLapsBest, "cell 0 3,alignx left,aligny bottom");
		estimatedLapsBest = new JLabel("");
		getContentPane().add(estimatedLapsBest, "cell 1 3,alignx left,aligny bottom");
		estimatedTimeBest = new JLabel("");
		getContentPane().add(estimatedTimeBest, "cell 1 3,alignx left,aligny bottom");

		lblEstimatedLapsAvg = new JLabel("Estimated Laps (by average):");
		getContentPane().add(lblEstimatedLapsAvg, "cell 0 4,alignx left,aligny bottom");
		estimatedLapsAvg = new JLabel("");
		getContentPane().add(estimatedLapsAvg, "cell 1 4,alignx left,aligny bottom");
		estimatedTimeAvg = new JLabel("");
		getContentPane().add(estimatedTimeAvg, "cell 1 4,alignx left,aligny bottom");

		separator.setForeground(Color.BLACK);
		separator.setBorder(null);
		getContentPane().add(separator, "cell 0 5 2 1,growx,aligny top");

		lblTopThree = new JLabel("Top Three:");
		getContentPane().add(lblTopThree, "cell 0 6,alignx left,aligny top");
		topThree = new JLabel("");
		getContentPane().add(topThree, "cell 1 6,alignx left,aligny top");

		setBounds(100, 100, 450, 200);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		estimator.addPropertyChangeListener(this::updateDisplay);

		estimatedLapsBest.setText(Integer.toString(estimator.getEstimatedLapsBest()));
		estimatedTimeBest.setText(" @ " + DurationUtil.format(estimator.getEstimatedTimeBest()));
		estimatedLapsAvg.setText(Integer.toString(estimator.getEstimatedLapsAvg()));
		estimatedTimeAvg.setText(" @ " + DurationUtil.format(estimator.getEstimatedTimeAvg()));
		scheduledLaps.setText(Integer.toString(estimator.getScheduledLaps()));
		scheduledTime.setText(DurationUtil.format(estimator.getScheduledTime()));
		lapsComplete.setText(Integer.toString(estimator.getLapsComplete()));
		topThree.setText(getTopThreeText());
	}

	private String getTopThreeText() {
		StringBuilder builder = new StringBuilder();
		builder.append("<html><body>");

		for (int i = 1; i <= 3; i++) {
			Competitor comp = Competitor.getByPosition(i);
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
		if (evt.getPropertyName().equals("estimatedLapsBest")) {
			estimatedLapsBest.setText((String) evt.getNewValue());
		}

		if (evt.getPropertyName().equals("estimatedTimeBest")) {
			estimatedTimeBest.setText(" @ " + DurationUtil.format((Duration) evt.getNewValue()));
		}

		if (evt.getPropertyName().equals("scheduledLaps")) {
			scheduledLaps.setText((String) evt.getNewValue());
		}

		if (evt.getPropertyName().equals("estimatedLapsAvg")) {
			estimatedLapsAvg.setText((String) evt.getNewValue());
		}

		if (evt.getPropertyName().equals("estimatedTimeAvg")) {
			estimatedTimeAvg.setText(" @ " + DurationUtil.format((Duration) evt.getNewValue()));
		}

		if (evt.getPropertyName().equals("scheduledTime")) {
			scheduledTime.setText(DurationUtil.format((Duration) evt.getNewValue()));
		}

		if (evt.getPropertyName().equals("lapsComplete")) {
			lapsComplete.setText(evt.getNewValue().toString());
		}

		topThree.setText(this.getTopThreeText());
	}

	public static EstimatorFrame getInstance() {
		if (instance == null) {
			instance = new EstimatorFrame();
		}

		return instance;
	}
}
