package com.zacharyfox.rmonitor.entities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Duration;
import java.util.HashMap;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	private static final Logger LOGGER = LogManager.getLogger(Race.class);
	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	private int competitorsVersion = 0;
	private Duration elapsedTime = Duration.ZERO;
	private String flagStatus = "";

	public enum FlagState {
		PURPLE, GREEN, YELLOW, RED, FINISH, NONE
	}

	private FlagState currentFlagState;
	private int id = 0;
	private int lapsComplete = 0;
	private int lapsToGo = 0;
	private String name = "";
	private Duration scheduledTime = Duration.ZERO;
	private Duration timeOfDay = Duration.ZERO;
	private Duration timeToGo = Duration.ZERO;
	private Float trackLength = (float) 0.0;
	private String trackName = "";

	private static HashMap<Integer, RaceTO> allRaces = new HashMap<>();

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property, PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(property, l);
	}

	public int getLapsComplete() {
		return lapsComplete;
	}

	public int getLapsToGo() {
		return lapsToGo;
	}

	public int getScheduledLaps() {
		return lapsComplete + lapsToGo;
	}

	public Duration getScheduledTime() {
		return scheduledTime;
	}

	public Float getTrackLength() {
		return trackLength;
	}

	public String getTrackName() {
		return trackName;
	}

	public String getRaceName() {
		return name;
	}

	/**
	 * @return the elapsedTime
	 */
	public synchronized Duration getElapsedTime() {
		return elapsedTime;
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(l);
	}

	public void removePropertyChangeListener(String property, PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(property, l);
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
		Competitor leader = Competitor.getByPosition(1);

		if (leader != null) {
			string += "Leader: " + leader.getRegNumber() + "\n";
			string += "Leader Laps: " + leader.getLapsComplete() + "\n";
			string += "Leader Total Time: " + leader.getTotalTime() + "\n";
		}

		return string;
	}

	public void update(RMonitorMessage message) {
		if (message != null) {
			if (message instanceof RegistrationInfo info) {
				Competitor.updateOrCreate(info);
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
				setCompetitorsVersion();
			} else if (message instanceof CompInfo || message instanceof LapInfo || message instanceof QualInfo) {
				setCompetitorsVersion();
			} else if (message instanceof ClassInfo classInfo) {
				RaceClass.update(classInfo);
			} else {
				LOGGER.info("Message not processed by Race: {}", message);
			}
		}
	}

	private void messageUpdate(Heartbeat message) {
		setElapsedTime(message.getRaceTime());
		setLapsToGo(message.getLapsToGo());
		setTimeToGo(message.getTimeToGo());
		setScheduledTime(elapsedTime.plus(timeToGo));
		setTimeOfDay(message.getTimeOfDay());
		setFlagStatus(message.getFlagStatus());
	}

	private void messageUpdate(RaceInfo message) {
		if (message.getPosition() == 1) {
			setLapsComplete(message.getLaps());
		}
	}

	private void messageUpdate(RunInfo message) {
		if (id != message.getUniqueId() && !Objects.equals(name, message.getRaceName())) {
			competitorsVersion = 0;
			elapsedTime = Duration.ZERO;
			id = 0;
			lapsComplete = 0;
			lapsToGo = 0;
			name = "";
			scheduledTime = Duration.ZERO;
			timeOfDay = Duration.ZERO;
			timeToGo = Duration.ZERO;
			trackLength = (float) 0.0;
			trackName = "";

			Competitor.reset();
		}

		setName(message.getRaceName());
		setId(message.getUniqueId());
		setFlagStatus("");
	}

	private void messageUpdate() {
		// before initializing the race we FINISH the last one and store the status of
		// the old race
		setFlagStatus("FINISH");
		getRaceTO(); // we fetch the TO so that the current race gets stored

		setElapsedTime(Duration.ZERO);
		setLapsToGo(0);
		setTimeToGo(Duration.ZERO);
		setScheduledTime(Duration.ZERO);
		setTimeOfDay(Duration.ZERO);
		Competitor.reset();
		competitorsVersion = 0;
		setCompetitorsVersion();
		setLapsComplete(0);
		setTrackLength((float) 0.0);

		setTrackName("");
		setName("");
		setId(0);
		setFlagStatus("");

	}

	private void messageUpdate(SettingInfo message) {
		if (message.getDescription().equals("TRACKNAME")) {
			setTrackName(message.getValue());
		}

		if (message.getDescription().equals("TRACKLENGTH")) {
			setTrackLength(Float.parseFloat(message.getValue()));
		}
	}

	private void setCompetitorsVersion() {
		this.competitorsVersion = this.competitorsVersion + 1;
		changeSupport.firePropertyChange("competitorsVersion", this.competitorsVersion - 1, this.competitorsVersion);
	}

	private void setElapsedTime(Duration elapsedTime) {
		Duration oldElapsedTime = this.elapsedTime;
		this.elapsedTime = elapsedTime;
		changeSupport.firePropertyChange("elapsedTime", oldElapsedTime, this.elapsedTime);
	}

	private void setFlagStatus(String flagStatus) {
		String oldFlagStatus = this.flagStatus;
		FlagState oldCurrentFlagState = this.currentFlagState;

		this.flagStatus = flagStatus;

		switch (flagStatus.toUpperCase()) {
		case "RED":
			currentFlagState = FlagState.RED;
			break;

		case "YELLOW":
			currentFlagState = FlagState.YELLOW;
			break;

		case "GREEN":
			currentFlagState = FlagState.GREEN;
			break;

		case "FINISH":
			currentFlagState = FlagState.FINISH;
			break;

		default:
			// If Race Name is empty and raceID is 0 we have no active Race.
			if ("".equals(this.name) && this.id == 0) {
				currentFlagState = FlagState.NONE;
			} else {
				currentFlagState = FlagState.PURPLE;
			}
		}

		changeSupport.firePropertyChange("flagStatus", oldFlagStatus, this.flagStatus);
		changeSupport.firePropertyChange("currentFlagState", oldCurrentFlagState, this.currentFlagState);
	}

	private void setId(int id) {
		int oldId = this.id;
		this.id = id;
		changeSupport.firePropertyChange("raceID", oldId, this.id);
	}

	private void setLapsComplete(int lapsComplete) {
		int oldLapsComplete = this.lapsComplete;
		this.lapsComplete = lapsComplete;
		changeSupport.firePropertyChange("lapsComplete", oldLapsComplete, this.lapsComplete);
	}

	private void setLapsToGo(int lapsToGo) {
		int oldLapsToGo = this.lapsToGo;
		this.lapsToGo = lapsToGo;
		changeSupport.firePropertyChange("lapsToGo", oldLapsToGo, this.lapsToGo);
	}

	private void setName(String name) {
		String oldName = this.name;
		this.name = name;
		changeSupport.firePropertyChange("raceName", oldName, this.name);
	}

	/**
	 * @return the flagStatus
	 */
	public synchronized String getFlagStatus() {
		return flagStatus;
	}

	/**
	 * @return the currentFlagState
	 */
	public synchronized FlagState getCurrentFlagState() {
		return currentFlagState;
	}

	private void setScheduledTime(Duration scheduledTime) {
		Duration oldScheduledTime = this.scheduledTime;
		this.scheduledTime = scheduledTime;
		changeSupport.firePropertyChange("scheduledTime", oldScheduledTime, this.scheduledTime);
	}

	private void setTimeOfDay(Duration timeOfDay) {
		Duration oldTimeOfDay = this.timeOfDay;
		this.timeOfDay = timeOfDay;
		changeSupport.firePropertyChange("timeOfDay", oldTimeOfDay, this.timeOfDay);
	}

	private void setTimeToGo(Duration timeToGo) {
		Duration oldTimeToGo = this.timeToGo;
		this.timeToGo = timeToGo;
		changeSupport.firePropertyChange("timeToGo", oldTimeToGo, this.timeToGo);
	}

	private void setTrackLength(Float trackLength) {
		Float oldTrackLength = this.trackLength;
		this.trackLength = trackLength;
		changeSupport.firePropertyChange("trackLength", oldTrackLength, this.trackLength);
	}

	private void setTrackName(String trackName) {
		String oldTrackName = this.trackName;
		this.trackName = trackName;
		changeSupport.firePropertyChange("trackName", oldTrackName, this.trackName);
	}

	public RaceTO getRaceTO() {
		RaceTO raceTO = new RaceTO(DurationUtil.format(elapsedTime), currentFlagState.toString(), id, lapsToGo,
				lapsComplete, name, DurationUtil.format(timeOfDay), trackName);
		Competitor.setCompetitorTO(raceTO);

		if (id != 0) {
			allRaces.put(id, raceTO);
		}
		return raceTO;
	}

	public static RaceTO getToByID(int raceID) {
		return allRaces.get(raceID);
	}

	public static RaceTO[] getAllRaceTOs() {
		return allRaces.values().toArray(new RaceTO[0]);
	}
}
