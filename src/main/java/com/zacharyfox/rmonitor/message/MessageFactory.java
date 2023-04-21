package com.zacharyfox.rmonitor.message;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageFactory.class);

	// @formatter:off
	private static Map<String, Function<String[], RMonitorMessage>> messageFactories = Map.ofEntries(
			new AbstractMap.SimpleEntry<String, Function<String[], RMonitorMessage>>("$F", Heartbeat::new),
			new AbstractMap.SimpleEntry<>("$B", RunInfo::new),
			new AbstractMap.SimpleEntry<>("$G", RaceInfo::new),
			new AbstractMap.SimpleEntry<>("$A", CompInfo::new),
			new AbstractMap.SimpleEntry<>("$C", ClassInfo::new),
			new AbstractMap.SimpleEntry<>("$H", QualiInfo::new),
			new AbstractMap.SimpleEntry<>("$E", SettingInfo::new),
			new AbstractMap.SimpleEntry<>("$I", InitRecord::new),
			new AbstractMap.SimpleEntry<>("$J", PassingInfo::new),
			new AbstractMap.SimpleEntry<>("$SP", LapInfo::new),
			new AbstractMap.SimpleEntry<>("$SR", LapInfo::new),
			new AbstractMap.SimpleEntry<>("$COMP", CompInfo::new));
	// @formatter:on

	private MessageFactory() {
	}

	public static RMonitorMessage createMessage(String line) {
		LOGGER.debug("Message: {}", line);
		// TODO: better tokenizing here - doesn't handle values with commas
		String[] tokens = line.split(",");

		for (int i = 0; i < tokens.length; i++) {
			tokens[i] = tokens[i].replace("\"", "");
		}

		Function<String[], RMonitorMessage> factory = messageFactories.get(tokens[0]);

		if (factory == null) {
			LOGGER.warn("Unknown type ({}) in message: \"{}\". Message will be skipped.", tokens[0], line);
			return null;
		}

		return factory.apply(tokens);
	}
}
