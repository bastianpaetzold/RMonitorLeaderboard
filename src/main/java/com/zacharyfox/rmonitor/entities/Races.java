package com.zacharyfox.rmonitor.entities;

import java.util.HashMap;
import java.util.Map;

public class Races {

	private static final Map<Integer, RaceTO> raceMap = new HashMap<>();

	private Races() {
	}

	public static void addRace(Race race) {
		RaceTO raceTO = RaceTO.from(race);

		if (race.getId() != 0) {
			raceMap.put(race.getId(), raceTO);
		}
	}

	public static RaceTO getRaceTOById(int raceId) {
		return raceMap.get(raceId);
	}

	public static RaceTO[] getAllRaceTOs() {
		return raceMap.values().toArray(new RaceTO[0]);
	}
}
