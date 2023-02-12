package com.zacharyfox.rmonitor.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.zacharyfox.rmonitor.utils.DurationUtil;

class QualInfoTest {

	@Test
	void test() {
		String[] tokens = { "$H", "2", "1234BE", "3", "00:02:17.872" };

		QualInfo message = new QualInfo(tokens);

		assertEquals(2, message.getPosition());
		assertEquals("1234BE", message.getRegNumber());
		assertEquals(3, message.getBestLap());
		assertEquals(DurationUtil.parse("00:02:17.872"), message.getBestLapTime());
	}
}
