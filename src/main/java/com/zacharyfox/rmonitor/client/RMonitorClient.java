package com.zacharyfox.rmonitor.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zacharyfox.rmonitor.config.ConfigurationManager;
import com.zacharyfox.rmonitor.entities.RaceManager;
import com.zacharyfox.rmonitor.message.MessageFactory;
import com.zacharyfox.rmonitor.utils.Estimator;
import com.zacharyfox.rmonitor.utils.Recorder;

public class RMonitorClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(RMonitorClient.class);

	public static final String DEFAULT_HOST = "127.0.0.1";
	public static final int DEFAULT_PORT = 50000;
	public static final String DEFAULT_ENCODING = Charset.defaultCharset().name();
	public static final int DEFAULT_RETRY_MAX = 3;
	public static final int DEFAULT_RETRY_TIMEOUT = 10000;

	private static final String PROP_HOST = "client.host";
	private static final String PROP_PORT = "client.port";
	private static final String PROP_ENCODING = "client.encoding";
	private static final String PROP_RETRY_MAX = "client.retry.max";
	private static final String PROP_RETRY_TIMEOUT = "client.retry.timeout";

	private static RMonitorClient instance;

	private String host;
	private int port;
	private Charset encoding;
	private int maxRetries;
	private int retryTimeout;

	private Thread thread;
	private State currentState;
	private List<BiConsumer<State, State>> listenerList;
	private ConfigurationManager configManager;

	private Recorder recorder;
	private Estimator estimator;

	public enum State {
		STARTED, CONNECTING, CONNECTED, STOPPING, STOPPED
	}

	private RMonitorClient() {
		listenerList = new ArrayList<>();
		currentState = State.STOPPED;

		recorder = Recorder.getInstance();
		estimator = Estimator.getInstance();

		configManager = ConfigurationManager.getInstance();
		host = configManager.getConfig(PROP_HOST, DEFAULT_HOST);
		port = configManager.getConfig(PROP_PORT, DEFAULT_PORT);
		maxRetries = configManager.getConfig(PROP_RETRY_MAX, DEFAULT_RETRY_MAX);
		retryTimeout = configManager.getConfig(PROP_RETRY_TIMEOUT, DEFAULT_RETRY_TIMEOUT);

		String encodingString = configManager.getConfig(PROP_ENCODING, DEFAULT_ENCODING);
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

		int retryCounter = 0;
		while (canConnect(retryCounter)) {
			updateCurrentState(State.CONNECTING);
			sleepRetryTimeout(retryCounter);

			try (Socket socket = new Socket(host, port)) {
				updateCurrentState(State.CONNECTED);
				readMessages(socket.getInputStream());
			} catch (UnknownHostException | IllegalArgumentException e) {
				e.printStackTrace();
				updateCurrentState(State.STOPPING);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				retryCounter++;
			}
		}
		updateCurrentState(State.STOPPED);
	}

	private boolean canConnect(int retryCounter) {
		return currentState != State.STOPPING && (maxRetries == -1 || retryCounter <= maxRetries);
	}

	private void sleepRetryTimeout(int retryCounter) {
		if (retryCounter > 0) {
			try {
				Thread.sleep(retryTimeout);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			LOGGER.info("Retry: {}", retryCounter);
		}
	}

	private boolean readMessages(InputStream inputStream) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, encoding))) {
			String line;

			while (currentState == State.CONNECTED && (line = reader.readLine()) != null) {
				processMessage(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void processMessage(String message) {
		RaceManager.getInstance().processMessage(MessageFactory.createMessage(message));
		recorder.push(message);
		estimator.update();
	}

	public synchronized void stop() {
		if (currentState != State.STOPPED) {
			updateCurrentState(State.STOPPING);
			thread.interrupt();
		}
	}

	private void updateCurrentState(State newState) {
		if (currentState != newState) {
			State oldState = currentState;
			currentState = newState;
			notifyStateChangeListener(oldState, newState);
		}
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

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	public void setRetryTimeout(int retryTimeout) {
		this.retryTimeout = retryTimeout;
	}

	public State getCurrentState() {
		return currentState;
	}
}
