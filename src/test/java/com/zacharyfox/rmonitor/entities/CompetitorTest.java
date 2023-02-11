package com.zacharyfox.rmonitor.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.zacharyfox.rmonitor.message.CompInfo;
import com.zacharyfox.rmonitor.message.LapInfo;
import com.zacharyfox.rmonitor.message.PassingInfo;
import com.zacharyfox.rmonitor.message.QualInfo;
import com.zacharyfox.rmonitor.message.RaceInfo;
import com.zacharyfox.rmonitor.utils.DurationUtil;

class CompetitorTest {

	protected boolean bestLapFired = false;
	protected boolean classIdFired = false;
	protected boolean firstNameFired = false;
	protected boolean lapsCompleteFired = false;
	protected boolean lapsFired = false;
	protected boolean lastLapFired = false;
	protected boolean lastNameFired = false;
	protected boolean nationalityFired = false;
	protected boolean numberFired = false;
	protected boolean positionFired = false;
	protected boolean totalTimeFired = false;
	protected boolean transNumberFired = false;

	@Test
	void testAddLap() {
		final Competitor competitor = getCompetitor();

		this.playLapMessages();

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
	void testAvgLap() {
		final Competitor competitor = getCompetitor();

		assertEquals(Duration.ZERO, competitor.calcAvgLap());

		this.playLapMessages();

		assertEquals(DurationUtil.parse("00:01:47.3105"), competitor.calcAvgLap());
	}

	@Test
	void testGetByPosition() {
		final Competitor competitor = getCompetitor();
		this.playLapMessages();

		assertEquals(competitor, Competitors.getCompetitorByPosition(2));
	}

	@Test
	void testGetFastestLap() {
		this.playLapMessages();
		assertEquals(DurationUtil.parse("00:01:46.749"), Competitors.getFastestLap());
	}

	@Test
	void testGetPositionInClass() {
		final Competitor competitor = getCompetitor();

		this.playLapMessages();
		assertEquals(1, competitor.calcPositionInClass());
	}

	@Test
	void testUpdateCompInfo() {
		final Competitor competitor = getCompetitor();

		String[] tokens = { "$A", "1234BE", "123", "54321", "Jack", "Jackson", "MX", "6" };
		CompInfo message = new CompInfo(tokens);

		Competitors.updateOrCreate(message);

		assertEquals("123", competitor.getNumber());
		assertEquals("54321", competitor.getTransNumber());
		assertEquals("Jack", competitor.getFirstName());
		assertEquals("Jackson", competitor.getLastName());
		assertEquals("MX", competitor.getNationality());
		assertEquals(6, competitor.getClassId());
	}

	@Test
	void testUpdateLapInfo() {
		final Competitor competitor = getCompetitor();

		String[] tokens1 = { "$SP", "3", "1234BE", "2", "00:01:33.894", "76682" };
		LapInfo message1 = new LapInfo(tokens1);

		Competitors.updateOrCreate(message1);

		for (Lap lap : competitor.getLaps()) {
			if (lap.getLapNumber() == 2) {
				assertEquals(lap.getLapTime(), DurationUtil.parse("00:01:33.894"));
			}
		}

		String[] tokens2 = { "$SP", "3", "1234BE", "3", "00:01:31.123", "76682" };
		LapInfo message2 = new LapInfo(tokens2);

		Competitors.updateOrCreate(message2);

		assertEquals(DurationUtil.parse("00:01:31.123"), competitor.getBestLap());
	}

	@Test
	void testUpdatePassingInfo() {
		final Competitor competitor = getCompetitor();

		String[] tokens = { "$J", "1234BE", "01:12:47.872", "01:12:47.872" };
		PassingInfo message = new PassingInfo(tokens);

		Competitors.updateOrCreate(message);

		assertEquals(DurationUtil.parse("01:12:47.872"), competitor.getLastLap());
		assertEquals(DurationUtil.parse("01:12:47.872"), competitor.getTotalTime());
	}

	@Test
	void testUpdateQualInfo() {
		final Competitor competitor = getCompetitor();

		String[] tokens = { "$H", "1", "1234BE", "1", "01:12:47.872" };
		QualInfo message = new QualInfo(tokens);

		Competitors.updateOrCreate(message);

		assertEquals(DurationUtil.parse("01:12:47.872"), competitor.getBestLap());
	}

	@Test
	void testUpdateRaceInfo() {
		final Competitor competitor = getCompetitor();

		String[] tokens = { "$G", "3", "1234BE", "14", "01:12:47.872" };
		RaceInfo message = new RaceInfo(tokens);

		Competitors.updateOrCreate(message);

		assertEquals(3, competitor.getPosition());
		assertEquals(14, competitor.getLapsComplete());
		assertEquals(DurationUtil.parse("01:12:47.872"), competitor.getTotalTime());
	}

	private Competitor getCompetitor() {
		Competitors.reset();

		String[] tokens = { "$A", "1234BE", "12X", "52474", "John", "Johnson", "USA", "5" };

		CompInfo message = new CompInfo(tokens);
		Competitors.updateOrCreate(message);

		return Competitors.getCompetitor("1234BE");
	}

	private void playLapMessages() {
		String[] tokens = { "$G", "3", "1234BE", "1", "00:01:47.872" };

		RaceInfo message = new RaceInfo(tokens);

		String[] tokens2 = { "$J", "1234BE", "00:01:47.872", "00:01:47.872" };

		PassingInfo message2 = new PassingInfo(tokens2);

		String[] tokens3 = { "$J", "1234BE", "00:01:46.749", "00:03:34.621" };

		PassingInfo message3 = new PassingInfo(tokens3);

		String[] tokens4 = { "$G", "2", "1234BE", "2", "00:03:34.621" };

		RaceInfo message4 = new RaceInfo(tokens4);

		Competitors.updateOrCreate(message);
		Competitors.updateOrCreate(message2);
		Competitors.updateOrCreate(message3);
		Competitors.updateOrCreate(message4);
	}
}
