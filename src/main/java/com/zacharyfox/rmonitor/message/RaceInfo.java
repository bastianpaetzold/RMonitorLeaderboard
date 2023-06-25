package com.zacharyfox.rmonitor.message;

import java.time.Duration;

import com.zacharyfox.rmonitor.utils.DurationUtil;

public class RaceInfo implements RMonitorMessage, RegistrationInfo {

	private int position;
	private String regNumber;
	private int laps;
	private Duration totalTime;

	public RaceInfo(String[] tokens) {
		position = Integer.parseInt(tokens[1]);
		regNumber = tokens[2];
		laps = (tokens[3].equals("")) ? 0 : Integer.parseInt(tokens[3]);
		totalTime = DurationUtil.parse(tokens[4]);
	}

	public int getLaps() {
		return laps;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public String getRegNumber() {
		return regNumber;
	}

	public Duration getTotalTime() {
		return totalTime;
	}

	@Override
	public String toString() {
		return "position: " + position + ", registration number: " + regNumber + ", laps: " + laps + ", total time: "
				+ totalTime;
	}
}
