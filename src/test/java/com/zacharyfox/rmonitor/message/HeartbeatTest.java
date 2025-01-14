package com.zacharyfox.rmonitor.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.zacharyfox.rmonitor.utils.DurationUtil;

class HeartbeatTest {

	@Test
	void test() {
		String[] tokens = { "$F", "14", "00:12:45", "13:34:23", "00:09:47", "Green" };

		Heartbeat message = new Heartbeat(tokens);

		assertEquals(14, message.getLapsToGo(), "Laps to go");
		assertEquals(DurationUtil.parse("00:12:45"), message.getTimeToGo());
		assertEquals(DurationUtil.parse("13:34:23"), message.getTimeOfDay());
		assertEquals(DurationUtil.parse("00:09:47"), message.getRaceTime());
		assertEquals("Green", message.getFlagStatus());
	}
}
