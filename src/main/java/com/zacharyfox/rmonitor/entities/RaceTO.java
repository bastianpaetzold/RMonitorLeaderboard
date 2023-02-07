package com.zacharyfox.rmonitor.entities;

public class RaceTO {

	private String raceName;
	private int raceID;
	private String flagStatus;
	private String elapsedTime;
	private String timeOfDay;
	private int lapsComplete;
	private int lapsToGo;
	private String trackName;
	private CompetitorTO[] competitors;

	public RaceTO(String elapsedTime, String flagStatus, int id, int lapsToGo, int lapsComplete, String name,
			String timeOfDay, String trackName) {
		this.elapsedTime = elapsedTime;
		this.flagStatus = flagStatus;
		this.raceID = id;
		this.lapsToGo = lapsToGo;
		this.lapsComplete = lapsComplete;
		this.raceName = name;
		this.timeOfDay = timeOfDay;
		this.trackName = trackName;
	}

	public String getRaceName() {
		return raceName;
	}

	public void setRaceName(String raceName) {
		this.raceName = raceName;
	}

	public int getRaceID() {
		return raceID;
	}

	public void setRaceID(int raceID) {
		this.raceID = raceID;
	}

	public String getFlagStatus() {
		return flagStatus;
	}

	public void setFlagStatus(String flagStatus) {
		this.flagStatus = flagStatus;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public String getTimeOfDay() {
		return timeOfDay;
	}

	public void setTimeOfDay(String timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public int getLapsComplete() {
		return lapsComplete;
	}

	public void setLapsComplete(int lapsComplete) {
		this.lapsComplete = lapsComplete;
	}

	public int getLapsToGo() {
		return lapsToGo;
	}

	public void setLapsToGo(int lapsToGo) {
		this.lapsToGo = lapsToGo;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public CompetitorTO[] getCompetitors() {
		return competitors;
	}

	public void setCompetitors(CompetitorTO[] competitors) {
		this.competitors = competitors;
	}
}
