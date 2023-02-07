package com.zacharyfox.rmonitor.entities;

import java.time.Duration;

public class Lap {

	private int lapNumber;
	private Duration lapTime;
	private int position;
	private Duration totalTime;

	public int getLapNumber() {
		return lapNumber;
	}

	public void setLapNumber(int lapNumber) {
		this.lapNumber = lapNumber;
	}

	public Duration getLapTime() {
		return lapTime;
	}

	public void setLapTime(Duration lapTime) {
		this.lapTime = lapTime;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Duration getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(Duration totalTime) {
		this.totalTime = totalTime;
	}
}