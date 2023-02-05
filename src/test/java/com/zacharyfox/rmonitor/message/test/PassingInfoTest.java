package com.zacharyfox.rmonitor.message.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.zacharyfox.rmonitor.message.PassingInfo;
import com.zacharyfox.rmonitor.utils.DurationUtil;

public class PassingInfoTest {

	@Test
	public void test() {
		String[] tokens = { "$J", "1234BE", "00:02:03.826", "01:42:17.672" };

		PassingInfo message = new PassingInfo(tokens);

		assertEquals("1234BE", message.getRegNumber());
		assertEquals(DurationUtil.parse("00:02:03.826"), message.getLapTime());
		assertEquals(DurationUtil.parse("01:42:17.672"), message.getTotalTime());
	}
}
