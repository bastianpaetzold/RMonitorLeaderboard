package com.zacharyfox.rmonitor.entities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Duration;
import java.util.Objects;

import com.zacharyfox.rmonitor.message.ClassInfo;
import com.zacharyfox.rmonitor.message.CompInfo;
import com.zacharyfox.rmonitor.message.Heartbeat;
import com.zacharyfox.rmonitor.message.InitRecord;
import com.zacharyfox.rmonitor.message.LapInfo;
import com.zacharyfox.rmonitor.message.QualInfo;
import com.zacharyfox.rmonitor.message.RMonitorMessage;
import com.zacharyfox.rmonitor.message.RaceInfo;
import com.zacharyfox.rmonitor.message.RegistrationInfo;
import com.zacharyfox.rmonitor.message.RunInfo;
import com.zacharyfox.rmonitor.message.SettingInfo;
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

	private static final String SETTING_TRACKNAME = "TRACKNAME";
	private static final String SETTING_TRACK_LENGTH = "TRACKLENGTH";

	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

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
	private int competitorsVersion;

	public enum FlagStatus {
		PURPLE, GREEN, YELLOW, RED, FINISH, NONE
	}

	public Race() {
		id = 0;
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
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(l);
	}

	public void update(RMonitorMessage message) {
		if (message != null) {
			if (message instanceof RegistrationInfo info) {
				Competitors.updateOrCreate(info);
			}

			if (message instanceof Heartbeat heartbeat) {
				messageUpdate(heartbeat);
			} else if (message instanceof RunInfo runInfo) {
				messageUpdate(runInfo);
			} else if (message instanceof SettingInfo settingsInfo) {
				messageUpdate(settingsInfo);
			} else if (message instanceof InitRecord) {
				messageUpdate();
			} else if (message instanceof RaceInfo raceInfo) {
				messageUpdate(raceInfo);
				incrementCompetitorsVersion();
			} else if (message instanceof CompInfo || message instanceof LapInfo || message instanceof QualInfo) {
				incrementCompetitorsVersion();
			} else if (message instanceof ClassInfo classInfo) {
				RaceClass.update(classInfo);
			}
		}
	}

	private void messageUpdate(Heartbeat heartbeat) {
		setFlagStatus(heartbeat.getFlagStatus());
		setLapsToGo(heartbeat.getLapsToGo());
		setScheduledLaps(lapsComplete + lapsToGo);
		setElapsedTime(heartbeat.getRaceTime());
		setTimeToGo(heartbeat.getTimeToGo());
		setTimeOfDay(heartbeat.getTimeOfDay());
		setScheduledTime(elapsedTime.plus(timeToGo));
	}

	private void messageUpdate(RaceInfo raceInfo) {
		if (raceInfo.getPosition() == 1) {
			setLapsComplete(raceInfo.getLaps());
		}
	}

	private void messageUpdate(RunInfo runInfo) {
		if (id != runInfo.getUniqueId() && !Objects.equals(name, runInfo.getRaceName())) {
			id = 0;
			name = "";
			trackName = "";
			trackLength = 0.0F;
			lapsComplete = 0;
			lapsToGo = 0;
			scheduledLaps = 0;
			elapsedTime = Duration.ZERO;
			timeToGo = Duration.ZERO;
			timeOfDay = Duration.ZERO;
			scheduledTime = Duration.ZERO;
			competitorsVersion = 0;

			Competitors.reset();
		}

		setId(runInfo.getUniqueId());
		setName(runInfo.getRaceName());
		setFlagStatus("");
	}

	private void messageUpdate() {
		// before initializing the race we FINISH the last one and store the status of
		// the old race
		setFlagStatus("FINISH");
		Races.addRace(this); // we fetch the TO so that the current race gets stored

		setId(0);
		setName("");
		setTrackName("");
		setTrackLength(0.0F);
		setFlagStatus("");
		setLapsComplete(0);
		setLapsToGo(0);
		setScheduledLaps(0);
		setElapsedTime(Duration.ZERO);
		setTimeToGo(Duration.ZERO);
		setTimeOfDay(Duration.ZERO);
		setScheduledTime(Duration.ZERO);

		Competitors.reset();
		competitorsVersion = 0;
		incrementCompetitorsVersion();

	}

	private void messageUpdate(SettingInfo settingInfo) {
		if (settingInfo.getDescription().equals(SETTING_TRACKNAME)) {
			setTrackName(settingInfo.getValue());
		}

		if (settingInfo.getDescription().equals(SETTING_TRACK_LENGTH)) {
			setTrackLength(Float.parseFloat(settingInfo.getValue()));
		}
	}

	private FlagStatus convertFlagStatus(String flagStatus) {
		return switch (flagStatus.toUpperCase()) {
		case "GREEN" -> FlagStatus.GREEN;
		case "YELLOW" -> FlagStatus.YELLOW;
		case "RED" -> FlagStatus.RED;
		case "FINISH" -> FlagStatus.FINISH;
		// If Race Name is empty and raceID is 0 we have no active Race.
		default -> "".equals(name) && this.id == 0 ? FlagStatus.NONE : FlagStatus.PURPLE;
		};
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

	private void setId(int id) {
		int oldId = this.id;
		this.id = id;
		changeSupport.firePropertyChange(PROPERTY_RACE_ID, oldId, this.id);
	}

	private void setName(String name) {
		String oldName = this.name;
		this.name = name;
		changeSupport.firePropertyChange(PROPERTY_RACE_NAME, oldName, this.name);
	}

	private void setTrackName(String trackName) {
		String oldTrackName = this.trackName;
		this.trackName = trackName;
		changeSupport.firePropertyChange(PROPERTY_TRACK_NAME, oldTrackName, this.trackName);
	}

	private void setTrackLength(Float trackLength) {
		Float oldTrackLength = this.trackLength;
		this.trackLength = trackLength;
		changeSupport.firePropertyChange(PROPERTY_TRACK_LENGTH, oldTrackLength, this.trackLength);
	}

	private void setFlagStatus(String flagStatus) {
		FlagStatus oldFlagStatus = this.flagStatus;
		this.flagStatus = convertFlagStatus(flagStatus);
		changeSupport.firePropertyChange(PROPERTY_FLAG_STATUS, oldFlagStatus, this.flagStatus);
	}

	private void setLapsComplete(int lapsComplete) {
		int oldLapsComplete = this.lapsComplete;
		this.lapsComplete = lapsComplete;
		changeSupport.firePropertyChange(PROPERTY_LAPS_COMPLETE, oldLapsComplete, this.lapsComplete);
	}

	private void setLapsToGo(int lapsToGo) {
		int oldLapsToGo = this.lapsToGo;
		this.lapsToGo = lapsToGo;
		changeSupport.firePropertyChange(PROPERTY_LAPS_TO_GO, oldLapsToGo, this.lapsToGo);
	}

	private void setScheduledLaps(int scheduledLaps) {
		int oldScheduledLaps = scheduledLaps;
		this.scheduledLaps = scheduledLaps;
		changeSupport.firePropertyChange(PROPERTY_SCHEDULED_LAPS, oldScheduledLaps, this.scheduledLaps);
	}

	private void setTimeOfDay(Duration timeOfDay) {
		Duration oldTimeOfDay = this.timeOfDay;
		this.timeOfDay = timeOfDay;
		changeSupport.firePropertyChange(PROPERTY_TIME_OF_DAY, oldTimeOfDay, this.timeOfDay);
	}

	private void setElapsedTime(Duration elapsedTime) {
		Duration oldElapsedTime = this.elapsedTime;
		this.elapsedTime = elapsedTime;
		changeSupport.firePropertyChange(PROPERTY_ELAPSED_TIME, oldElapsedTime, this.elapsedTime);
	}

	private void setTimeToGo(Duration timeToGo) {
		Duration oldTimeToGo = this.timeToGo;
		this.timeToGo = timeToGo;
		changeSupport.firePropertyChange(PROPERTY_TIME_TO_GO, oldTimeToGo, this.timeToGo);
	}

	private void setScheduledTime(Duration scheduledTime) {
		Duration oldScheduledTime = this.scheduledTime;
		this.scheduledTime = scheduledTime;
		changeSupport.firePropertyChange(PROPERTY_SCHEDULED_TIME, oldScheduledTime, this.scheduledTime);
	}

	private void incrementCompetitorsVersion() {
		this.competitorsVersion = this.competitorsVersion + 1;
		changeSupport.firePropertyChange(PROPERTY_COMPETITORS_VERSION, this.competitorsVersion - 1,
				this.competitorsVersion);
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
		Competitor leader = Competitors.getCompetitorByPosition(1);

		if (leader != null) {
			string += "Leader: " + leader.getRegNumber() + "\n";
			string += "Leader Laps: " + leader.getLapsComplete() + "\n";
			string += "Leader Total Time: " + leader.getTotalTime() + "\n";
		}

		return string;
	}
}
