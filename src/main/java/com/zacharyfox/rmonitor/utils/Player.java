package com.zacharyfox.rmonitor.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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

public class Player {

	private static final Logger LOGGER = LoggerFactory.getLogger(Player.class);

	public static final int DEFAULT_PORT = 50000;
	public static final String DEFAULT_SPEEDUP = "2";
	public static final String DEFAULT_FILE_PATH = "";
	public static final String DEFAULT_FILE_ENCODING = Charset.defaultCharset().name();
	public static final String DEFAULT_STREAM_ENCODING = Charset.defaultCharset().name();

	private static final String PROP_PORT = "player.port";
	private static final String PROP_SPEEDUP = "player.speedup";
	private static final String PROP_FILE_PATH = "player.file";
	private static final String PROP_FILE_ENCODING = "player.file.encoding";
	private static final String PROP_STREAM_ENCODING = "player.stream.encoding";

	private int port;
	private int speedup = 2;
	private Path filePath;
	private Charset fileEncoding;
	private Charset streamEncoding;

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
		port = configManager.getConfig(PROP_PORT, DEFAULT_PORT);
		speedup = Integer.parseInt(configManager.getConfig(PROP_SPEEDUP, DEFAULT_SPEEDUP));
		filePath = Paths.get(configManager.getConfig(PROP_FILE_PATH, DEFAULT_FILE_PATH));

		String fileEncodingString = configManager.getConfig(PROP_FILE_ENCODING, DEFAULT_FILE_ENCODING);
		try {
			fileEncoding = Charset.forName(fileEncodingString);
		} catch (IllegalCharsetNameException e) {
			LOGGER.error("Illegal charset defined as player file encoding: {}", fileEncodingString);
			fileEncoding = Charset.defaultCharset();
		} catch (UnsupportedCharsetException e) {
			LOGGER.error("Unsupported charset defined as player file encoding: {}", fileEncodingString);
			fileEncoding = Charset.defaultCharset();
		}

		String streamEncodingString = configManager.getConfig(PROP_STREAM_ENCODING, DEFAULT_STREAM_ENCODING);
		try {
			streamEncoding = Charset.forName(streamEncodingString);
		} catch (IllegalCharsetNameException e) {
			LOGGER.error("Illegal charset defined as player stream encoding: {}", streamEncodingString);
			streamEncoding = Charset.defaultCharset();
		} catch (UnsupportedCharsetException e) {
			LOGGER.error("Unsupported charset defined as player stream encoding: {}", streamEncodingString);
			streamEncoding = Charset.defaultCharset();
		}
	}

	public synchronized void start() {
		if (isStartable()) {
			thread = new Thread(this::run);
			thread.start();
		}
	}

	private void run() {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(Files.newInputStream(filePath), fileEncoding))) {
			updateCurrentState(State.STARTED);

			if (awaitConnection()) {
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true, streamEncoding);

				if (pauseRequested) {
					pauseRequested = false;
					updateCurrentState(State.PAUSED);
				}

				playMessages(reader, writer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			updateCurrentState(State.STOPPED);
			closeConnections();
			LOGGER.info("Player stopped");
		}
	}

	private boolean awaitConnection() {
		try {
			serverSocket = new ServerSocket(port);
			updateCurrentState(State.WAITING_FOR_CONNECTION);
		} catch (IOException e) {
			LOGGER.error("Failed to start server for player", e);
		}

		if (currentState == State.WAITING_FOR_CONNECTION) {
			try {
				LOGGER.info("Player waiting for client");
				clientSocket = serverSocket.accept();
				updateCurrentState(State.RUNNING);
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Client connected to player: {}", clientSocket.getRemoteSocketAddress());
				}
			} catch (IOException e) {
				LOGGER.info("Player waiting for client cancelled");
			}
		}

		return currentState == State.RUNNING;
	}

	private void playMessages(BufferedReader reader, PrintWriter writer) throws IOException, InterruptedException {
		long lastTimestamp = 0;
		String line = reader.readLine();
		while (line != null) {
			if (currentState == State.PAUSED) {
				Thread.sleep(1000);
			} else {
				String[] tokens = line.split(" ", 2);
				long timestamp = Integer.parseInt(tokens[0]);
				Thread.sleep((int) ((timestamp - lastTimestamp) / speedup));
				writer.println(tokens[1]);
				lastTimestamp = timestamp;
				line = reader.readLine();
			}
		}
	}

	private void closeConnections() {
		try {
			if (clientSocket != null) {
				clientSocket.close();
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Connection to player closed for client {}", clientSocket.getRemoteSocketAddress());
				}
			}
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void stop() {
		closeConnections();
		if (thread != null) {
			thread.interrupt();
		}
	}

	public synchronized void pause() {
		if (currentState == State.RUNNING) {
			updateCurrentState(State.PAUSED);
		} else {
			pauseRequested = true;
		}
	}

	public synchronized void resume() {
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
				LOGGER.error("Player file is not valid or readable: {}", filePath);
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
		configManager.setConfig(PROP_FILE_PATH, filePath.toString());
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
		configManager.setConfig(PROP_PORT, port);
	}

	public void setFileEncoding(Charset fileEncoding) {
		this.fileEncoding = fileEncoding;
		configManager.setConfig(PROP_FILE_ENCODING, fileEncoding.name());
	}

	public void setStreamEncoding(Charset streamEncoding) {
		this.streamEncoding = streamEncoding;
		configManager.setConfig(PROP_FILE_ENCODING, streamEncoding.name());
	}

	public State getCurrentState() {
		return currentState;
	}
}
