package com.zacharyfox.rmonitor.message;

import java.lang.reflect.Constructor;
import java.util.AbstractMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Factory {

	private static final Logger LOGGER = LogManager.getLogger(Factory.class);

	// @formatter:off
	private static Map<String, Class<?>> classMap = Map.ofEntries(
			new AbstractMap.SimpleEntry<>("$F", Heartbeat.class),
			new AbstractMap.SimpleEntry<>("$B", RunInfo.class),
			new AbstractMap.SimpleEntry<>("$G", RaceInfo.class),
			new AbstractMap.SimpleEntry<>("$A", CompInfo.class),
			new AbstractMap.SimpleEntry<>("$C", ClassInfo.class),
			new AbstractMap.SimpleEntry<>("$H", QualInfo.class),
			new AbstractMap.SimpleEntry<>("$E", SettingInfo.class),
			new AbstractMap.SimpleEntry<>("$I", InitRecord.class),
			new AbstractMap.SimpleEntry<>("$J", PassingInfo.class),
			new AbstractMap.SimpleEntry<>("$SP", LapInfo.class),
			new AbstractMap.SimpleEntry<>("$SR", LapInfo.class),
			new AbstractMap.SimpleEntry<>("$COMP", CompInfo.class));
	// @formatter:on

	private Factory() {
	}

	@SuppressWarnings("unchecked")
	public static <M extends RMonitorMessage> M getMessage(String line) {
		LOGGER.debug("Message: {}", line);
		// TODO: better tokenizing here - doesn't handle values with commas
		String[] tokens = line.split(",");

		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = tokens[i].replace("\"", "");
		}
		Class<?> messageClass = classMap.get(tokens[0]);

		try {
			Constructor<?> constructor = messageClass.getDeclaredConstructor(String[].class);
			return (M) constructor.newInstance(new Object[] { tokens });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
