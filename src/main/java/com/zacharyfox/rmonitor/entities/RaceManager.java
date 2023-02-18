package com.zacharyfox.rmonitor.entities;

import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.zacharyfox.rmonitor.entities.Race.FlagStatus;
import com.zacharyfox.rmonitor.message.ClassInfo;
import com.zacharyfox.rmonitor.message.CompInfo;
import com.zacharyfox.rmonitor.message.Heartbeat;
import com.zacharyfox.rmonitor.message.InitRecord;
import com.zacharyfox.rmonitor.message.LapInfo;
import com.zacharyfox.rmonitor.message.PassingInfo;
import com.zacharyfox.rmonitor.message.QualInfo;
import com.zacharyfox.rmonitor.message.RMonitorMessage;
import com.zacharyfox.rmonitor.message.RaceInfo;
import com.zacharyfox.rmonitor.message.RunInfo;
import com.zacharyfox.rmonitor.message.SettingInfo;

public class RaceManager {

	private static final String SETTING_TRACKNAME = "TRACKNAME";
	private static final String SETTING_TRACK_LENGTH = "TRACKLENGTH";

	private Map<Integer, Race> raceMap;
	private Race currentRace;
	private Race previousRace;

	private static RaceManager instance;

	public static RaceManager getInstance() {
		if (instance == null) {
			instance = new RaceManager();
		}
		return instance;
	}

	/**
	 * This should only be called directly by tests. Use {@link #getInstance()}
	 * instead.
	 */
	RaceManager() {
		raceMap = new HashMap<>();
		currentRace = new Race();
	}

	public Race getCurrentRace() {
		return currentRace;
	}

	public Race getPreviousRace() {
		return previousRace;
	}

	public void startNewRace() {
		currentRace.setFlagStatus(FlagStatus.FINISH.toString());

		if (currentRace.getId() > 0) {
			raceMap.put(currentRace.getId(), currentRace);
			previousRace = currentRace;
		}

		Race race = new Race();
		for (PropertyChangeListener listener : currentRace.getPropertyChangeSupport().getPropertyChangeListeners()) {
			currentRace.getPropertyChangeSupport().removePropertyChangeListener(listener);
			race.getPropertyChangeSupport().addPropertyChangeListener(listener);
		}
		currentRace = race;
	}

	public Race getRace(int raceId) {
		return raceMap.get(raceId);
	}

	public Collection<Race> getAllRaces() {
		return Collections.unmodifiableCollection(raceMap.values());
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		currentRace.getPropertyChangeSupport().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		currentRace.getPropertyChangeSupport().removePropertyChangeListener(listener);
	}

	public void processMessage(RMonitorMessage message) {
		if (message != null) {
			if (message instanceof Heartbeat heartbeat) {
				processHeartbeat(heartbeat);
			} else if (message instanceof RunInfo runInfo) {
				processRunInfo(runInfo);
			} else if (message instanceof SettingInfo settingsInfo) {
				processSettingsInfo(settingsInfo);
			} else if (message instanceof InitRecord) {
				processInitRecord();
			} else if (message instanceof RaceInfo raceInfo) {
				currentRace.getOrCreateCompetitor(raceInfo.getRegNumber()).update(raceInfo);
				processRunInfo(raceInfo);
				currentRace.incrementCompetitorsVersion();
			} else if (message instanceof CompInfo compInfo) {
				currentRace.getOrCreateCompetitor(compInfo.getRegNumber()).update(compInfo);
				currentRace.incrementCompetitorsVersion();
			} else if (message instanceof LapInfo lapInfo) {
				currentRace.getOrCreateCompetitor(lapInfo.getRegNumber()).update(lapInfo);
				currentRace.incrementCompetitorsVersion();
			} else if (message instanceof QualInfo qualiInfo) {
				currentRace.getOrCreateCompetitor(qualiInfo.getRegNumber()).update(qualiInfo);
				currentRace.incrementCompetitorsVersion();
			} else if (message instanceof ClassInfo classInfo) {
				RaceClass.update(classInfo);
			} else if (message instanceof PassingInfo passingInfo) {
				currentRace.getOrCreateCompetitor(passingInfo.getRegNumber()).update(passingInfo);
			}
		}
	}

	private void processHeartbeat(Heartbeat heartbeat) {
		currentRace.setFlagStatus(heartbeat.getFlagStatus());
		currentRace.setLapsToGo(heartbeat.getLapsToGo());
		currentRace.setScheduledLaps(currentRace.getLapsComplete() + currentRace.getLapsToGo());
		currentRace.setElapsedTime(heartbeat.getRaceTime());
		currentRace.setTimeToGo(heartbeat.getTimeToGo());
		currentRace.setTimeOfDay(heartbeat.getTimeOfDay());
		currentRace.setScheduledTime(currentRace.getElapsedTime().plus(currentRace.getTimeToGo()));
	}

	private void processRunInfo(RaceInfo raceInfo) {
		if (raceInfo.getPosition() == 1) {
			currentRace.setLapsComplete(raceInfo.getLaps());
		}
	}

	private void processRunInfo(RunInfo runInfo) {
		if (currentRace.getId() != runInfo.getUniqueId()) {
			startNewRace();
		}

		currentRace.setId(runInfo.getUniqueId());
		currentRace.setName(runInfo.getRaceName());
		currentRace.setFlagStatus("");

		if (currentRace.getId() > 0) {
			raceMap.put(currentRace.getId(), currentRace);
		}
	}

	private void processInitRecord() {
		startNewRace();

		currentRace.incrementCompetitorsVersion();
	}

	private void processSettingsInfo(SettingInfo settingInfo) {
		if (settingInfo.getDescription().equals(SETTING_TRACKNAME)) {
			currentRace.setTrackName(settingInfo.getValue());
		}

		if (settingInfo.getDescription().equals(SETTING_TRACK_LENGTH)) {
			currentRace.setTrackLength(Float.parseFloat(settingInfo.getValue()));
		}
	}
}
