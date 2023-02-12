package com.zacharyfox.rmonitor.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ClassInfoTest {

	@Test
	void test() {
		String[] tokens = { "$C", "5", "Formula 300" };

		ClassInfo message = new ClassInfo(tokens);

		assertEquals(5, message.getUniqueId(), "Class ID 5");
		assertEquals("Formula 300", message.getDescription(), "Class Description is Formula 300");
	}
}
