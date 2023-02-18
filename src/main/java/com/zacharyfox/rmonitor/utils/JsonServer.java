package com.zacharyfox.rmonitor.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.zacharyfox.rmonitor.config.ConfigurationManager;
import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.entities.RaceManager;
import com.zacharyfox.rmonitor.entities.RaceTO;

public class JsonServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonServer.class);

	public static final String DEFAULT_HOST = "0.0.0.0";
	public static final int DEFAULT_PORT = 8080;

	private static final String PROP_HOST = "server.host";
	private static final String PROP_PORT = "server.port";

	private String host;
	private int port;

	private Server jettyServer;
	private State currentState;
	private List<BiConsumer<State, State>> listenerList;
	private ConfigurationManager configManager;

	public enum State {
		STARTED, RUNNING, STOPPED
	}

	private static JsonServer instance;

	public static JsonServer getInstance() {
		if (instance == null) {
			instance = new JsonServer();
		}

		return instance;
	}

	private JsonServer() {
		currentState = State.STOPPED;
		listenerList = new ArrayList<>();

		configManager = ConfigurationManager.getInstance();
		host = configManager.getConfig(PROP_HOST, DEFAULT_HOST);
		port = configManager.getConfig(PROP_PORT, DEFAULT_PORT);
	}

	public synchronized void start() {
		if (currentState == State.STOPPED) {
			updateCurrentState(State.STARTED);
			try {
				getJettyServer().start();
				updateCurrentState(State.RUNNING);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void stop() {
		try {
			getJettyServer().stop();
			updateCurrentState(State.STOPPED);
		} catch (Exception e) {
			e.printStackTrace();
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

	private Server getJettyServer() {
		if (jettyServer == null) {
			jettyServer = new Server(new InetSocketAddress(host, port));
			jettyServer.setHandler(new JsonHandler());
		}

		return jettyServer;
	}

	public void setHost(String host) {
		this.host = host;
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

	public State getCurrentState() {
		return currentState;
	}

	private static class JsonHandler extends AbstractHandler {

		private RaceManager raceManager;

		private Gson gson;

		public JsonHandler() {
			raceManager = RaceManager.getInstance();
			gson = new Gson();
		}

		@Override
		public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			// Declare response encoding and types
			response.setContentType("application/json");

			// set CORS Headers
			response.addHeader("Access-Control-Allow-Headers",
					"Access-Control-Allow-Origin, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
			if (response.getHeader("Access-Control-Allow-Origin") == null) {
				response.addHeader("Access-Control-Allow-Origin", "*");
			}

			Object resultTO;

			LOGGER.info("Path Info: {}", request.getPathInfo());
			// We split the path of the request
			String[] pathInfoParts = request.getPathInfo().split("/");
			if (pathInfoParts.length > 1 && pathInfoParts[1].equals("race")) {
				// object selected is race
				LOGGER.debug("Path is race: {}", pathInfoParts[1]);
				// check for an ID as second part
				if (pathInfoParts.length > 2 && pathInfoParts[2].matches("\\d+")) {
					int raceID = Integer.parseInt(pathInfoParts[2]);
					LOGGER.trace("Returning race with ID: {}", raceID);
					resultTO = getRaceTO(raceID);
				} else {
					LOGGER.trace("Returning current race");
					resultTO = getRaceToReturn();
				}
			} else if (pathInfoParts.length > 1 && pathInfoParts[1].equals("races")) {
				// default we return the race
				resultTO = getAllRaceTOs();
			} else {
				resultTO = getRaceToReturn();
			}

			if (resultTO != null) {
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}

			// Write back response
			response.getWriter().println(gson.toJson(resultTO));

			// Inform jetty that this request has now been handled
			baseRequest.setHandled(true);
		}

		private RaceTO getRaceToReturn() {
			RaceTO raceTO;

			Race currentRace = raceManager.getCurrentRace();
			if (currentRace.getId() >= 0) {
				raceTO = RaceTO.from(currentRace);
			} else {
				Race previousRace = raceManager.getPreviousRace();

				if (previousRace != null) {
					raceTO = RaceTO.from(previousRace);
				} else {
					raceTO = RaceTO.from(currentRace);
				}
			}

			return raceTO;

		}

		private RaceTO getRaceTO(int id) {
			Race race = raceManager.getRace(id);

			if (race != null) {
				return RaceTO.from(race);
			}

			return null;
		}

		private RaceTO[] getAllRaceTOs() {
			return raceManager.getAllRaces().stream().map(RaceTO::from).toArray(RaceTO[]::new);
		}
	}
}
