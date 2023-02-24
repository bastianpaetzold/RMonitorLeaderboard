package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.HeadlessException;
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
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.entities.Race.FlagStatus;
import com.zacharyfox.rmonitor.entities.RaceManager;
import com.zacharyfox.rmonitor.leaderboard.LeaderBoardMenuBar;
import com.zacharyfox.rmonitor.leaderboard.LeaderBoardTable;
import com.zacharyfox.rmonitor.utils.DurationUtil;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);

	private static final Color COLOR_GREEN = new Color(0, 150, 0);
	private static final Color COLOR_PURPLE = new Color(98, 0, 255);

	private JLabel labelRunName;
	private JLabel labelTrackName;
	private JPanel panelFlagColor1;
	private JPanel panelFlagColor2;
	private JPanel panelFlagColor3;
	private JPanel panelFlagColor4;
	private JLabel labelElapsedTime;
	private LeaderBoardTable leaderBoardTable;
	private LeaderBoardMenuBar leaderBoardMenuBar;
	private JLabel labelToGoValue;

	private Font fontLabelBold;

	public MainFrame() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 870, 430);

		Font fontLabelSystem = UIManager.getFont("Label.font");
		fontLabelBold = fontLabelSystem.deriveFont(Font.BOLD, fontLabelSystem.getSize() + 3F);

		initContent();

		RaceManager.getInstance().addPropertyChangeListener(e -> SwingUtilities.invokeLater(() -> updateDisplay(e)));
	}

	private void initContent() {
		getContentPane().setLayout(new MigLayout("", "[grow][grow]", "[][10:10:10][][][grow]"));

		getContentPane().add(createPanelTitle(), "cell 0 0 2 1,grow");

		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		getContentPane().add(separator, "cell 0 1 2 1,growx,aligny top");

		getContentPane().add(createPanelTime(), "cell 0 3 2 1,growx");

		JPanel resultsTablePanel = new JPanel();
		resultsTablePanel.setLayout(new GridLayout(1, 0));
		resultsTablePanel.add(new JScrollPane(createLeaderBoardTable()));
		getContentPane().add(resultsTablePanel, "cell 0 4 2 1,grow");

		leaderBoardMenuBar = new LeaderBoardMenuBar(this);
		setJMenuBar(leaderBoardMenuBar);
	}

	private JPanel createPanelTitle() {
		JPanel titleBar = new JPanel();
		titleBar.setLayout(new GridLayout(1, 0));

		labelRunName = new JLabel("-");
		labelRunName.setFont(fontLabelBold);
		titleBar.add(labelRunName);

		labelTrackName = new JLabel("-");
		labelTrackName.setFont(fontLabelBold);
		labelTrackName.setHorizontalAlignment(SwingConstants.RIGHT);
		titleBar.add(labelTrackName);

		return titleBar;
	}

	private JPanel createPanelTime() {
		JPanel timeBar = new JPanel();
		timeBar.setLayout(new MigLayout("", "[50:50:50][grow][grow][grow]", "[][]"));
		timeBar.add(createPanelFlag(), "flowx,cell 0 0 1 2,grow");

		JLabel labelElapsed = new JLabel("Elapsed:");
		labelElapsed.setHorizontalAlignment(SwingConstants.RIGHT);
		timeBar.add(labelElapsed, "cell 2 0");

		JLabel labelToGo = new JLabel("To Go:");
		labelToGo.setHorizontalAlignment(SwingConstants.RIGHT);
		timeBar.add(labelToGo, "cell 3 0");

		labelElapsedTime = new JLabel(DurationUtil.format(Duration.ZERO));
		labelElapsedTime.setHorizontalAlignment(SwingConstants.RIGHT);
		labelElapsedTime.setFont(fontLabelBold);
		timeBar.add(labelElapsedTime, "cell 2 1");

		labelToGoValue = new JLabel(DurationUtil.format(Duration.ZERO));
		labelToGoValue.setHorizontalAlignment(SwingConstants.RIGHT);
		labelToGoValue.setFont(fontLabelBold);
		timeBar.add(labelToGoValue, "cell 3 1");

		return timeBar;
	}

	private JPanel createPanelFlag() {
		JPanel panelFlag = new JPanel();
		panelFlag.setLayout(new GridLayout(0, 2));

		panelFlagColor1 = new JPanel();
		panelFlag.add(panelFlagColor1);

		panelFlagColor2 = new JPanel();
		panelFlag.add(panelFlagColor2);

		panelFlagColor3 = new JPanel();
		panelFlag.add(panelFlagColor3);

		panelFlagColor4 = new JPanel();
		panelFlag.add(panelFlagColor4);
		return panelFlag;
	}

	private LeaderBoardTable createLeaderBoardTable() {
		leaderBoardTable = new LeaderBoardTable();
		leaderBoardTable.setIntercellSpacing(new Dimension(10, 1));
		leaderBoardTable.setFillsViewportHeight(true);
		leaderBoardTable.setShowVerticalLines(false);
		leaderBoardTable.setShowHorizontalLines(false);
		leaderBoardTable.setShowGrid(false);
		leaderBoardTable.setRowMargin(2);
		leaderBoardTable.setRowHeight(18);
		leaderBoardTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		leaderBoardTable.setRowSelectionAllowed(false);

		return leaderBoardTable;
	}

	public void enterFullScreen() {
		Cursor oldCursor = getContentPane().getCursor();
		Rectangle oldBounds = getBounds();
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		if (gd.isFullScreenSupported()) {
			try {
				Cursor customCursor = getToolkit().createCustomCursor(
						new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null");
				setCursor(customCursor);
			} catch (IndexOutOfBoundsException | HeadlessException e) {
				LOGGER.warn("Failed to create custom cursor", e);
			}

			dispose();
			leaderBoardMenuBar.setVisible(false);
			setUndecorated(true);
			pack();
			setVisible(true);
			gd.setFullScreenWindow(this);

			InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			ActionMap actionMap = getRootPane().getActionMap();

			KeyStroke escKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
			inputMap.put(escKeyStroke, "escAction");
			actionMap.put("escAction", new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					actionMap.remove(escKeyStroke);

					MainFrame.this.setCursor(oldCursor);
					gd.setFullScreenWindow(null);

					MainFrame.this.dispose();
					leaderBoardMenuBar.setVisible(true);
					MainFrame.this.setUndecorated(false);
					MainFrame.this.pack();
					MainFrame.this.setBounds(oldBounds);
					MainFrame.this.setVisible(true);
				}
			});
		} else {
			setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
		}
	}

	private void updateDisplay(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case Race.PROPERTY_RACE_NAME:
			labelRunName.setText((String) evt.getNewValue());
			break;

		case Race.PROPERTY_ELAPSED_TIME:
			labelElapsedTime.setText(DurationUtil.format((Duration) evt.getNewValue()));
			break;

		case Race.PROPERTY_TIME_TO_GO:
			labelToGoValue.setText(DurationUtil.format((Duration) evt.getNewValue()));
			break;

		case Race.PROPERTY_LAPS_TO_GO:
			labelToGoValue.setText(String.valueOf(((int) evt.getNewValue())));
			break;

		case Race.PROPERTY_COMPETITORS_VERSION:
			leaderBoardTable.updateData();
			break;

		case Race.PROPERTY_FLAG_STATUS:
			updateFlagColor((FlagStatus) evt.getNewValue());
			break;

		case Race.PROPERTY_TRACK_NAME:
			labelTrackName.setText(evt.getNewValue().toString());
			break;

		default:
			break;
		}
	}

	private void updateFlagColor(FlagStatus status) {
		switch (status) {
		case NONE:
			panelFlagColor1.setBackground(null);
			panelFlagColor2.setBackground(null);
			panelFlagColor3.setBackground(null);
			panelFlagColor4.setBackground(null);
			break;

		case GREEN:
			panelFlagColor1.setBackground(COLOR_GREEN);
			panelFlagColor2.setBackground(COLOR_GREEN);
			panelFlagColor3.setBackground(COLOR_GREEN);
			panelFlagColor4.setBackground(COLOR_GREEN);
			break;

		case YELLOW:
			panelFlagColor1.setBackground(Color.YELLOW);
			panelFlagColor2.setBackground(Color.YELLOW);
			panelFlagColor3.setBackground(Color.YELLOW);
			panelFlagColor4.setBackground(Color.YELLOW);
			break;

		case RED:
			panelFlagColor1.setBackground(Color.RED);
			panelFlagColor2.setBackground(Color.RED);
			panelFlagColor3.setBackground(Color.RED);
			panelFlagColor4.setBackground(Color.RED);
			break;

		case FINISH:
			panelFlagColor1.setBackground(Color.BLACK);
			panelFlagColor2.setBackground(Color.WHITE);
			panelFlagColor3.setBackground(Color.WHITE);
			panelFlagColor4.setBackground(Color.BLACK);
			break;

		case PURPLE:
			panelFlagColor1.setBackground(COLOR_PURPLE);
			panelFlagColor2.setBackground(COLOR_PURPLE);
			panelFlagColor3.setBackground(COLOR_PURPLE);
			panelFlagColor4.setBackground(COLOR_PURPLE);
			break;

		default:
			break;
		}
	}
}