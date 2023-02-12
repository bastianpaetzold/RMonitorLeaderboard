package com.zacharyfox.rmonitor.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.zacharyfox.rmonitor.utils.DurationUtil;

class RaceInfoTest {

	@Test
	void test() {
		String[] tokens = { "$G", "3", "1234BE", "14", "01:12:47.872" };

		RaceInfo message = new RaceInfo(tokens);

		assertEquals(3, message.getPosition());
		assertEquals("1234BE", message.getRegNumber());
		assertEquals(14, message.getLaps());
		assertEquals(DurationUtil.parse("01:12:47.872"), message.getTotalTime());
	}
}
