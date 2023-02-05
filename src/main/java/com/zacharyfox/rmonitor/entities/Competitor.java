package com.zacharyfox.rmonitor.entities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zacharyfox.rmonitor.message.CompInfo;
import com.zacharyfox.rmonitor.message.LapInfo;
import com.zacharyfox.rmonitor.message.PassingInfo;
import com.zacharyfox.rmonitor.message.QualInfo;
import com.zacharyfox.rmonitor.message.RaceInfo;
import com.zacharyfox.rmonitor.message.RegistrationInfo;
import com.zacharyfox.rmonitor.utils.DurationUtil;

public class Competitor {

	public class Lap {

		public int lapNumber;
		public Duration lapTime;
		public int position;
		public Duration totalTime;
	}

	private String addData = "";
	private Duration bestLap = Duration.ZERO;
	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	private int classId = 0;
	private String firstName = "";
	private final ArrayList<Competitor.Lap> laps = new ArrayList<>();
	private int lapsComplete = 0;
	private Duration lastLap = Duration.ZERO;
	private String lastName = "";
	private String nationality = "";
	private String number = "";
	private int position = 0;
	private int qualiPosition = 0;
	private String regNumber = "";
	private Duration totalTime = Duration.ZERO;
	private String transNumber = "";
	private static HashMap<String, Competitor> instances = new HashMap<>();

	private Competitor() {

	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(l);
	}

	public void addPropertyChangeListener(String property, PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(property, l);
	}

	public String getAddData() {
		return addData;
	}

	public Duration getAvgLap() {
		Duration duration = Duration.ZERO;

		int count = 0;
		for (Competitor.Lap lap : laps) {
			if (lap.lapTime != null && lap.lapNumber > 0) {
				duration = duration.plus(lap.lapTime);
				count++;
			}
		}

		if (count > 0) {
			duration = duration.dividedBy(count);
		}

		return duration;
	}

	public Duration getBestLap() {
		return bestLap;
	}

	public int getClassId() {
		return classId;
	}

	public String getFirstName() {
		return firstName;
	}

	public List<Competitor.Lap> getLaps() {
		return laps;
	}

	public int getLapsComplete() {
		return lapsComplete;
	}

	public Duration getLastLap() {
		return lastLap;
	}

	public String getLastName() {
		return lastName;
	}

	public String getNationality() {
		return nationality;
	}

	public String getNumber() {
		return number;
	}

	public int getPosition() {
		return position;
	}

	public int getQualiPosition() {
		return qualiPosition;
	}

	public int getPositionInClass() {
		int positionInClass = 1;

		for (Competitor competitor : instances.values()) {
			if (competitor.classId == classId && competitor.position < position) {
				positionInClass += 1;
			}
		}

		return positionInClass;
	}

	public String getRegNumber() {
		return regNumber;
	}

	public Duration getTotalTime() {
		return totalTime;
	}

	public String getTransNumber() {
		return transNumber;
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(l);
	}

