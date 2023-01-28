package com.zacharyfox.rmonitor.utils.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.zacharyfox.rmonitor.utils.Duration;

public class DurationTest
{
	@Test
	public void testEmpty()
	{
		Duration duration = new Duration();
		
		assertTrue(duration.isEmpty());
		assertEquals(0, duration.toInt());
	}
	
	@Test
	public void testEquals()
	{
		assertEquals(new Duration("01:00:00.000"), new Duration("01:00:00"));
		assertEquals(new Duration("01:00:00.000"), new Duration(3600));
		assertEquals(new Duration("01:00:00.123"), new Duration((float) 3600.123));
	}

	@Test
	public void testFloat()
	{
		Float floatVal = (float) 3600.123;
		int intVal = 3600;
		String stringVal = "1:00:00.123";
		
		Duration duration = new Duration(floatVal);

		assertEquals(intVal, duration.toInt());
		assertEquals(floatVal, duration.toFloat());
		assertEquals(stringVal, duration.toString());
	}

	@Test
	public void testInt()
	{
		Float floatVal = (float) 3600.0;
		int intVal = 3600;
		String stringVal = "1:00:00";
		
		Duration duration = new Duration(intVal);

		assertEquals(intVal, duration.toInt());
		assertEquals(floatVal, duration.toFloat());
		assertEquals(stringVal, duration.toString());
	}

	@Test
	public void testLt()
	{
		Duration duration = new Duration();
		Duration duration_1 = new Duration(1);
		
		assertTrue(duration.lt(duration_1));
	}
	
	@Test
	public void testString()
	{
		Float floatVal = (float) 3600.123;
		int intVal = 3600;
		String stringVal = "01:00:00.123";
		
		Duration duration = new Duration(stringVal);

		assertEquals(intVal, duration.toInt());
		assertEquals(floatVal, duration.toFloat());
		assertEquals("1:00:00.123", duration.toString());
	}
	
	@Test
	public void testString2()
	{
		Float floatVal = (float) 3600.0;
		int intVal = 3600;
		String stringVal = "01:00:00";
		
		Duration duration = new Duration(stringVal);

		assertEquals(intVal, duration.toInt());
		assertEquals(floatVal, duration.toFloat());
		assertEquals("1:00:00", duration.toString());
	} 

}
