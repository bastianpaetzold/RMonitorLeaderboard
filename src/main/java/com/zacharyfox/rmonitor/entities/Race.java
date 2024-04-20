package com.zacharyfox.rmonitor.entities;

import java.beans.PropertyChangeSupport;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.zacharyfox.rmonitor.utils.DurationUtil;

public class Race {

	public static final String PROPERTY_RACE_ID = "raceID";
	public static final String PROPERTY_RACE_NAME = "raceName";
	public static final String PROPERTY_TRACK_NAME = "trackName";
	public static final String PROPERTY_TRACK_LENGTH = "trackLength";
	public static final String PROPERTY_FLAG_STATUS = "flagStatus";
	public static final String PROPERTY_LAPS_COMPLETE = "lapsComplete";
	public static final String PROPERTY_LAPS_TO_GO = "lapsToGo";
	public static final String PROPERTY_SCHEDULED_LAPS = "scheduledLaps";
	public static final String PROPERTY_TIME_OF_DAY = "timeOfDay";
	public static final String PROPERTY_ELAPSED_TIME = "elapsedTime";
	public static final String PROPERTY_SCHEDULED_TIME = "scheduledTime";
	public static final String PROPERTY_TIME_TO_GO = "timeToGo";
	public static final String PROPERTY_COMPETITORS_VERSION = "competitorsVersion";

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private int id;
	private String name;
	private String trackName;
	private Float trackLength;
	private FlagStatus flagStatus;
	private int lapsComplete;
	private int lapsToGo;
	private int scheduledLaps;
	private Duration timeOfDay;
	private Duration elapsedTime;
	private Duration timeToGo;
	private Duration scheduledTime;
	private Map<String, Competitor> competitorMap;
	private int competitorsVersion;

	public enum FlagStatus {
		PURPLE, GREEN, YELLOW, RED, FINISH, NONE
	}

