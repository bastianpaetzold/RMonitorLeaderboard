package com.zacharyfox.rmonitor.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.zacharyfox.rmonitor.message.ClassInfo;
import com.zacharyfox.rmonitor.message.MessageFactory;
import com.zacharyfox.rmonitor.message.RMonitorMessage;

class RaceClassTest {

	@Test
	void testUpdateRaceClass() {
		assertEquals("", RaceClass.getClassName(5));

		String line = "$C,5,\"Formula 300\"";
		RMonitorMessage message = MessageFactory.createMessage(line);

		RaceClass.update((ClassInfo) message);

		assertEquals("Formula 300", RaceClass.getClassName(5));

		String line2 = "$C,5,\"Formula 1000\"";
		RMonitorMessage message2 = MessageFactory.createMessage(line2);

		RaceClass.update((ClassInfo) message2);

		assertEquals("Formula 1000", RaceClass.getClassName(5));
	}
}
