package com.zacharyfox.rmonitor.utils;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Duration;

import com.zacharyfox.rmonitor.entities.Competitor;
import com.zacharyfox.rmonitor.entities.Race;

public class Estimator {

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	private int estimatedLapsAvg = 0;
	private int estimatedLapsBest = 0;
	private Duration estimatedTimeAvg = Duration.ZERO;
	private Duration estimatedTimeBest = Duration.ZERO;
	private int lapsComplete = 0;
	private int lapsToGo = 0;
	private int scheduledLaps = 0;

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

	public int getEstimatedLapsAvg() {
		return estimatedLapsAvg;
	}

	public int getEstimatedLapsBest() {
		return estimatedLapsBest;
	}

	public Duration getEstimatedTimeAvg() {
		return estimatedTimeAvg;
	}

	public Duration getEstimatedTimeBest() {
		return estimatedTimeBest;
	}

	public int getLapsComplete() {
		return lapsComplete;
	}

	public int getScheduledLaps() {
		return scheduledLaps;
	}

	public Duration getScheduledTime() {
		return scheduledTime;
	}

	public void update(Race race) {
		setScheduledTime(race.getScheduledTime());
		setScheduledLaps(race.getScheduledLaps());
		setLapsComplete(race.getLapsComplete());
		setLapsToGo(race.getLapsToGo());

		calculateEstimatedLapsByBest();
		calculateEstimatedLapsByAvg();
	}

	private void calculateEstimatedLapsByAvg() {
		int oldEstimatedLapsAvg = estimatedLapsAvg;
		Duration oldEstimatedTimeAvg = estimatedTimeAvg;
		Competitor competitor = Competitor.getByPosition(1);

		if (competitor == null) {
			estimatedLapsAvg = lapsToGo;
			return;
		}

		int laps = competitor.getLapsComplete();

		if (laps == 0) {
			estimatedLapsAvg = lapsToGo;
			return;
		}

		Duration time = competitor.getTotalTime();
		Duration avgLapTime = competitor.getAvgLap();

		do {
			time = time.plus(avgLapTime);
			laps++;
		} while (time.compareTo(scheduledTime) < 0);

		if (lapsToGo == 0 || laps < lapsToGo + competitor.getLapsComplete()) {
			estimatedLapsAvg = laps;
		} else {
			estimatedLapsAvg = scheduledLaps;
		}

		estimatedTimeAvg = time;

		changeSupport.firePropertyChange("estimatedLapsAvg", oldEstimatedLapsAvg, Integer.toString(estimatedLapsAvg));
		changeSupport.firePropertyChange("estimatedTimeAvg", oldEstimatedTimeAvg, estimatedTimeAvg);
	}

	private void calculateEstimatedLapsByBest() {
		int oldEstimatedLapsBest = estimatedLapsBest;
		Duration oldEstimatedTimeBest = estimatedTimeBest;
		Competitor competitor = Competitor.getByPosition(1);
		if (competitor == null) {
			this.estimatedLapsBest = lapsToGo;
			return;
		}

		int laps = competitor.getLapsComplete();
		if (laps == 0) {
			estimatedLapsBest = lapsToGo;
			return;
		}
		Duration time = competitor.getTotalTime();
		Duration bestLapTime = competitor.getBestLap();

		if (bestLapTime.isZero()) {
			estimatedLapsBest = lapsToGo;
			return;
		}

		do {
			time = time.plus(bestLapTime);
			laps++;
		} while (time.compareTo(scheduledTime) < 0);

		if (lapsToGo == 0 || laps < lapsToGo + competitor.getLapsComplete()) {
			estimatedLapsBest = laps;
		} else {
			estimatedLapsBest = scheduledLaps;
		}

		estimatedTimeBest = time;

		changeSupport.firePropertyChange("estimatedLapsBest", oldEstimatedLapsBest,
				Integer.toString(estimatedLapsBest));
		changeSupport.firePropertyChange("estimatedTimeBest", oldEstimatedTimeBest, estimatedTimeBest);
	}

	private void setLapsComplete(int lapsComplete) {
		int oldLapsComplete = this.lapsComplete;
		this.lapsComplete = lapsComplete;
		changeSupport.firePropertyChange("lapsComplete", oldLapsComplete, Integer.toString(lapsComplete));
	}

	private void setLapsToGo(int lapsToGo) {
		int oldLapsToGo = this.lapsToGo;
		this.lapsToGo = lapsToGo;
		changeSupport.firePropertyChange("lapsToGo", oldLapsToGo, Integer.toString(lapsToGo));
	}

	private void setScheduledLaps(int scheduledLaps) {
		int oldScheduledLaps = this.scheduledLaps;
		this.scheduledLaps = scheduledLaps;
		changeSupport.firePropertyChange("scheduledLaps", oldScheduledLaps, Integer.toString(scheduledLaps));
	}

	private void setScheduledTime(Duration scheduledTime) {
		Duration oldScheduledTime = this.scheduledTime;
		this.scheduledTime = scheduledTime;
		changeSupport.firePropertyChange("scheduledTime", oldScheduledTime, this.scheduledTime);
	}
}
