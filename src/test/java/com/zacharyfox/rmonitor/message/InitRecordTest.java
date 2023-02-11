package com.zacharyfox.rmonitor.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.zacharyfox.rmonitor.message.InitRecord;
import com.zacharyfox.rmonitor.utils.DurationUtil;

class InitRecordTest {

	@Test
	void test() {
		String[] tokens = { "$I", "16:36:08.000", "12 jan 01" };

		InitRecord message = new InitRecord(tokens);

		assertEquals(DurationUtil.parse("16:36:08.000"), message.getTimeOfDay());
	}
}
