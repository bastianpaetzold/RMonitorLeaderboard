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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.entities.RaceManager;
import com.zacharyfox.rmonitor.leaderboard.FinishLineLogTable;
import com.zacharyfox.rmonitor.utils.DurationUtil;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class FinishLineLogFrame extends JFrame {

	private JLabel labelRaceName;
	private JLabel labelTrackName;
	private JLabel labelElapsedTime;
	private JLabel labelToGoTime;
	private FinishLineLogTable finishLineLogTable;

	private Font fontLabelBold;

	public FinishLineLogFrame(int rowHeight) {
		Font fontLabelSystem = UIManager.getFont("Label.font");
		fontLabelBold = fontLabelSystem.deriveFont(Font.BOLD, fontLabelSystem.getSize() + 3F);

		initContent(rowHeight);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 870, 430);

		PropertyChangeListener listener = e -> SwingUtilities.invokeLater(() -> updateDisplay(e));
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				RaceManager.getInstance().removePropertyChangeListener(listener);
			}
		});
		RaceManager.getInstance().addPropertyChangeListener(listener);
	}

	private void initContent(int rowHeight) {
		getContentPane().setLayout(new MigLayout("", "[grow][grow]", "[][10:10:10][][][grow]"));
		getContentPane().add(createPanelTitleBar(), "cell 0 0 2 1,grow");

		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		getContentPane().add(separator, "cell 0 1 2 1,growx,aligny top");

		getContentPane().add(createPanelTimeBar(), "cell 0 3 2 1,growx");
		getContentPane().add(createPanelResultsTable(rowHeight), "cell 0 4 2 1,grow");
	}

	private JPanel createPanelTitleBar() {
		JPanel panelTitleBar = new JPanel();
		panelTitleBar.setLayout(new GridLayout(1, 0));

		String raceName = RaceManager.getInstance().getCurrentRace().getName();
		labelRaceName = new JLabel("".equals(raceName) ? "-" : raceName);
		labelRaceName.setFont(fontLabelBold);
		panelTitleBar.add(labelRaceName);

		String trackName = RaceManager.getInstance().getCurrentRace().getTrackName();
		labelTrackName = new JLabel("".equals(trackName) ? "-" : trackName);
		labelTrackName.setFont(fontLabelBold);
		labelTrackName.setHorizontalAlignment(SwingConstants.RIGHT);
		panelTitleBar.add(labelTrackName);

		return panelTitleBar;
	}

	private JPanel createPanelTimeBar() {
		JPanel panelTimeBar = new JPanel();
		panelTimeBar.setLayout(new MigLayout("", "[][grow]50[][grow]", "[]"));

		JLabel labelElapsed = new JLabel("Elapsed:");
		labelElapsed.setHorizontalTextPosition(SwingConstants.RIGHT);
		labelElapsed.setHorizontalAlignment(SwingConstants.RIGHT);
		panelTimeBar.add(labelElapsed, "cell 1 0");

		JLabel labelToGo = new JLabel("To Go:");
		labelToGo.setHorizontalAlignment(SwingConstants.RIGHT);
		panelTimeBar.add(labelToGo, "cell 3 0");

		labelElapsedTime = new JLabel(DurationUtil.format(Duration.ZERO));
		labelElapsedTime.setHorizontalTextPosition(SwingConstants.LEFT);
		labelElapsedTime.setHorizontalAlignment(SwingConstants.LEFT);
		labelElapsedTime.setFont(fontLabelBold);
		panelTimeBar.add(labelElapsedTime, "cell 2 0");

		labelToGoTime = new JLabel(DurationUtil.format(Duration.ZERO));
		labelToGoTime.setHorizontalTextPosition(SwingConstants.LEFT);
		labelToGoTime.setHorizontalAlignment(SwingConstants.LEFT);
		labelToGoTime.setFont(fontLabelBold);
		panelTimeBar.add(labelToGoTime, "cell 4 0");

		return panelTimeBar;
	}

	private JPanel createPanelResultsTable(int rowHeight) {
		Font fontTable = new Font("Lucida Console", Font.BOLD, rowHeight);

		JPanel panelResultsTable = new JPanel();
		panelResultsTable.setLayout(new BorderLayout());

		finishLineLogTable = new FinishLineLogTable(rowHeight);
		finishLineLogTable.getTableHeader().setFont(fontTable);
		finishLineLogTable.getTableHeader().setBackground(Color.BLACK);
		finishLineLogTable.getTableHeader().setForeground(Color.YELLOW);
		finishLineLogTable.getTableHeader().setOpaque(false);
		panelResultsTable.add(finishLineLogTable.getTableHeader(), BorderLayout.NORTH);

		finishLineLogTable.setFont(fontTable);
		finishLineLogTable.setBackground(Color.BLACK);
		finishLineLogTable.setForeground(Color.YELLOW);
		finishLineLogTable.setFillsViewportHeight(true);
		finishLineLogTable.setShowVerticalLines(false);
		finishLineLogTable.setShowHorizontalLines(false);
		finishLineLogTable.setShowGrid(false);
		finishLineLogTable.setRowMargin(2);
		finishLineLogTable.setIntercellSpacing(new Dimension(10, 1));
		finishLineLogTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		finishLineLogTable.setRowSelectionAllowed(false);
		panelResultsTable.add(finishLineLogTable, BorderLayout.CENTER);

		return panelResultsTable;
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
			labelRaceName.setText((String) evt.getNewValue());
			break;

		case Race.PROPERTY_ELAPSED_TIME:
			labelElapsedTime.setText(DurationUtil.format((Duration) evt.getNewValue()));
			break;

		case Race.PROPERTY_TIME_TO_GO:
			labelToGoTime.setText(DurationUtil.format((Duration) evt.getNewValue()));
			break;

		case Race.PROPERTY_LAPS_TO_GO:
			labelToGoTime.setText(String.valueOf(((int) evt.getNewValue())));
			break;

		case Race.PROPERTY_COMPETITORS_VERSION:
			finishLineLogTable.updateData();
			break;

		case Race.PROPERTY_TRACK_NAME:
			labelTrackName.setText(evt.getNewValue().toString());
			break;

		case Race.PROPERTY_TRACK_LENGTH:
			// TODO handle track length
			break;

		default:
			break;
		}
	}
}