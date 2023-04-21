package com.zacharyfox.rmonitor.message;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class MessageFactoryTest {

	@Test
	void testMessageQuotedWithComma() {
		String[] tokens = MessageFactory.tokenizeMessage("$E,\"TRACKNAME\",\"Arena, Geisingen\"");
		assertArrayEquals(new String[] { "$E", "TRACKNAME", "Arena, Geisingen" }, tokens);
	}

	@Test
	void testMessageQuotedLastToken() {
		String[] tokens = MessageFactory.tokenizeMessage("$E,\"TRACKNAME\",\"Arena Geisingen\"");
		assertArrayEquals(new String[] { "$E", "TRACKNAME", "Arena Geisingen" }, tokens);
	}

	@Test
	void testMessageSingleLastToken() {
		String[] tokens = MessageFactory.tokenizeMessage("$A,\"1\",\"1\",1001,\"\",\"\",\"\",1");
		assertArrayEquals(new String[] { "$A", "1", "1", "1001", "", "", "", "1" }, tokens);
	}

	@Test
	void testMessageEmptyToken() {
		String[] tokens = MessageFactory.tokenizeMessage("$SR,40,\"40\",,\"00:00:00\",0");
		assertArrayEquals(new String[] { "$SR", "40", "40", "", "00:00:00", "0" }, tokens);
	}

	@Test
	void testMessageQuotedEmptyToken() {
		String[] tokens = MessageFactory.tokenizeMessage("$A,\"1\",\"1\",1001,\"\",\"\",\"\",1");
		assertArrayEquals(new String[] { "$A", "1", "1", "1001", "", "", "", "1" }, tokens);
	}
}
