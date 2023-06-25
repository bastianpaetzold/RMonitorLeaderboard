package com.zacharyfox.rmonitor.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Test;

class DurationUtilTest {

	@Test
	void testParseNormal() {
		Duration expected = Duration.parse("PT9M47S");
		Duration actual = DurationUtil.parse("00:09:47");
		assertEquals(expected, actual);
	}

	@Test
	void testParseWithNegative() {
		Duration expected = Duration.parse("-PT9M47S");
		Duration actual = DurationUtil.parse("00:09:-47");
		assertEquals(expected, actual);
	}
}
