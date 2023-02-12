package com.zacharyfox.rmonitor.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RunInfoTest {

	@Test
	void test() {
		String[] tokens = { "$B", "5", "Friday free practice" };

		RunInfo message = new RunInfo(tokens);

		assertEquals(5, message.getUniqueId());
		assertEquals("Friday free practice", message.getRaceName());
	}
}
