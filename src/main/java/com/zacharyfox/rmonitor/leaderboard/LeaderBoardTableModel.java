package com.zacharyfox.rmonitor.leaderboard;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.zacharyfox.rmonitor.entities.Competitor;
import com.zacharyfox.rmonitor.entities.RaceClass;
import com.zacharyfox.rmonitor.entities.RaceManager;

@SuppressWarnings("serial")
public class LeaderBoardTableModel extends AbstractTableModel {

	private String[] columnNames = { "Pos", "PIC", "#", "Class", "Name", "Laps", "Total Time", "Last Time", "Best Time",
			"Avg. Time" };

	private transient List<Object[]> data;

	public LeaderBoardTableModel() {
		data = new ArrayList<>();
		data.add(new Object[] { "", "", "", "", "", "", "", "", "", "" });
	}

	@Override
	public Class<?> getColumnClass(int c) {
		if (!data.isEmpty()) {
			return getValueAt(0, c).getClass();
		}
		return Object.class;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (row < data.size()) {
			return data.get(row)[col];
		}
		return null;
	}

	void updateData() {
		data = RaceManager.getInstance().getCurrentRace().getCompetitors().stream().map(this::getRow).toList();

		if (!data.isEmpty()) {
			fireTableDataChanged();
		}
	}

	private Object[] getRow(Competitor competitor) {
		return new Object[] { (competitor.getPosition() == 0) ? 9999 : competitor.getPosition(),
				competitor.calcPositionInClass(), competitor.getRegNumber(),
				RaceClass.getClassName(competitor.getClassId()),
				competitor.getFirstName() + " " + competitor.getLastName(), competitor.getLapsComplete(),
				competitor.getTotalTime(), competitor.getLastLap(), competitor.getBestLap(), competitor.getAvgLap(),
				"" };
	}
}