	public void removePropertyChangeListener(String property, PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(property, l);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Competitor.Lap lap : laps) {
			builder.append(String.format("%s %s %s %s%n", lap.lapNumber, lap.position, DurationUtil.format(lap.lapTime),
					DurationUtil.format(lap.totalTime)));
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

	private void addLap(LapInfo message) {
		boolean found = false;

		for (Competitor.Lap lap : this.laps) {
			if (lap.lapNumber == message.getLapNumber()) {
				found = true;
				lap.lapTime = message.getLapTime();
				lap.position = message.getPosition();
			}
		}

		if (!found) {
			Competitor.Lap lap = new Competitor.Lap();
			lap.lapNumber = message.getLapNumber();
			lap.position = message.getPosition();
			lap.lapTime = message.getLapTime();

			this.laps.add(lap);
		}
	}

	private void addLap(PassingInfo message) {
		boolean found = false;

		for (Competitor.Lap lap : this.laps) {
			if (lap != null && lap.totalTime != null && lap.totalTime.equals(message.getTotalTime())) {
				found = true;
				lap.lapTime = message.getLapTime();
			}
		}

		if (!found) {
			Competitor.Lap lap = new Competitor.Lap();
			lap.lapTime = message.getLapTime();
			lap.totalTime = message.getTotalTime();
			this.laps.add(lap);
		}
	}

	private void addLap(RaceInfo message) {
		boolean found = false;
		for (Competitor.Lap lap : this.laps) {
			if (lap != null && lap.totalTime != null && lap.totalTime.equals(message.getTotalTime())) {
				found = true;
				lap.totalTime = message.getTotalTime();
				lap.lapNumber = message.getLaps();
				lap.position = message.getPosition();
			}
		}

		if (!found) {
			Competitor.Lap lap = new Competitor.Lap();
			lap.totalTime = message.getTotalTime();
			lap.lapNumber = message.getLaps();
			lap.position = message.getPosition();
			this.laps.add(lap);
		}
	}

	private void messageUpdate(CompInfo message) {
		setRegNumber(message.getRegNumber());
		setNumber(message.getNumber());
		setTransNumber(message.getTransNumber());
		setFirstName(message.getFirstName());
		setLastName(message.getLastName());
		setClassId(message.getClassId());

		if (!"".equals(message.getNationality())) {
			this.setNationality(message.getNationality());
		}

		if (!"".equals(message.getAddInfo())) {
			this.setAddData(message.getAddInfo());
		}
	}

	private void messageUpdate(LapInfo message) {
		this.addLap(message);

		if (message.getLapNumber() == lapsComplete) {
			setLastLap(message.getLapTime());
		}

		setBestLap(message.getLapTime());
	}

	private void messageUpdate(PassingInfo message) {
		addLap(message);

		setLastLap(message.getLapTime());
		setBestLap(message.getLapTime());
		setTotalTime(message.getTotalTime());
	}

	private void messageUpdate(QualInfo message) {
		setRegNumber(message.getRegNumber());
		setQualiPosition(message.getPosition());
		setBestLap(message.getBestLapTime());
	}

	private void messageUpdate(RaceInfo message) {
		addLap(message);

		setRegNumber(message.getRegNumber());
		setPosition(message.getPosition());
		setLapsComplete(message.getLaps());
		setTotalTime(message.getTotalTime());
	}

	private void setAddData(String addData) {
		String oldAddData = this.addData;
		this.addData = addData;
		changeSupport.firePropertyChange("addData", oldAddData, this.addData);
	}

	private void setBestLap(Duration bestLap) {
		if (this.bestLap.isZero() || bestLap.compareTo(this.bestLap) < 0) {
			Duration oldBestLap = this.bestLap;
			this.bestLap = bestLap;
			changeSupport.firePropertyChange("bestLap", oldBestLap, this.bestLap);
		}
	}

	private void setClassId(int classId) {
		int oldClassId = this.classId;
		this.classId = classId;
		changeSupport.firePropertyChange("classId", oldClassId, this.classId);
	}

	private void setFirstName(String firstName) {
		String oldName = this.firstName;
		this.firstName = firstName;
		changeSupport.firePropertyChange("firstName", oldName, firstName);
	}

	private void setLapsComplete(int lapsComplete) {
		int oldLapsComplete = this.lapsComplete;
		if (oldLapsComplete != lapsComplete) {
			this.lapsComplete = lapsComplete;
			changeSupport.firePropertyChange("lapsComplete", oldLapsComplete, this.lapsComplete);
		}
	}

	private void setLastLap(Duration lastLap) {
		Duration oldLastLap = this.lastLap;
		this.lastLap = lastLap;
		changeSupport.firePropertyChange("lastLap", oldLastLap, this.lastLap);
	}

	private void setLastName(String lastName) {
		String oldLastName = this.lastName;
		this.lastName = lastName;
		changeSupport.firePropertyChange("lastName", oldLastName, this.lastName);
	}

	private void setNationality(String nationality) {
		String oldNationality = this.nationality;
		this.nationality = nationality;
		changeSupport.firePropertyChange("nationality", oldNationality, this.nationality);
	}

	private void setNumber(String number) {
		String oldNumber = this.number;
		this.number = number;
		changeSupport.firePropertyChange("number", oldNumber, this.number);
	}

	private void setPosition(int position) {
		int oldPosition = this.position;
		this.position = position;
		changeSupport.firePropertyChange("position", oldPosition, this.position);
	}

	private void setQualiPosition(int qualiPosition) {
		int oldQualiPosition = this.qualiPosition;
		this.qualiPosition = qualiPosition;
		changeSupport.firePropertyChange("qualiPosition", oldQualiPosition, this.qualiPosition);
	}

	private void setRegNumber(String regNumber) {
		String oldRegNumber = this.regNumber;
		this.regNumber = regNumber;
		changeSupport.firePropertyChange("regNumber", oldRegNumber, this.regNumber);
	}

	private void setTotalTime(Duration totalTime) {
		Duration oldTotalTime = this.totalTime;
		this.totalTime = totalTime;
		changeSupport.firePropertyChange("totalTime", oldTotalTime, this.totalTime);
	}

	private void setTransNumber(String transNumber) {
		String oldTransNumber = this.transNumber;
		this.transNumber = transNumber;
		changeSupport.firePropertyChange("transNumber", oldTransNumber, this.transNumber);
	}

	public static Competitor getByPosition(int position) {
		for (Competitor competitor : instances.values()) {
			if (competitor.position == position) {
				return competitor;
			}
		}
		return null;
	}

	public static Duration getFastestLap() {
		Duration fastestLap = Duration.ZERO;
		Duration competitorBestLap;

		for (Competitor competitor : instances.values()) {
			competitorBestLap = competitor.getBestLap();
			if (competitorBestLap.isZero()) {
				continue;
			}

			if (fastestLap.isZero() || competitorBestLap.compareTo(fastestLap) < 0) {
				fastestLap = competitorBestLap;
			}
		}

		return fastestLap;
	}

	public static Competitor getInstance(String regNumber) {
		return instances.get(regNumber);
	}

	public static Map<String, Competitor> getInstances() {
		return instances;
	}

	public static void reset() {
		instances = new HashMap<>();
	}

	public static void updateOrCreate(RegistrationInfo info) {
		Competitor instance = Competitor.getInstance(info.getRegNumber());

		instance = (instance == null) ? new Competitor() : instance;

		if (info.getClass() == RaceInfo.class) {
			instance.messageUpdate((RaceInfo) info);
		} else if (info.getClass() == CompInfo.class) {
			instance.messageUpdate((CompInfo) info);
		} else if (info.getClass() == LapInfo.class) {
			instance.messageUpdate((LapInfo) info);
		} else if (info.getClass() == QualInfo.class) {
			instance.messageUpdate((QualInfo) info);
		} else if (info.getClass() == PassingInfo.class) {
			instance.messageUpdate((PassingInfo) info);
		}

		instances.put(instance.getRegNumber(), instance);
	}

	public static void setCompetitorTO(RaceTO raceTO) {
		RaceTO.CompetitorTO[] competitors = new RaceTO.CompetitorTO[instances.size()];
		int i = 0;
		for (Competitor competitor : instances.values()) {
			RaceTO.CompetitorTO competitorTO = raceTO.new CompetitorTO(competitor.number, competitor.position,
					competitor.lapsComplete, competitor.firstName, competitor.lastName,
					DurationUtil.format(competitor.totalTime), DurationUtil.format(competitor.bestLap),
					DurationUtil.format(competitor.lastLap), competitor.qualiPosition);
			competitors[i++] = competitorTO;
		}
		raceTO.competitors = competitors;
	}
}