	Race() {
		id = -1;
		name = "";
		trackName = "";
		trackLength = 0.0F;
		flagStatus = FlagStatus.NONE;
		lapsComplete = 0;
		lapsToGo = 0;
		scheduledLaps = 0;
		timeOfDay = Duration.ZERO;
		scheduledTime = Duration.ZERO;
		elapsedTime = Duration.ZERO;
		timeToGo = Duration.ZERO;
		competitorsVersion = 0;
		competitorMap = new HashMap<>();
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTrackName() {
		return trackName;
	}

	public Float getTrackLength() {
		return trackLength;
	}

	public FlagStatus getFlagStatus() {
		return flagStatus;
	}

	public int getLapsComplete() {
		return lapsComplete;
	}

	public int getLapsToGo() {
		return lapsToGo;
	}

	public Duration getTimeOfDay() {
		return timeOfDay;
	}

	public int getScheduledLaps() {
		return scheduledLaps;
	}

	public Duration getElapsedTime() {
		return elapsedTime;
	}

	public Duration getTimeToGo() {
		return timeToGo;
	}

	public int getCompetitorsVersion() {
		return competitorsVersion;
	}

	public Duration getScheduledTime() {
		return scheduledTime;
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

	/**
	 * @return an unmodifiable copy of the competitors
	 */
	public List<Competitor> getCompetitors() {
		synchronized (competitorMap) {
			return Collections.unmodifiableList(new ArrayList<>(competitorMap.values()));
		}
	}

	Competitor getOrCreateCompetitor(String regNumber) {
		synchronized (competitorMap) {
			return competitorMap.computeIfAbsent(regNumber, k -> new Competitor());
		}
	}

	public Competitor getCompetitor(String regNumber) {
		synchronized (competitorMap) {
			return competitorMap.get(regNumber);
		}
	}

	public Competitor getCompetitorByPosition(int position) {
		synchronized (competitorMap) {
			// @formatter:off
			return competitorMap.values().stream()
					.filter(c -> c.getPosition() == position)
					.findFirst()
					.orElse(null);
			// @formatter:on
		}
	}

	public Duration getFastestLap() {
		synchronized (competitorMap) {
			// @formatter:off
			return competitorMap.values().stream()
					.map(Competitor::getBestLap)
					.filter(Predicate.not(Duration::isZero))
					.sorted()
					.findFirst()
					.orElse(Duration.ZERO);
			// @formatter:on
		}
	}

	public long calcPositionInClass(Competitor competitor) {
		synchronized (competitorMap) {
			// @formatter:off
			return competitorMap.values().stream()
					.filter(c -> c.getClassId() == competitor.getClassId() && c.getPosition() < competitor.getPosition())
					.count() + 1;
			// @formatter:on
		}
	}

	void setId(int id) {
		int oldId = this.id;
		this.id = id;
		propertyChangeSupport.firePropertyChange(PROPERTY_RACE_ID, oldId, this.id);
	}

	void setName(String name) {
		String oldName = this.name;
		this.name = name;
		propertyChangeSupport.firePropertyChange(PROPERTY_RACE_NAME, oldName, this.name);
	}

	void setTrackName(String trackName) {
		String oldTrackName = this.trackName;
		this.trackName = trackName;
		propertyChangeSupport.firePropertyChange(PROPERTY_TRACK_NAME, oldTrackName, this.trackName);
	}

	void setTrackLength(Float trackLength) {
		Float oldTrackLength = this.trackLength;
		this.trackLength = trackLength;
		propertyChangeSupport.firePropertyChange(PROPERTY_TRACK_LENGTH, oldTrackLength, this.trackLength);
	}

	void setFlagStatus(String flagStatus) {
		FlagStatus oldFlagStatus = this.flagStatus;
		this.flagStatus = convertFlagStatus(flagStatus);
		propertyChangeSupport.firePropertyChange(PROPERTY_FLAG_STATUS, oldFlagStatus, this.flagStatus);
	}

	void setLapsComplete(int lapsComplete) {
		int oldLapsComplete = this.lapsComplete;
		this.lapsComplete = lapsComplete;
		propertyChangeSupport.firePropertyChange(PROPERTY_LAPS_COMPLETE, oldLapsComplete, this.lapsComplete);
	}

	void setLapsToGo(int lapsToGo) {
		int oldLapsToGo = this.lapsToGo;
		this.lapsToGo = lapsToGo;
		propertyChangeSupport.firePropertyChange(PROPERTY_LAPS_TO_GO, oldLapsToGo, this.lapsToGo);
	}

	void setScheduledLaps(int scheduledLaps) {
		int oldScheduledLaps = scheduledLaps;
		this.scheduledLaps = scheduledLaps;
		propertyChangeSupport.firePropertyChange(PROPERTY_SCHEDULED_LAPS, oldScheduledLaps, this.scheduledLaps);
	}

	void setTimeOfDay(Duration timeOfDay) {
		Duration oldTimeOfDay = this.timeOfDay;
		this.timeOfDay = timeOfDay;
		propertyChangeSupport.firePropertyChange(PROPERTY_TIME_OF_DAY, oldTimeOfDay, this.timeOfDay);
	}

	void setElapsedTime(Duration elapsedTime) {
		Duration oldElapsedTime = this.elapsedTime;
		this.elapsedTime = elapsedTime;
		propertyChangeSupport.firePropertyChange(PROPERTY_ELAPSED_TIME, oldElapsedTime, this.elapsedTime);
	}

	void setTimeToGo(Duration timeToGo) {
		Duration oldTimeToGo = this.timeToGo;
		this.timeToGo = timeToGo;
		propertyChangeSupport.firePropertyChange(PROPERTY_TIME_TO_GO, oldTimeToGo, this.timeToGo);
	}

	void setScheduledTime(Duration scheduledTime) {
		Duration oldScheduledTime = this.scheduledTime;
		this.scheduledTime = scheduledTime;
		propertyChangeSupport.firePropertyChange(PROPERTY_SCHEDULED_TIME, oldScheduledTime, this.scheduledTime);
	}

	void incrementCompetitorsVersion() {
		this.competitorsVersion = this.competitorsVersion + 1;
		propertyChangeSupport.firePropertyChange(PROPERTY_COMPETITORS_VERSION, this.competitorsVersion - 1,
				this.competitorsVersion);
	}

	private FlagStatus convertFlagStatus(String flagStatus) {
		return switch (flagStatus.toUpperCase()) {
		case "GREEN" -> FlagStatus.GREEN;
		case "YELLOW" -> FlagStatus.YELLOW;
		case "RED" -> FlagStatus.RED;
		case "FINISH" -> FlagStatus.FINISH;
		// If Race Name is empty and raceID is -1 we have no active Race.
		default -> "".equals(name) && id == -1 ? FlagStatus.NONE : FlagStatus.PURPLE;
		};
	}

	@Override
	public String toString() {
		String string;

		// Race Info
		string = "Race Name: " + name + "\n";
		string += "Time to go: " + DurationUtil.format(timeToGo) + "\n";
		string += "Elapsed Time: " + DurationUtil.format(elapsedTime) + "\n";
		string += "Race Duration: " + DurationUtil.format(scheduledTime) + "\n";

		// Leader Info
		Competitor leader = getCompetitorByPosition(1);

		if (leader != null) {
			string += "Leader: " + leader.getRegNumber() + "\n";
			string += "Leader Laps: " + leader.getLapsComplete() + "\n";
			string += "Leader Total Time: " + leader.getTotalTime() + "\n";
		}

		return string;
	}
}
