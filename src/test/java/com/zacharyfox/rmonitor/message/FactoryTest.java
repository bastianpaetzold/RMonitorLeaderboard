package com.zacharyfox.rmonitor.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FactoryTest {

	@Test
	void testClassInfo() {
		String line = "$C,5,\"Formula 300\"";
		RMonitorMessage message = MessageFactory.createMessage(line);

		assertEquals(ClassInfo.class, message.getClass());
	}

	@Test
	void testCompInfo() {
		String line = "$A,\"1234BE\",\"12X\",52474,\"John\",\"Johnson\",\"USA\",5";
		RMonitorMessage message = MessageFactory.createMessage(line);

		assertEquals(CompInfo.class, message.getClass());

		String line_1 = "$COMP,\"1234BE\",\"12X\",5,\"John\",\"Johnson\",\"USA\",\"CAMEL\"";
		RMonitorMessage message_1 = MessageFactory.createMessage(line_1);

		assertEquals(CompInfo.class, message_1.getClass());
	}

	@Test
	void testHeartbeat() {
		String line = "$F,14,\"00:12:45\",\"13:34:23\",\"00:09:47\",\"Green\"";
		RMonitorMessage message = MessageFactory.createMessage(line);

		assertEquals(Heartbeat.class, message.getClass());
	}

	@Test
	void testInitRecord() {
		String line = "$I,\"16:36:08.000\",\"12 jan 01\"";
		RMonitorMessage message = MessageFactory.createMessage(line);

		assertEquals(InitRecord.class, message.getClass());
	}

	@Test
	void testPassingInfo() {
		String line = "$J,\"1234BE\",\"00:02:03.826\",\"01:42:17.672\"";
		RMonitorMessage message = MessageFactory.createMessage(line);

		assertEquals(PassingInfo.class, message.getClass());
	}

	@Test
	void testQualInfo() {
		String line = "$H,2,\"1234BE\",3,\"00:02:17.872\"";
		RMonitorMessage message = MessageFactory.createMessage(line);

		assertEquals(QualiInfo.class, message.getClass());
	}

	@Test
	void testRaceInfo() {
		String line = "$G,3,\"1234BE\",14,\"01:12:47.872\"";
		RMonitorMessage message = MessageFactory.createMessage(line);

		assertEquals(RaceInfo.class, message.getClass());
	}

	@Test
	void testRunInfo() {
		String line = "$B,5,\"Friday free practice\"";
		RMonitorMessage message = MessageFactory.createMessage(line);

		assertEquals(RunInfo.class, message.getClass());
	}

	@Test
	void testSettingInfo() {
		String line = "$E,\"TRACKNAME\",\"Indianapolis Motor Speedway\"";
		RMonitorMessage message = MessageFactory.createMessage(line);

		assertEquals(SettingInfo.class, message.getClass());
	}
}
