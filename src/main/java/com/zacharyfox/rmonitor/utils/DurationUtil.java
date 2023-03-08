package com.zacharyfox.rmonitor.utils;

import java.time.Duration;
import java.time.LocalTime;

public class DurationUtil {

	private DurationUtil() {
	}

	public static Duration parse(String duration) {
		return Duration.ofNanos(LocalTime.parse(duration).toNanoOfDay());
	}

	public static String format(Duration duration) {
		return format(duration, false);
	}

	public static String format(Duration duration, boolean withMillis) {
		long hours = duration.toHours();
		int minutes = duration.toMinutesPart();
		int seconds = duration.toSecondsPart();
		int millis = duration.toMillisPart();

		String durationString;

		if (hours != 0) {
			durationString = String.format("%d:%02d:%02d", hours, minutes, seconds);
		} else {
			durationString = String.format("%d:%02d", minutes, seconds);
		}

		if (withMillis || millis != 0) {
			durationString += String.format(".%03d", millis);
		}

		return durationString;
	}
}
