package com.zacharyfox.rmonitor.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.zacharyfox.rmonitor.message.CompInfo;
import com.zacharyfox.rmonitor.message.LapInfo;
import com.zacharyfox.rmonitor.message.PassingInfo;
import com.zacharyfox.rmonitor.message.QualiInfo;
import com.zacharyfox.rmonitor.message.RaceInfo;
import com.zacharyfox.rmonitor.utils.DurationUtil;

class CompetitorTest {

	private static final String TEST_COMPETITOR_REG_NUMBER = "1234BE";

	private static RaceManager manager;

	@BeforeEach
	void prepare() {
		manager = new RaceManager();

		String[] tokens = { "$A", TEST_COMPETITOR_REG_NUMBER, "12X", "52474", "John", "Johnson", "USA", "5" };
		CompInfo message = new CompInfo(tokens);
		manager.processMessage(message);
	}

	@Test
	void testAddLaps() {
		final Competitor competitor = getTestCompetitor();

		playLapMessages();

		List<Lap> laps = competitor.getLaps();

		assertEquals(1, laps.get(0).getLapNumber());
		assertEquals(3, laps.get(0).getPosition());
		assertEquals(DurationUtil.parse("00:01:47.872"), laps.get(0).getLapTime());
		assertEquals(DurationUtil.parse("00:01:47.872"), laps.get(0).getTotalTime());

		assertEquals(2, laps.get(1).getLapNumber());
		assertEquals(2, laps.get(1).getPosition());
		assertEquals(DurationUtil.parse("00:01:46.749"), laps.get(1).getLapTime());
		assertEquals(DurationUtil.parse("00:03:34.621"), laps.get(1).getTotalTime());
	}

	@Test
	void testCalcAvgLap() {
		final Competitor competitor = getTestCompetitor();

		assertEquals(Duration.ZERO, competitor.getAvgLap());

		playLapMessages();

		assertEquals(DurationUtil.parse("00:01:47.3105"), competitor.getAvgLap());
	}

	@Test
	void testGetCompetitorByPosition() {
		final Competitor competitor = getTestCompetitor();
		playLapMessages();

		assertEquals(competitor, manager.getCurrentRace().getCompetitorByPosition(2));
	}

	@Test
	void testGetFastestLap() {
		playLapMessages();
		assertEquals(DurationUtil.parse("00:01:46.749"), manager.getCurrentRace().getFastestLap());
	}

	@Test
	void testCalcPositionInClass() {
		final Competitor competitor = getTestCompetitor();
		playLapMessages();

		assertEquals(1, competitor.calcPositionInClass());
	}

	@Test
	void testUpdateCompInfo() {
		final Competitor competitor = getTestCompetitor();

		String[] tokens = { "$A", TEST_COMPETITOR_REG_NUMBER, "123", "54321", "Jack", "Jackson", "MX", "6" };
		CompInfo message = new CompInfo(tokens);

		manager.processMessage(message);

		assertEquals("123", competitor.getNumber());
		assertEquals("54321", competitor.getTransNumber());
		assertEquals("Jack", competitor.getFirstName());
		assertEquals("Jackson", competitor.getLastName());
		assertEquals("MX", competitor.getNationality());
		assertEquals(6, competitor.getClassId());
	}

	@Test
	void testUpdateLapInfo() {
		final Competitor competitor = getTestCompetitor();

		String[] tokens1 = { "$SP", "3", TEST_COMPETITOR_REG_NUMBER, "2", "00:01:33.894", "76682" };
		LapInfo message1 = new LapInfo(tokens1);

		manager.processMessage(message1);

		for (Lap lap : competitor.getLaps()) {
			if (lap.getLapNumber() == 2) {
				assertEquals(lap.getLapTime(), DurationUtil.parse("00:01:33.894"));
			}
		}

		String[] tokens2 = { "$SP", "3", TEST_COMPETITOR_REG_NUMBER, "3", "00:01:31.123", "76682" };
		LapInfo message2 = new LapInfo(tokens2);

		manager.processMessage(message2);

		assertEquals(DurationUtil.parse("00:01:31.123"), competitor.getBestLap());
	}

	@Test
	void testUpdatePassingInfo() {
		final Competitor competitor = getTestCompetitor();

		String[] tokens = { "$J", TEST_COMPETITOR_REG_NUMBER, "01:12:47.872", "01:12:47.872" };
		PassingInfo message = new PassingInfo(tokens);

		manager.processMessage(message);

		assertEquals(DurationUtil.parse("01:12:47.872"), competitor.getLastLap());
		assertEquals(DurationUtil.parse("01:12:47.872"), competitor.getTotalTime());
	}

	@Test
	void testUpdateQualInfo() {
		final Competitor competitor = getTestCompetitor();

		String[] tokens = { "$H", "1", TEST_COMPETITOR_REG_NUMBER, "1", "01:12:47.872" };
		QualiInfo message = new QualiInfo(tokens);

		manager.processMessage(message);

		assertEquals(DurationUtil.parse("01:12:47.872"), competitor.getBestLap());
	}

	@Test
	void testUpdateRaceInfo() {
		final Competitor competitor = getTestCompetitor();

		String[] tokens = { "$G", "3", TEST_COMPETITOR_REG_NUMBER, "14", "01:12:47.872" };
		RaceInfo message = new RaceInfo(tokens);

		manager.processMessage(message);

		assertEquals(3, competitor.getPosition());
		assertEquals(14, competitor.getLapsComplete());
		assertEquals(DurationUtil.parse("01:12:47.872"), competitor.getTotalTime());
	}

	private Competitor getTestCompetitor() {
		return manager.getCurrentRace().getCompetitor(TEST_COMPETITOR_REG_NUMBER);
	}

	private void playLapMessages() {
		String[] tokens = { "$G", "3", TEST_COMPETITOR_REG_NUMBER, "1", "00:01:47.872" };
		RaceInfo message = new RaceInfo(tokens);

		String[] tokens2 = { "$J", TEST_COMPETITOR_REG_NUMBER, "00:01:47.872", "00:01:47.872" };
		PassingInfo message2 = new PassingInfo(tokens2);

		String[] tokens3 = { "$J", TEST_COMPETITOR_REG_NUMBER, "00:01:46.749", "00:03:34.621" };
		PassingInfo message3 = new PassingInfo(tokens3);

		String[] tokens4 = { "$G", "2", TEST_COMPETITOR_REG_NUMBER, "2", "00:03:34.621" };
		RaceInfo message4 = new RaceInfo(tokens4);

		manager.processMessage(message);
		manager.processMessage(message2);
		manager.processMessage(message3);
		manager.processMessage(message4);
	}
}
