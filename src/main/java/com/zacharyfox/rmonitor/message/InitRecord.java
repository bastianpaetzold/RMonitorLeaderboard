package com.zacharyfox.rmonitor.message;

import java.time.Duration;

import com.zacharyfox.rmonitor.utils.DurationUtil;

public class InitRecord implements RMonitorMessage {

	private Duration timeOfDay;

	public InitRecord(String[] tokens) {
		timeOfDay = DurationUtil.parse(tokens[1]);
	}

	public Duration getTimeOfDay() {
		return timeOfDay;
	}
}