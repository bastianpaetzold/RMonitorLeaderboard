package com.zacharyfox.rmonitor.entities;

import com.zacharyfox.rmonitor.utils.DurationUtil;

public class CompetitorTO {

	private String number;
	private int position;
	private int lapsComplete;
	private String firstName;
	private String lastName;
	private String totalTime;
	private String bestLap;
	private String lastLap;
	private int qualiPosition;

	private CompetitorTO() {
	}

	public String getNumber() {
		return number;
	}

	public int getPosition() {
		return position;
	}

	public int getLapsComplete() {
		return lapsComplete;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public String getBestLap() {
		return bestLap;
	}

	public String getLastLap() {
		return lastLap;
	}

	public int getQualiPosition() {
		return qualiPosition;
	}

	public static CompetitorTO from(Competitor competitor) {
		CompetitorTO competitorTO = new CompetitorTO();
		competitorTO.number = competitor.getNumber();
		competitorTO.position = competitor.getPosition();
		competitorTO.lapsComplete = competitor.getLapsComplete();
		competitorTO.firstName = competitor.getFirstName();
		competitorTO.lastName = competitor.getLastName();
		competitorTO.totalTime = DurationUtil.format(competitor.getTotalTime());
		competitorTO.bestLap = DurationUtil.format(competitor.getBestLap());
		competitorTO.lastLap = DurationUtil.format(competitor.getLastLap());
		competitorTO.qualiPosition = competitor.getQualiPosition();

		return competitorTO;
	}
}