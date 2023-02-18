package com.zacharyfox.rmonitor.leaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.zacharyfox.rmonitor.entities.Competitor;
import com.zacharyfox.rmonitor.entities.RaceManager;

@SuppressWarnings("serial")
public class FinishlineLogTableModel extends AbstractTableModel {

	private String[] columnNames = { "No.", "Laps", "Name", "Pos" };

	private transient List<Object[]> data;

	public FinishlineLogTableModel() {
		data = new ArrayList<>();
		data.add(new Object[] { "", "", "", "" });
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
		// @formatter:off
		data = RaceManager.getInstance().getCurrentRace().getCompetitors().stream()
				.filter(c -> c.getLapsComplete() > 0)
				.sorted(Comparator.comparing(Competitor::getTotalTime))
				.map(this::createRow)
				.toList();
		// @formatter:on

		if (!data.isEmpty()) {
			fireTableDataChanged();
		}
	}

	private Object[] createRow(Competitor competitor) {
		return new Object[] { competitor.getRegNumber(), competitor.getLapsComplete(),
				competitor.getFirstName() + " " + competitor.getLastName(), competitor.getPosition(), "" };
	}
}
