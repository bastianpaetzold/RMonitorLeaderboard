package com.zacharyfox.rmonitor.message;

import java.time.Duration;

import com.zacharyfox.rmonitor.utils.DurationUtil;

public class Heartbeat implements RMonitorMessage {

	private String flagStatus;
	private int lapsToGo;
	private Duration raceTime;
	private Duration timeOfDay;
	private Duration timeToGo;

	public Heartbeat(String[] tokens) {
		lapsToGo = Integer.parseInt(tokens[1]);
		timeToGo = DurationUtil.parse(tokens[2]);
		timeOfDay = DurationUtil.parse(tokens[3]);
		raceTime = DurationUtil.parse(tokens[4]);
		flagStatus = tokens[5].trim();
	}

	public String getFlagStatus() {
		return flagStatus;
	}

	public int getLapsToGo() {
		return lapsToGo;
	}

	public Duration getRaceTime() {
		return raceTime;
	}

	public Duration getTimeOfDay() {
		return timeOfDay;
	}

	public Duration getTimeToGo() {
		return timeToGo;
	}
}
