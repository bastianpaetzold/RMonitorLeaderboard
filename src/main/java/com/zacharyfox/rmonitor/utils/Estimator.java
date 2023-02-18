package com.zacharyfox.rmonitor.utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Duration;

import com.zacharyfox.rmonitor.entities.Competitor;
import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.entities.RaceManager;

public class Estimator {

	public static final String PROPERTY_ESTIMATED_LAPS_BY_AVG = "estimatedLapsByAvg";
	public static final String PROPERTY_ESTIMATED_LAPS_BY_BEST = "estimatedLapsByBest";
	public static final String PROPERTY_ESTIMATED_TIME_BY_BEST = "estimatedTimeByBest";
	public static final String PROPERTY_ESTIMATED_TIME_BY_AVG = "estimatedTimeBAvg";

	private static final RaceManager raceManager = RaceManager.getInstance();

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	private int estimatedLapsByAvg = 0;
	private int estimatedLapsByBest = 0;
	private Duration estimatedTimeByAvg = Duration.ZERO;
	private Duration estimatedTimeByBest = Duration.ZERO;

	private Duration scheduledTime = Duration.ZERO;

	private static Estimator instance;

	public static Estimator getInstance() {
		if (instance == null) {
			instance = new Estimator();
		}

		return instance;
	}

	private Estimator() {
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(l);
	}

	public int getEstimatedLapsByAvg() {
		return estimatedLapsByAvg;
	}

	public int getEstimatedLapsByBest() {
		return estimatedLapsByBest;
	}

	public Duration getEstimatedTimeByAvg() {
		return estimatedTimeByAvg;
	}

	public Duration getEstimatedTimeByBest() {
		return estimatedTimeByBest;
	}

	public void update() {
		calculateEstimatedLapsByBest();
		calculateEstimatedLapsByAvg();
	}

	private void calculateEstimatedLapsByAvg() {
		Race race = raceManager.getCurrentRace();
		Competitor competitor = race.getCompetitorByPosition(1);

		if (race.getTimeToGo().isZero() || competitor == null || competitor.getLapsComplete() == 0) {
			setEstimatedLapsByAvg(race.getScheduledLaps());
		} else {
			Duration avgLapTime = competitor.getAvgLap();

			if (avgLapTime.isZero()) {
				setEstimatedLapsByAvg(race.getScheduledLaps());
			} else {
				Duration time = competitor.getTotalTime();
				int laps = competitor.getLapsComplete();

				do {
					time = time.plus(avgLapTime);
					laps++;
				} while (time.compareTo(scheduledTime) < 0);

				int lapsToGo = race.getLapsToGo();
				if (lapsToGo == 0 || laps < competitor.getLapsComplete() + lapsToGo) {
					setEstimatedLapsByAvg(laps);
				} else {
					setEstimatedLapsByAvg(race.getScheduledLaps());
				}

				setEstimatedTimeByAvg(time);

			}
		}
	}

	private void calculateEstimatedLapsByBest() {
		Race race = raceManager.getCurrentRace();
		Competitor competitor = race.getCompetitorByPosition(1);

		if (race.getTimeToGo().isZero() || competitor == null || competitor.getLapsComplete() == 0) {
			setEstimatedLapsByBest(race.getScheduledLaps());
		} else {
			Duration bestLapTime = competitor.getBestLap();

			if (bestLapTime.isZero()) {
				setEstimatedLapsByBest(race.getScheduledLaps());
			} else {
				Duration time = competitor.getTotalTime();
				int laps = competitor.getLapsComplete();

				do {
					time = time.plus(bestLapTime);
					laps++;
				} while (time.compareTo(scheduledTime) < 0);

				int lapsToGo = race.getLapsToGo();
				if (lapsToGo == 0 || laps < competitor.getLapsComplete() + lapsToGo) {
					setEstimatedLapsByBest(laps);
				} else {
					setEstimatedLapsByBest(race.getScheduledLaps());
				}

				setEstimatedTimeByBest(time);
			}
		}
	}

	private void setEstimatedLapsByAvg(int newEstimatedLapsByAvg) {
		int oldEstimatedLapsByAvg = estimatedLapsByAvg;
		estimatedLapsByAvg = newEstimatedLapsByAvg;

		changeSupport.firePropertyChange(PROPERTY_ESTIMATED_LAPS_BY_AVG, oldEstimatedLapsByAvg,
				Integer.toString(newEstimatedLapsByAvg));
	}

	private void setEstimatedLapsByBest(int newEstimatedLapsByBest) {
		int oldEstimatedLapsByBest = estimatedLapsByBest;
		estimatedLapsByBest = newEstimatedLapsByBest;

		changeSupport.firePropertyChange(PROPERTY_ESTIMATED_LAPS_BY_BEST, oldEstimatedLapsByBest,
				Integer.toString(newEstimatedLapsByBest));
	}

	private void setEstimatedTimeByAvg(Duration newEstimatedTimeByAvg) {
		Duration oldEstimatedTimeByAvg = estimatedTimeByAvg;
		estimatedTimeByAvg = newEstimatedTimeByAvg;

		changeSupport.firePropertyChange(PROPERTY_ESTIMATED_TIME_BY_AVG, oldEstimatedTimeByAvg, newEstimatedTimeByAvg);
	}

	private void setEstimatedTimeByBest(Duration newEstimatedTimeByBest) {
		Duration oldEstimatedTimeByBest = estimatedTimeByBest;
		estimatedTimeByBest = newEstimatedTimeByBest;

		changeSupport.firePropertyChange(PROPERTY_ESTIMATED_TIME_BY_BEST, oldEstimatedTimeByBest,
				newEstimatedTimeByBest);
	}
}
