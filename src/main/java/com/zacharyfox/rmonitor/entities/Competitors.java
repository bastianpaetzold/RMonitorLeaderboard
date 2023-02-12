package com.zacharyfox.rmonitor.entities;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.zacharyfox.rmonitor.message.CompInfo;
import com.zacharyfox.rmonitor.message.LapInfo;
import com.zacharyfox.rmonitor.message.PassingInfo;
import com.zacharyfox.rmonitor.message.QualInfo;
import com.zacharyfox.rmonitor.message.RaceInfo;
import com.zacharyfox.rmonitor.message.RegistrationInfo;

public class Competitors {

	private static final Map<String, Competitor> competitorMap = new HashMap<>();

	private Competitors() {
	}

	public static void updateOrCreate(RegistrationInfo info) {
		Competitor instance;
		synchronized (competitorMap) {
			instance = competitorMap.computeIfAbsent(info.getRegNumber(), k -> new Competitor());
		}

		if (info.getClass() == RaceInfo.class) {
			instance.update((RaceInfo) info);
		} else if (info.getClass() == CompInfo.class) {
			instance.update((CompInfo) info);
		} else if (info.getClass() == LapInfo.class) {
			instance.update((LapInfo) info);
		} else if (info.getClass() == QualInfo.class) {
			instance.update((QualInfo) info);
		} else if (info.getClass() == PassingInfo.class) {
			instance.update((PassingInfo) info);
		}
	}

	public static void reset() {
		synchronized (competitorMap) {
			competitorMap.clear();
		}
	}

	/**
	 * @return an unmodifiable copy of the competitors currently stored
	 */
	public static List<Competitor> getCompetitors() {
		synchronized (competitorMap) {
			return Collections.unmodifiableList(new ArrayList<>(competitorMap.values()));
		}
	}

	public static CompetitorTO[] getCompetitorsAsTO() {
		synchronized (competitorMap) {
			return competitorMap.values().stream().map(CompetitorTO::from).toArray(CompetitorTO[]::new);
		}
	}

	public static Competitor getCompetitor(String regNumber) {
		return competitorMap.get(regNumber);
	}

	public static Competitor getCompetitorByPosition(int position) {
		synchronized (competitorMap) {
			// @formatter:off
			return competitorMap.values().stream()
					.filter(c -> c.getPosition() == position)
					.findFirst()
					.orElse(null);
			// @formatter:on
		}
	}

	public static Duration getFastestLap() {
		synchronized (competitorMap) {
			// @formatter:off
			return competitorMap.values().stream()
					.map(Competitor::getBestLap)
					.filter(Predicate.not(Duration::isZero))
					.sorted()
					.findFirst()
					.orElse(Duration.ZERO);
			// @formatter:on
		}
	}

	public static long calcPositionInClass(Competitor competitor) {
		synchronized (competitorMap) {
			// @formatter:off
			return competitorMap.values().stream()
					.filter(c -> c.getClassId() == competitor.getClassId() && c.getPosition() < competitor.getPosition())
					.count() + 1;
			// @formatter:on
		}
	}
}
