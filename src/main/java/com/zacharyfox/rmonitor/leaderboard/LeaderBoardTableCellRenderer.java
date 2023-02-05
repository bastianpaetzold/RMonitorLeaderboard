package com.zacharyfox.rmonitor.leaderboard;

import java.awt.Color;
import java.awt.Component;
import java.time.Duration;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.zacharyfox.rmonitor.entities.Competitor;
import com.zacharyfox.rmonitor.utils.DurationUtil;

@SuppressWarnings("serial")
public class LeaderBoardTableCellRenderer extends DefaultTableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		// TODO: This breaks if you reorder columns!!! Need to figure out how to do this
		// with rowSorter and getModel
		Competitor currentComp = Competitor.getInstance((String) table.getValueAt(row, 2));

		Duration competitorBestLap;
		if (currentComp != null) {
			competitorBestLap = currentComp.getBestLap();
		} else {
			competitorBestLap = Duration.ZERO;
		}

		Duration fastestLap = Competitor.getFastestLap();
		if ((column == 7 || column == 8) && value.equals(fastestLap)) {
			c.setBackground(new Color(150, 0, 150));
			c.setForeground(Color.WHITE);
		} else if (column == 7 && value.equals(competitorBestLap)) {
			c.setBackground(new Color(0, 150, 0));
			c.setForeground(Color.WHITE);
		} else {
			c.setBackground(table.getBackground());
			c.setForeground(table.getForeground());
		}

		setValue(DurationUtil.format((Duration) value));

		return c;
	}
}
