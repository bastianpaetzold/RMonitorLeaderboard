package com.zacharyfox.rmonitor.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import com.zacharyfox.rmonitor.config.ConfigurationManager;
import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.message.Factory;
import com.zacharyfox.rmonitor.utils.Estimator;
import com.zacharyfox.rmonitor.utils.Recorder;

public class RMonitorClient {

	public static final String DEFAULT_HOST = "127.0.0.1";
	public static final int DEFAULT_PORT = 50000;
	public static final String DEFAULT_ENCODING = Charset.defaultCharset().name();
	
	private static final String PROP_HOST = "client.host";
	private static final String PROP_PORT = "client.port";
	private static final String PROP_ENCODING = "client.encoding";

	private static RMonitorClient instance;

	private String host;
	private int port;
	private Charset encoding;

	private Thread thread;
	private State currentState;
	private List<BiConsumer<State, State>> listenerList;
	private ConfigurationManager configManager;

	private Race currentRace;
	private Recorder recorder;
	private Estimator estimator;

	public enum State {
		STARTED, RUNNING, STOPPED
	}

	private RMonitorClient() {
		listenerList = new ArrayList<>();
		currentState = State.STOPPED;
		currentRace = new Race();
		
		recorder = Recorder.getInstance();

		configManager = ConfigurationManager.getInstance();
		host = configManager.getConfig(PROP_HOST, DEFAULT_HOST);
		port = configManager.getConfig(PROP_PORT, DEFAULT_PORT);

		String encodingString = configManager.getConfig(PROP_ENCODING, DEFAULT_ENCODING);
		try {
			encoding = Charset.forName(encodingString);
		} catch (IllegalCharsetNameException e) {
			System.err.println("Illegal charset defined as client stream encoding: " + encodingString);
			encoding = Charset.defaultCharset();
		} catch (UnsupportedCharsetException e) {
			System.err.println("Unsupported charset defined as client stream encoding: " + encodingString);
			encoding = Charset.defaultCharset();
		}
	}

	public static RMonitorClient getInstance() {
		if (instance == null) {
			instance = new RMonitorClient();
		}

		return instance;
	}

	public synchronized void start() {
		if (currentState == State.STOPPED) {
			thread = new Thread(this::run);
			thread.start();
		}
	}

	private void run() {
		updateCurrentState(State.STARTED);
		currentRace = new Race();

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
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, encoding))) {
			String line;

			while (!Thread.currentThread().isInterrupted() && (line = reader.readLine()) != null) {
				processMessage(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private synchronized void processMessage(String message) {
		currentRace.update(Factory.getMessage(message));
		recorder.push(message);
		
		if (estimator != null) {
			estimator.update(currentRace);
		}
	}

	public void stop() {
		thread.interrupt();
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
		configManager.setConfig(PROP_HOST, host);
	}
	
	public String getHost() {
		return host;
	}

	public void setPort(int port) {
		this.port = port;
		configManager.setConfig(PROP_PORT, port);
	}
	
	public int getPort() {
		return port;
	}
	
	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
		configManager.setConfig(PROP_ENCODING, encoding.name());
	}

	public Race getRace() {
		return currentRace;
	}

	public synchronized void setEstimator(Estimator estimator) {
		this.estimator = estimator;
	}

	public State getCurrentState() {
		return currentState;
	}
}
