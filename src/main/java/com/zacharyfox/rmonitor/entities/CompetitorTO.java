package com.zacharyfox.rmonitor.entities;

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

	public CompetitorTO(String number, int position, int lapsComplete, String firstName, String lastName,
			String totalTime, String bestLap, String lastLap, int qualiPosition) {
		this.number = number;
		this.position = position;
		this.lapsComplete = lapsComplete;
		this.firstName = firstName;
		this.lastName = lastName;
		this.totalTime = totalTime;
		this.bestLap = bestLap;
		this.lastLap = lastLap;
		this.qualiPosition = qualiPosition;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getLapsComplete() {
		return lapsComplete;
	}

	public void setLapsComplete(int lapsComplete) {
		this.lapsComplete = lapsComplete;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	public String getBestLap() {
		return bestLap;
	}

	public void setBestLap(String bestLap) {
		this.bestLap = bestLap;
	}

	public String getLastLap() {
		return lastLap;
	}

	public void setLastLap(String lastLap) {
		this.lastLap = lastLap;
	}

	public int getQualiPosition() {
		return qualiPosition;
	}

	public void setQualiPosition(int qualiPosition) {
		this.qualiPosition = qualiPosition;
	}
}