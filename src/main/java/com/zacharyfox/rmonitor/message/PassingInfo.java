package com.zacharyfox.rmonitor.message;

import java.time.Duration;

import com.zacharyfox.rmonitor.utils.DurationUtil;

public class PassingInfo implements RMonitorMessage, RegistrationInfo {

	private final String regNumber;
	private final Duration lapTime;
	private final Duration totalTime;

	public PassingInfo(String[] tokens) {
		regNumber = tokens[1];
		lapTime = DurationUtil.parse(tokens[2]);
		totalTime = DurationUtil.parse(tokens[3]);
	}

	public Duration getLapTime() {
		return lapTime;
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
		return "registration number: " + regNumber + ", lap time: " + lapTime + ", total time: " + totalTime;
	}
}
