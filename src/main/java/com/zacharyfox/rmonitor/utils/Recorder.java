package com.zacharyfox.rmonitor.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Recorder {

	private Path path;
	private BufferedWriter writer;
	private long startTime = 0;

	private State currentState;
	private List<BiConsumer<State, State>> listenerList;

	public enum State {
		STARTED, STOPPED
	}

	private static Recorder instance;

	public static Recorder getInstance() {
		if (instance == null) {
			instance = new Recorder();
		}

		return instance;
	}

	private Recorder() {
		currentState = State.STOPPED;
		listenerList = new ArrayList<>();

		path = Paths.get("");
	}

	public synchronized void start() {
		if (currentState == State.STOPPED) {
			try {
				writer = Files.newBufferedWriter(path);
				updateCurrentState(State.STARTED);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void stop() {
		if (currentState == State.STARTED) {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			updateCurrentState(State.STOPPED);
		}
	}

	public synchronized void push(String message) {
		if (currentState == State.STARTED) {
			if (startTime == 0) {
				startTime = System.currentTimeMillis();
			}
			long timeStamp = System.currentTimeMillis() - startTime;
			try {
				writer.write(timeStamp + " " + message);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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

	public State getCurrentState() {
		return currentState;
	}

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}
}
