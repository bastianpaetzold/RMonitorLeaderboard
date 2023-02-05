package com.zacharyfox.rmonitor.message;

import java.time.Duration;

import com.zacharyfox.rmonitor.utils.DurationUtil;

public class LapInfo implements RMonitorMessage, RegistrationInfo {

	private int position;
	private String regNumber;
	private int lapNumber;
	private Duration lapTime;

	public LapInfo(String[] tokens) {
		position = Integer.parseInt(tokens[1]);
		regNumber = tokens[2];
		lapNumber = tokens[3].equals("") ? 0 : Integer.parseInt(tokens[3]);
		lapTime = DurationUtil.parse(tokens[4]);
	}

	public int getPosition() {
		return position;
	}

	@Override
	public String getRegNumber() {
		return regNumber;
	}

	public int getLapNumber() {
		return lapNumber;
	}

	public Duration getLapTime() {
		return lapTime;
	}
}
