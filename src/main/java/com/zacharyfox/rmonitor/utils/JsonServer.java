package com.zacharyfox.rmonitor.utils;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.gson.Gson;
import com.zacharyfox.rmonitor.client.RMonitorClient;
import com.zacharyfox.rmonitor.entities.Race;
import com.zacharyfox.rmonitor.entities.RaceTO;

public class JsonServer {
	private Server jettyServer;

	public JsonServer(int port) {
		jettyServer = new Server(port);
		jettyServer.setHandler(new JsonHandler());
	}

	public void start() {
		try {
			jettyServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		try {
			jettyServer.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class JsonHandler extends AbstractHandler {
		private Gson gson;
		private RaceTO lastRaceTO;

		public JsonHandler() {
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
			if (response.getHeader("Access-Control-Allow-Origin") == null)
				response.addHeader("Access-Control-Allow-Origin", "*");

			// Declare response status code
			response.setStatus(HttpServletResponse.SC_OK);

			Object resultTO;

			System.out.println("Path Info:" + request.getPathInfo());
			// We split the path of the request
			String[] pathInfoParts = request.getPathInfo().split("/");
			if (pathInfoParts.length > 1 && pathInfoParts[1].equals("race")) {
				// object selected is race
				// System.out.println("Path is race:" + pathInfoParts[1]);
				// check for an ID as second part
				if (pathInfoParts.length > 2 && pathInfoParts[2].matches("\\d+")) {
					int raceID = Integer.parseInt(pathInfoParts[2]);
					// System.out.println("Returning race with ID:" + raceID);
					resultTO = getRaceToReturn(raceID);
				} else {
					// System.out.println("Returning current race");
					resultTO = getRaceToReturn();
				}
			} else if (pathInfoParts.length > 1 && pathInfoParts[1].equals("races")) {
				// default we return the race
				resultTO = getRacesToReturn();
			} else {
				resultTO = getRaceToReturn();
			}

			// Write back response
			response.getWriter().println(gson.toJson(resultTO));

			// Inform jetty that this request has now been handled
			baseRequest.setHandled(true);
		}

		private RaceTO getRaceToReturn() {
			RaceTO currentRaceTO = RMonitorClient.getInstance().getRace().getRaceTO();
			// if the currentRaceTO has ID = 0 we try to get the lastRaceTO from the history
			if (lastRaceTO != null && currentRaceTO.raceID == 0) {
				currentRaceTO = Race.getToByID(lastRaceTO.raceID);
			} else {
				lastRaceTO = currentRaceTO;
			}
			return currentRaceTO;
		}

		private RaceTO getRaceToReturn(int id) {
			return Race.getToByID(id);
		}

		private RaceTO[] getRacesToReturn() {
			return Race.getAllRaceTOs();
		}
	}
}
