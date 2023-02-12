package com.zacharyfox.rmonitor.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.zacharyfox.rmonitor.utils.DurationUtil;

class LapInfoTest {

	@Test
	void test() {
		String[] tokens = { "$SP", "3", "2", "2", "00:01:33.894", "76682" };

		LapInfo message = new LapInfo(tokens);

		assertEquals(3, message.getPosition());
		assertEquals("2", message.getRegNumber());
		assertEquals(2, message.getLapNumber());
		assertEquals(DurationUtil.parse("00:01:33.894"), message.getLapTime());
	}
}
