package com.zacharyfox.rmonitor.leaderboard;

import java.time.Duration;

import javax.swing.JTable;

@SuppressWarnings("serial")
public class FinishLineLogTable extends JTable {

	private FinishlineLogTableModel finishLineLogTableModel = new FinishlineLogTableModel();

	public FinishLineLogTable(int rowHeight) {
		setRowHeight(rowHeight);
		setModel(finishLineLogTableModel);
		setDefaultRenderer(Duration.class, new LeaderBoardTableCellRenderer());

		initColumns();
	}

	private void initColumns() {
		int smallColumnSize = 10 * rowHeight;
		int smallColumnMaxSize = 12 * rowHeight;
		int bigColumnSize = 60 * rowHeight;

		int[] smallColumns = { 0, 1, 3 };
		int[] bigColumns = { 2 };

		for (int column : smallColumns) {
			getColumnModel().getColumn(column).setPreferredWidth(smallColumnSize);
			getColumnModel().getColumn(column).setMaxWidth(smallColumnMaxSize);

		}

		for (int column : bigColumns) {
			getColumnModel().getColumn(column).setPreferredWidth(bigColumnSize);
		}
	}
}
