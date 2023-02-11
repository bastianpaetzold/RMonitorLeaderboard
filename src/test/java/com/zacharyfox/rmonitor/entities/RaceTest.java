package com.zacharyfox.rmonitor.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.zacharyfox.rmonitor.entities.Race.FlagStatus;
import com.zacharyfox.rmonitor.message.Heartbeat;
import com.zacharyfox.rmonitor.message.RaceInfo;
import com.zacharyfox.rmonitor.message.RunInfo;
import com.zacharyfox.rmonitor.message.SettingInfo;
import com.zacharyfox.rmonitor.utils.DurationUtil;

class RaceTest {

	@Test
	void testUpdateHeartbeat() {
		Heartbeat message = new Heartbeat(new String[] { "$F", "14", "00:12:45", "13:34:23", "00:09:47", "Green" });
		Race race = new Race();

		race.update(message);

		assertEquals(DurationUtil.parse("00:09:47"), race.getElapsedTime());
		assertEquals(14, race.getLapsToGo());
		assertEquals(DurationUtil.parse("00:12:45"), race.getTimeToGo());
		assertEquals(DurationUtil.parse("00:22:32"), race.getScheduledTime());
		assertEquals(DurationUtil.parse("13:34:23"), race.getTimeOfDay());
		assertEquals(FlagStatus.GREEN, race.getFlagStatus());
	}

	@Test
	void testUpdateRaceInfo() {
		RaceInfo message = new RaceInfo(new String[] { "$G", "3", "1234BE", "14", "01:12:47.872" });
		Race race = new Race();

		race.update(message);

		assertEquals(1, race.getCompetitorsVersion());
	}

	@Test
	void testUpdateRunInfo() {
		RunInfo message = new RunInfo(new String[] { "$B", "5", "Friday free practice" });
		Race race = new Race();

		race.update(message);

		assertEquals("Friday free practice", race.getName());
		assertEquals(5, race.getId());
	}

	@Test
	void testUpdateSettingInfo() {
		SettingInfo message1 = new SettingInfo(new String[] { "$E", "TRACKNAME", "Indianapolis Motor Speedway" });
		SettingInfo message2 = new SettingInfo(new String[] { "$E", "TRACKLENGTH", "2.500" });
		Race race = new Race();

		race.update(message1);
		race.update(message2);

		assertEquals("Indianapolis Motor Speedway", race.getTrackName());
		assertEquals(2.5F, race.getTrackLength());
	}
}
