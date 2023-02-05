package com.zacharyfox.rmonitor.leaderboard.frames;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.zacharyfox.rmonitor.client.RMonitorClient;
import com.zacharyfox.rmonitor.leaderboard.LeaderBoardMenuBar;
import com.zacharyfox.rmonitor.leaderboard.LeaderBoardTable;
import com.zacharyfox.rmonitor.leaderboard.LeaderBoardTableModel;
import com.zacharyfox.rmonitor.utils.DurationUtil;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	private static final Color COLOR_GREEN = new Color(0, 150, 0);

	private final JLabel elapsedTime;
	private final JPanel flagColor;
	private final JPanel flagColor1;
	private final JPanel flagColor2;
	private final JPanel flagColor3;
	private final JPanel flagColor4;
	private final JLabel lblNewLabel1;
	private final JLabel lblNewLabel2;
	private final LeaderBoardTable leaderBoardTable;
	private final LeaderBoardMenuBar leaderBoardMenuBar;
	private final JScrollPane resultsScrollPane;
	private final JPanel resultsTablePanel;
	private final JLabel runName;
	private final JSeparator separator;
	private final JPanel timeBar;
	private final JLabel timeToGo;
	private final JPanel titleBar;
	private final JLabel trackName;

	public MainFrame() {
		Font systemLabelFont = UIManager.getFont("Label.font");
		this.setBounds(100, 100, 870, 430);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
		timeBar.setLayout(new MigLayout("", "[50:50:50][grow][grow][grow]", "[][]"));

		flagColor = new JPanel();
		timeBar.add(flagColor, "flowx,cell 0 0 1 2,grow");
		flagColor.setBackground(null);
		flagColor.setBorder(null);
		flagColor.setLayout(new GridLayout(0, 2, 0, 0));

		flagColor1 = new JPanel();
		flagColor.add(flagColor1);
		flagColor1.setBorder(null);
		flagColor1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		flagColor2 = new JPanel();
		flagColor.add(flagColor2);
		flagColor2.setBorder(null);
		flagColor2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		flagColor3 = new JPanel();
		flagColor.add(flagColor3);
		flagColor3.setBorder(null);
		flagColor3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		flagColor4 = new JPanel();
		flagColor.add(flagColor4);
		flagColor4.setBorder(null);
		flagColor4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		lblNewLabel1 = new JLabel("Elapsed:");
		lblNewLabel1.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblNewLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
		timeBar.add(lblNewLabel1, "cell 2 0");

		lblNewLabel2 = new JLabel("To Go:");
		lblNewLabel2.setHorizontalAlignment(SwingConstants.RIGHT);
		timeBar.add(lblNewLabel2, "cell 3 0");

		elapsedTime = new JLabel(DurationUtil.format(Duration.ZERO));
		elapsedTime.setHorizontalAlignment(SwingConstants.RIGHT);
		elapsedTime.setFont(new Font(systemLabelFont.getName(), Font.BOLD, systemLabelFont.getSize() + 3));
		timeBar.add(elapsedTime, "cell 2 1");

		timeToGo = new JLabel(DurationUtil.format(Duration.ZERO));
		timeToGo.setHorizontalAlignment(SwingConstants.RIGHT);
		timeToGo.setFont(new Font(systemLabelFont.getName(), Font.BOLD, systemLabelFont.getSize() + 3));
		timeBar.add(timeToGo, "cell 3 1");

		resultsTablePanel = new JPanel();
		getContentPane().add(resultsTablePanel, "cell 0 4 2 1,grow");
		resultsTablePanel.setLayout(new GridLayout(1, 0, 0, 0));

		resultsScrollPane = new JScrollPane();
		resultsTablePanel.add(resultsScrollPane);

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
		resultsScrollPane.setViewportView(leaderBoardTable);

		leaderBoardMenuBar = new LeaderBoardMenuBar(this);
		leaderBoardMenuBar.disableStartSignalMenu();
		leaderBoardMenuBar.disableLapCounterMenu();
		setJMenuBar(leaderBoardMenuBar);

		RMonitorClient client = RMonitorClient.getInstance();
		client.addStateChangeListener((oldState, newState) -> {
			switch (newState) {
			case STARTED:
				break;

			case CONNECTED:
				client.getRace().addPropertyChangeListener(this::updateDisplay);

				leaderBoardMenuBar.enableStartSignalMenu();
				leaderBoardMenuBar.enableLapCounterMenu();
				leaderBoardMenuBar.enableFinishLineLogMenu();
				break;

			case STOPPED:
				leaderBoardMenuBar.disableStartSignalMenu();
				leaderBoardMenuBar.disableLapCounterMenu();
				leaderBoardMenuBar.disableFinishLineLogMenu();
				break;

			default:
				break;
			}
		});
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
				leaderBoardMenuBar.setVisible(false);
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
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else {
			setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
		}
	}

	private void setFlagColor(String status) {
		switch (status) {
		case "Green":
			flagColor1.setBackground(COLOR_GREEN);
			flagColor2.setBackground(COLOR_GREEN);
			flagColor3.setBackground(COLOR_GREEN);
			flagColor4.setBackground(COLOR_GREEN);
			break;

		case "Yellow":
			flagColor1.setBackground(Color.YELLOW);
			flagColor2.setBackground(Color.YELLOW);
			flagColor3.setBackground(Color.YELLOW);
			flagColor4.setBackground(Color.YELLOW);
			break;

		case "Red":
			flagColor1.setBackground(Color.RED);
			flagColor2.setBackground(Color.RED);
			flagColor3.setBackground(Color.RED);
			flagColor4.setBackground(Color.RED);
			break;

		case "Finish":
			flagColor1.setBackground(Color.BLACK);
			flagColor2.setBackground(Color.WHITE);
			flagColor3.setBackground(Color.WHITE);
			flagColor4.setBackground(Color.BLACK);
			break;

		default:
			break;
		}
	}

	private void updateDisplay(PropertyChangeEvent evt) {
		switch (evt.getPropertyName()) {
		case "raceName":
			runName.setText((String) evt.getNewValue());
			break;

		case "elapsedTime":
			elapsedTime.setText(DurationUtil.format((Duration) evt.getNewValue()));
			break;

		case "timeToGo":
			timeToGo.setText(DurationUtil.format((Duration) evt.getNewValue()));
			break;

		case "lapsToGo":
			timeToGo.setText(String.valueOf(((int) evt.getNewValue())));
			break;

		case "competitorsVersion":
			((LeaderBoardTableModel) leaderBoardTable.getModel()).updateData();
			break;

		case "flagStatus":
			setFlagColor(evt.getNewValue().toString());
			break;

		case "trackName":
			trackName.setText(evt.getNewValue().toString());
			break;

		default:
			break;
		}
	}
}