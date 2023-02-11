package com.zacharyfox.rmonitor.leaderboard;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import com.zacharyfox.rmonitor.entities.Competitor;
import com.zacharyfox.rmonitor.entities.Competitors;
import com.zacharyfox.rmonitor.entities.RaceClass;

@SuppressWarnings("serial")
public class LeaderBoardTableModel extends AbstractTableModel {

	private String[] columnNames = { "Pos", "PIC", "#", "Class", "Name", "Laps", "Total Time", "Last Time", "Best Time",
			"Avg. Time" };

	private ArrayList<Object[]> data = new ArrayList<>();

	public LeaderBoardTableModel() {
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
		if (data.size() > row) {
			return data.get(row)[col];
		}
		return null;
	}

	void updateData() {
		ArrayList<Object[]> rows = new ArrayList<>();

		for (Competitor competitor : Competitors.getCompetitors()) {
			rows.add(getRow(competitor));
		}

		data = rows;
		if (!data.isEmpty()) {
			fireTableDataChanged();
		}
	}

	private Object[] getRow(Competitor competitor) {
		return new Object[] { (competitor.getPosition() == 0) ? 9999 : competitor.getPosition(),
				competitor.calcPositionInClass(), competitor.getRegNumber(),
				RaceClass.getClassName(competitor.getClassId()),
				competitor.getFirstName() + " " + competitor.getLastName(), competitor.getLapsComplete(),
				competitor.getTotalTime(), competitor.getLastLap(), competitor.getBestLap(), competitor.calcAvgLap(),
				"" };
	}
}
