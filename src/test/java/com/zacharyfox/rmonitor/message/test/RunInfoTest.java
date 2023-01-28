package com.zacharyfox.rmonitor.message.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.zacharyfox.rmonitor.message.RunInfo;

public class RunInfoTest
{

	@Test
	public void test()
	{
		String[] tokens = {
			"$B", "5", "Friday free practice"
		};

		RunInfo message = new RunInfo(tokens);

		assertEquals(5, message.getUniqueId());
		assertEquals("Friday free practice", message.getRaceName());
	}
}
