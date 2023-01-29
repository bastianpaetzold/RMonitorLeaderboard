package com.zacharyfox.rmonitor.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.zacharyfox.rmonitor.config.ConfigurationManager;

public class Player {

	private static final int DEFAULT_PORT = 50000;
	private static final String PROP_LAST_FILE = "player.lastFile";
	private static final String PROP_SPEEDUP = "player.speedup";

	private int port = DEFAULT_PORT;
	private Path filePath;
	private int speedup = 2;

	private Thread thread;
	private State currentState;
	private boolean pauseRequested;
	private List<BiConsumer<State, State>> listenerList;
	private ConfigurationManager configManager;

	private Socket clientSocket;
	private ServerSocket serverSocket;

	public enum State {
		STARTED, WAITING_FOR_CONNECTION, RUNNING, PAUSED, STOPPED
	}

	private static Player instance;

	public static Player getInstance() {
		if (instance == null) {
			instance = new Player();
		}

		return instance;
	}

	private Player() {
		currentState = State.STOPPED;
		listenerList = new ArrayList<>();

		configManager = ConfigurationManager.getInstance();
		filePath = Paths.get(configManager.getConfig(PROP_LAST_FILE, ""));
		speedup = Integer.parseInt(configManager.getConfig(PROP_SPEEDUP, "2"));
	}

	public synchronized void start() {
		if (isStartable()) {
			thread = new Thread(this::run);
			thread.start();
		}
	}

	private void run() {
		try (BufferedReader reader = Files.newBufferedReader(filePath)) {
			updateCurrentState(State.STARTED);

			if (awaitConnection()) {
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

				if (pauseRequested) {
					pauseRequested = false;
					updateCurrentState(State.PAUSED);
				}

				long lastTs = 0;
				String line = reader.readLine();
				while (line != null) {
					if (currentState == State.PAUSED) {
						Thread.sleep(1000);
					} else {
						String[] tokens = line.split(" ", 2);
						long tS = Integer.parseInt(tokens[0]);
						out.println(tokens[1]);
						Thread.sleep((int) ((tS - lastTs) / speedup));
						lastTs = tS;
						line = reader.readLine();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			updateCurrentState(State.STOPPED);
			System.out.println("Player stopped");
		}
	}

	private boolean awaitConnection() {
		try {
			serverSocket = new ServerSocket(port);
			updateCurrentState(State.WAITING_FOR_CONNECTION);
		} catch (IOException e) {
			System.err.println("Failed to start server for player: " + e.getMessage());
		}

		if (currentState == State.WAITING_FOR_CONNECTION) {
			try {
				System.out.println("Player waiting for client");
				clientSocket = serverSocket.accept();
				updateCurrentState(State.RUNNING);
				System.out.println("Player client connected");
			} catch (IOException e) {
				System.out.println("Player waiting for client cancelled");
			}
		}

		return currentState == State.RUNNING;
	}

	public void stop() {
		try {
			if (clientSocket != null) {
				clientSocket.close();
			}
			if (serverSocket != null) {
				serverSocket.close();
			}
			if (thread != null) {
				thread.interrupt();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pause() {
		if (currentState == State.RUNNING) {
			updateCurrentState(State.PAUSED);
		} else {
			pauseRequested = true;
		}
	}

	public void resume() {
		if (currentState == State.PAUSED) {
			updateCurrentState(State.RUNNING);
			pauseRequested = false;
		}
	}

	public boolean isStartable() {
		boolean startable = false;

		if (currentState == State.STOPPED) {
			if (filePath != null && Files.isReadable(filePath)) {
				startable = true;
			} else {
				System.err.println("Player file is not valid or readable: " + filePath.toString());
			}
		}
		
		return startable;
	}

	private void updateCurrentState(State newState) {
		State oldState = currentState;
		currentState = newState;
		notifyStateChangeListener(oldState, newState);
	}

	private void notifyStateChangeListener(State oldState, State newState) {
		listenerList.forEach(c -> c.accept(oldState, newState));
	}

	public void addStateChangeListener(BiConsumer<State, State> listener) {
		listenerList.add(listener);
	}

	public void removeStateChangeListener(BiConsumer<State, State> listener) {
		listenerList.remove(listener);
	}

	public int getSpeedup() {
		return speedup;
	}

	public void setSpeedup(int speedup) {
		this.speedup = speedup;
		configManager.setConfig(PROP_SPEEDUP, speedup);
	}

	public Path getFilePath() {
		return filePath;
	}

	public void setFilePath(Path filePath) {
		this.filePath = filePath;
		configManager.setConfig(PROP_LAST_FILE, filePath.toString());
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public State getCurrentState() {
		return currentState;
	}
}
