package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.time.Duration;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.zacharyfox.rmonitor.client.RMonitorClient;
import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.leaderboard.FinishLineLogTable;
import com.zacharyfox.rmonitor.leaderboard.FinishlineLogTableModel;
import com.zacharyfox.rmonitor.utils.DurationUtil;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class FinishLineLogFrame extends JFrame {

	private final JLabel elapsedTime;
	private final JLabel lblNewLabel1;
	private final JLabel lblNewLabel2;
	private final FinishLineLogTable finishLineLogTable;
	private final JPanel resultsTablePanel;
	private final JLabel runName;
	private final JSeparator separator;
	private final JPanel timeBar;
	private final JLabel timeToGo;
	private final JPanel titleBar;
	private final JLabel trackName;

	public FinishLineLogFrame(int rowHeight) {
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		Font systemLabelFont = UIManager.getFont("Label.font");
		this.setBounds(100, 100, 870, 430);
		this.getContentPane().setLayout(new MigLayout("", "[grow][grow]", "[][10:10:10][][][grow]"));

		titleBar = new JPanel();
		this.getContentPane().add(titleBar, "cell 0 0 2 1,grow");
		titleBar.setLayout(new GridLayout(1, 0, 0, 0));

		runName = new JLabel("-");
		runName.setFont(new Font(systemLabelFont.getName(), Font.BOLD, systemLabelFont.getSize() + 3));
		titleBar.add(runName);

		trackName = new JLabel("-");
		trackName.setFont(new Font(systemLabelFont.getName(), Font.BOLD, systemLabelFont.getSize() + 3));
		trackName.setHorizontalAlignment(SwingConstants.RIGHT);
		titleBar.add(trackName);

		separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBorder(null);
		getContentPane().add(separator, "cell 0 1 2 1,growx,aligny top");

		timeBar = new JPanel();
		getContentPane().add(timeBar, "cell 0 3 2 1,growx");
		timeBar.setLayout(new MigLayout("", "[][grow]50[][grow]", "[]"));

		lblNewLabel1 = new JLabel("Elapsed:");
		lblNewLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblNewLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
		timeBar.add(lblNewLabel1, "cell 1 0");

		lblNewLabel2 = new JLabel("To Go:");
		lblNewLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
		timeBar.add(lblNewLabel2, "cell 3 0");

		elapsedTime = new JLabel(DurationUtil.format(Duration.ZERO));
		elapsedTime.setHorizontalTextPosition(SwingConstants.LEFT);
		elapsedTime.setHorizontalAlignment(SwingConstants.LEFT);
		elapsedTime.setFont(new Font(systemLabelFont.getName(), Font.BOLD, systemLabelFont.getSize() + 3));
		timeBar.add(elapsedTime, "cell 2 0");

		timeToGo = new JLabel(DurationUtil.format(Duration.ZERO));
		timeToGo.setHorizontalTextPosition(SwingConstants.LEFT);
		timeToGo.setHorizontalAlignment(SwingConstants.LEFT);
		timeToGo.setFont(new Font(systemLabelFont.getName(), Font.BOLD, systemLabelFont.getSize() + 3));
		timeBar.add(timeToGo, "cell 4 0");

		resultsTablePanel = new JPanel();
		getContentPane().add(resultsTablePanel, "cell 0 4 2 1,grow");
		resultsTablePanel.setLayout(new BorderLayout());
		finishLineLogTable = new FinishLineLogTable(rowHeight);
		finishLineLogTable.setIntercellSpacing(new Dimension(10, 1));
		finishLineLogTable.setFillsViewportHeight(true);
		finishLineLogTable.setShowVerticalLines(false);
		finishLineLogTable.setShowHorizontalLines(false);
		finishLineLogTable.setShowGrid(false);
		finishLineLogTable.setRowMargin(2);
		finishLineLogTable.setFont(new Font("Lucida Console", Font.BOLD, rowHeight));
		finishLineLogTable.setBackground(Color.BLACK);
		finishLineLogTable.setForeground(Color.YELLOW);
		finishLineLogTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		finishLineLogTable.setRowSelectionAllowed(false);
		finishLineLogTable.getTableHeader().setFont(new Font("Lucida Console", Font.BOLD, rowHeight));
		finishLineLogTable.getTableHeader().setOpaque(false);
		finishLineLogTable.getTableHeader().setBackground(Color.BLACK);
		finishLineLogTable.getTableHeader().setForeground(Color.YELLOW);
		resultsTablePanel.add(finishLineLogTable.getTableHeader(), BorderLayout.NORTH);
		resultsTablePanel.add(finishLineLogTable, BorderLayout.CENTER);

		RMonitorClient.getInstance().getRace().addPropertyChangeListener(this::updateDisplay);

	}

	public void goFullScreen() {
		final Cursor oldCursor = getContentPane().getCursor();
		final Rectangle oldBounds = getBounds();
		final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		if (gd.isFullScreenSupported()) {
			try {
				setCursor(getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
						new Point(0, 0), "null"));
				dispose();
				setUndecorated(true);
				pack();
				setVisible(true);
				gd.setFullScreenWindow(this);

				InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				final ActionMap actionMap = getRootPane().getActionMap();

				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "escAction");
				actionMap.put("escAction", new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent evt) {
						actionMap.remove(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true));
						FinishLineLogFrame.this.setCursor(oldCursor);
						gd.setFullScreenWindow(null);
						FinishLineLogFrame.this.dispose();
						FinishLineLogFrame.this.setUndecorated(false);
						FinishLineLogFrame.this.pack();
						FinishLineLogFrame.this.setBounds(oldBounds);
						FinishLineLogFrame.this.setVisible(true);
					}
				});
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else {
			setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
		}
	}

	private void updateDisplay(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case Race.PROPERTY_RACE_NAME:
			runName.setText((String) evt.getNewValue());
			break;

		case Race.PROPERTY_ELAPSED_TIME:
			elapsedTime.setText(DurationUtil.format((Duration) evt.getNewValue()));
			break;

		case Race.PROPERTY_TIME_TO_GO:
			timeToGo.setText(DurationUtil.format((Duration) evt.getNewValue()));
			break;

		case Race.PROPERTY_LAPS_TO_GO:
			timeToGo.setText(String.valueOf(((int) evt.getNewValue())));
			break;

		case Race.PROPERTY_COMPETITORS_VERSION:
			((FinishlineLogTableModel) finishLineLogTable.getModel()).updateData();
			break;

		case Race.PROPERTY_TRACK_NAME:
			trackName.setText(evt.getNewValue().toString());
			break;

		case Race.PROPERTY_TRACK_LENGTH:
			// TODO handle track length
			break;

		default:
			break;
		}
	}
}