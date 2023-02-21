package com.zacharyfox.rmonitor.entities;

import com.zacharyfox.rmonitor.utils.DurationUtil;

public class RaceTO {

	private String raceName;
	private int raceID;
	private String flagStatus;
	private String elapsedTime;
	private String timeOfDay;
	private int lapsComplete;
	private int lapsToGo;
	private String trackName;
	private Float trackLength;
	private CompetitorTO[] competitors;

	private RaceTO() {
	}

	public String getRaceName() {
		return raceName;
	}

	public int getRaceID() {
		return raceID;
	}

	public String getFlagStatus() {
		return flagStatus;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public String getTimeOfDay() {
		return timeOfDay;
	}

	public int getLapsComplete() {
		return lapsComplete;
	}

	public int getLapsToGo() {
		return lapsToGo;
	}

	public String getTrackName() {
		return trackName;
	}

	public Float getTrackLength() {
		return trackLength;
	}

	public CompetitorTO[] getCompetitors() {
		return competitors;
	}

	public static RaceTO from(Race race) {
		RaceTO raceTO = new RaceTO();
		raceTO.elapsedTime = DurationUtil.format(race.getElapsedTime());
		raceTO.flagStatus = race.getFlagStatus().toString();
		raceTO.raceID = race.getId();
		raceTO.lapsToGo = race.getLapsToGo();
		raceTO.lapsComplete = race.getLapsComplete();
		raceTO.raceName = race.getName();
		raceTO.timeOfDay = DurationUtil.format(race.getTimeOfDay());
		raceTO.trackName = race.getTrackName();
		raceTO.trackLength = race.getTrackLength();
		raceTO.competitors = race.getCompetitors().stream().map(CompetitorTO::from).toArray(CompetitorTO[]::new);

		return raceTO;
	}
}
