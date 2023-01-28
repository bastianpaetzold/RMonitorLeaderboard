package com.zacharyfox.rmonitor.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.message.Factory;
import com.zacharyfox.rmonitor.utils.Estimator;
import com.zacharyfox.rmonitor.utils.Recorder;

public class RMonitorClient {

	public static final String DEFAULT_HOST = "127.0.0.1";
	public static final int DEFAULT_PORT = 50000;

	private static RMonitorClient instance;

	private String host;
	private int port;

	private Thread thread;
	private State currentState;
	private List<BiConsumer<State, State>> listenerList;

	private Race race;
	private Recorder recorder;
	private Estimator estimator;
	
	public enum State {
		STARTED, RUNNING, STOPPED
	}

	private RMonitorClient(String host, int port) {
		this.host = host;
		this.port = port;

		listenerList = new ArrayList<>();
		currentState = State.STOPPED;
	}

	public static RMonitorClient getInstance() {
		if (instance == null) {
			instance = new RMonitorClient(DEFAULT_HOST, DEFAULT_PORT);
		}

		return instance;
	}

	public synchronized void start() {
		if (currentState == State.STOPPED) {
			thread = new Thread(this::run);
			thread.setDaemon(true);
			thread.start();
		}
	}

	private void run() {
		updateCurrentState(State.STARTED);
		race = new Race();

		try (Socket socket = new Socket(host, port)) {
			updateCurrentState(State.RUNNING);
			doWork(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			updateCurrentState(State.STOPPED);
		}
	}

	private void doWork(InputStream inputStream) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			String line;

			while (!Thread.currentThread().isInterrupted() && (line = reader.readLine()) != null) {
				processMessage(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void processMessage(String message) {
		if (race != null) {
			race.update(Factory.getMessage(message));

			if (estimator != null) {
				estimator.update(race);
			}
		}

		if (recorder != null) {
			recorder.push(message);
		}
	}

	public void stop() {
		thread.interrupt();
	}

	public void stopAndWait() throws InterruptedException {
		stop();
		thread.join();
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

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public Race getRace() {
		return race;
	}

	public synchronized void setRecorder(Recorder recorder) {
		this.recorder = recorder;
	}

	public synchronized void setEstimator(Estimator estimator) {
		this.estimator = estimator;
	}
	
	public State getCurrentState() {
		return currentState;
	}
}
