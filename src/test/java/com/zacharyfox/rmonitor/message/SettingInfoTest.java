package com.zacharyfox.rmonitor.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SettingInfoTest {

	@Test
	void test() {
		String[] tokens = { "$E", "TRACKNAME", "Indianapolis Motor Speedway" };

		SettingInfo message = new SettingInfo(tokens);

		assertEquals("TRACKNAME", message.getDescription());
		assertEquals("Indianapolis Motor Speedway", message.getValue());

		String[] tokens_1 = { "$E", "TRACKLENGTH", "2.500" };

		SettingInfo message_1 = new SettingInfo(tokens_1);

		assertEquals("TRACKLENGTH", message_1.getDescription());
		assertEquals("2.500", message_1.getValue());
	}
}
