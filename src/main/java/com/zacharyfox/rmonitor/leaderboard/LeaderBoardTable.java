package com.zacharyfox.rmonitor.leaderboard;

import java.time.Duration;

import javax.swing.JTable;
import javax.swing.table.TableRowSorter;

@SuppressWarnings("serial")
public class LeaderBoardTable extends JTable {

	private LeaderBoardTableModel leaderBoardTableModel = new LeaderBoardTableModel();
	private TableRowSorter<LeaderBoardTableModel> sorter = new TableRowSorter<>(leaderBoardTableModel);

	public LeaderBoardTable() {
		setModel(leaderBoardTableModel);
		setRowSorter(sorter);
		setDefaultRenderer(Duration.class, new LeaderBoardTableCellRenderer());

		initColumns();

		sorter.setSortsOnUpdates(true);
		sorter.toggleSortOrder(0);
	}

	private void initColumns() {
		int smallColumnSize = 40;
		int timeColumnSize = 100;

		int[] smallColumns = { 0, 1, 2, 3, 5 };
		int[] timeColumns = { 6, 7, 8, 9 };

		for (int column : smallColumns) {
			getColumnModel().getColumn(column).setPreferredWidth(smallColumnSize);
		}

		for (int column : timeColumns) {
			getColumnModel().getColumn(column).setPreferredWidth(timeColumnSize);
		}
	}
}
