package com.zacharyfox.rmonitor.entities;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.zacharyfox.rmonitor.message.CompInfo;
import com.zacharyfox.rmonitor.message.LapInfo;
import com.zacharyfox.rmonitor.message.PassingInfo;
import com.zacharyfox.rmonitor.message.QualInfo;
import com.zacharyfox.rmonitor.message.RaceInfo;
import com.zacharyfox.rmonitor.utils.DurationUtil;

public class Competitor {

	private String regNumber;
	private String transNumber;
	private String number;
	private String firstName;
	private String lastName;
	private String nationality;
	private int classId;
	private String addData;

	private Duration totalTime;
	private Duration bestLap;
	private Duration lastLap;
	private Duration avgLap;
	private int lapsComplete;
	private int position;
	private int qualiPosition;
	private List<Lap> laps;

	Competitor() {
		regNumber = "";
		transNumber = "";
		number = "";
		firstName = "";
		lastName = "";
		nationality = "";
		classId = 0;
		addData = "";
		bestLap = Duration.ZERO;
		totalTime = Duration.ZERO;
		lastLap = Duration.ZERO;
		avgLap = Duration.ZERO;
		lapsComplete = 0;
		position = 0;
		qualiPosition = 0;
		laps = new ArrayList<>();
	}

	public String getRegNumber() {
		return regNumber;
	}

	public String getTransNumber() {
		return transNumber;
	}

	public String getNumber() {
		return number;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getNationality() {
		return nationality;
	}

	public int getClassId() {
		return classId;
	}

	public String getAddData() {
		return addData;
	}

	public Duration getTotalTime() {
		return totalTime;
	}

	public Duration getBestLap() {
		return bestLap;
	}

	public Duration getLastLap() {
		return lastLap;
	}

	public Duration getAvgLap() {
		return avgLap;
	}

	public int getLapsComplete() {
		return lapsComplete;
	}

	public int getPosition() {
		return position;
	}

	public int getQualiPosition() {
		return qualiPosition;
	}

	/*
	 * Only for testing
	 */
	List<Lap> getLaps() {
		return laps;
	}

	public long calcPositionInClass() {
		// TODO the position is wrong if the competitor is not part of the current race
		return RaceManager.getInstance().getCurrentRace().calcPositionInClass(this);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Lap lap : laps) {
			builder.append(String.format("%s %s %s %s%n", lap.getLapNumber(), lap.getPosition(),
					DurationUtil.format(lap.getLapTime()), DurationUtil.format(lap.getTotalTime())));
		}
		return """
				First Name: %s
				Last Name: %s
				Registration Number: %s
				Number: %s
				Transponder Number: %s
				Class ID: %s
				Position: %s
				Laps: %s
				Total Time: %s
				Best Time: %s
				Laps:
				%s
				""".formatted(firstName, lastName, regNumber, number, transNumber, classId, position, lapsComplete,
				DurationUtil.format(totalTime), DurationUtil.format(bestLap), builder.toString());
	}

	void update(CompInfo compInfo) {
		regNumber = compInfo.getRegNumber();
		number = compInfo.getNumber();
		transNumber = compInfo.getTransNumber();
		firstName = compInfo.getFirstName();
		lastName = compInfo.getLastName();
		classId = compInfo.getClassId();

		if (!"".equals(compInfo.getNationality())) {
			nationality = compInfo.getNationality();
		}

		if (!"".equals(compInfo.getAddInfo())) {
			addData = compInfo.getAddInfo();
		}
	}

	void update(QualInfo qualiInfo) {
		regNumber = qualiInfo.getRegNumber();
		qualiPosition = qualiInfo.getPosition();

		updateBestLap(qualiInfo.getBestLapTime());
	}

	void update(LapInfo lapInfo) {
		updateOrCreateLap(lapInfo);

		if (lapInfo.getLapNumber() == lapsComplete) {
			lastLap = lapInfo.getLapTime();
		}

		updateBestLap(lapInfo.getLapTime());
	}

	void update(PassingInfo passingInfo) {
		Lap lap = findOrCreateLap(passingInfo.getTotalTime());
		lap.setLapTime(passingInfo.getLapTime());

		lastLap = passingInfo.getLapTime();
		totalTime = passingInfo.getTotalTime();

		updateBestLap(passingInfo.getLapTime());
		updateAvgLap();
	}

	void update(RaceInfo raceInfo) {
		Lap lap = findOrCreateLap(raceInfo.getTotalTime());
		lap.setLapNumber(raceInfo.getLaps());
		lap.setPosition(raceInfo.getPosition());

		regNumber = raceInfo.getRegNumber();
		position = raceInfo.getPosition();
		lapsComplete = raceInfo.getLaps();
		totalTime = raceInfo.getTotalTime();

		updateAvgLap();
	}

	private void updateOrCreateLap(LapInfo lapInfo) {
		boolean found = false;

		for (Lap lap : laps) {
			if (lap.getLapNumber() == lapInfo.getLapNumber()) {
				found = true;
				lap.setLapTime(lapInfo.getLapTime());
				lap.setPosition(lapInfo.getPosition());
			}
		}

		if (!found) {
			Lap lap = new Lap();
			lap.setLapNumber(lapInfo.getLapNumber());
			lap.setPosition(lapInfo.getPosition());
			lap.setLapTime(lapInfo.getLapTime());

			laps.add(lap);
		}

		updateAvgLap();
	}

	private Lap findOrCreateLap(Duration totalTime) {
		Lap lap;
		Optional<Lap> optionalLap = laps.stream().filter(l -> totalTime.equals(l.getTotalTime())).findFirst();

		if (optionalLap.isPresent()) {
			lap = optionalLap.get();
		} else {
			lap = new Lap();
			lap.setTotalTime(totalTime);
			laps.add(lap);
		}

		return lap;
	}

	private void updateBestLap(Duration bestLap) {
		if (this.bestLap.isZero() || bestLap.compareTo(this.bestLap) < 0) {
			this.bestLap = bestLap;
		}
	}

	private void updateAvgLap() {
		// @formatter:off
		avgLap = laps.stream()
				.filter(l -> l.getLapNumber() > 0)
				.map(Lap::getLapTime)
				.filter(Objects::nonNull)
				.collect(Collectors.teeing(
								Collectors.reducing(Duration.ZERO, Duration::plus),
								Collectors.counting(),
								(sum, count) -> count > 0 ? sum.dividedBy(count) : Duration.ZERO));
		// @formatter:on
	}
}
