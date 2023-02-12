package com.zacharyfox.rmonitor.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zacharyfox.rmonitor.config.ConfigurationManager;

public class Recorder {

	private static final Logger LOGGER = LoggerFactory.getLogger(Recorder.class);

	public static final String DEFAULT_FILE_PATH = "";
	public static final String DEFAULT_FILE_ENCODING = Charset.defaultCharset().name();

	private static final String PROP_FILE_PATH = "recorder.file";
	private static final String PROP_FILE_ENCODING = "recorder.file.encoding";

	private Path filePath;
	private Charset encoding;

	private BufferedWriter writer;
	private long startTime = 0;

	private State currentState;
	private List<BiConsumer<State, State>> listenerList;
	private ConfigurationManager configManager;

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

		configManager = ConfigurationManager.getInstance();
		filePath = Paths.get(configManager.getConfig(PROP_FILE_PATH, DEFAULT_FILE_PATH));

		String encodingString = configManager.getConfig(PROP_FILE_ENCODING, DEFAULT_FILE_ENCODING);
		try {
			encoding = Charset.forName(encodingString);
		} catch (IllegalCharsetNameException e) {
			LOGGER.error("Illegal charset defined as client stream encoding: {}", encodingString);
			encoding = Charset.defaultCharset();
		} catch (UnsupportedCharsetException e) {
			LOGGER.error("Unsupported charset defined as client stream encoding: {}", encodingString);
			encoding = Charset.defaultCharset();
		}
	}

	public synchronized void start() {
		if (currentState == State.STOPPED) {
			try {
				writer = Files.newBufferedWriter(filePath, encoding);
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

	public Path getFilePath() {
		return filePath;
	}

	public void setFilePath(Path filePath) {
		this.filePath = filePath;
		configManager.setConfig(PROP_FILE_PATH, filePath.toString());
	}

	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
		configManager.setConfig(PROP_FILE_ENCODING, encoding.name());
	}
}
