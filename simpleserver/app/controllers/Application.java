package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import play.mvc.Controller;
import play.mvc.Result;
import java.net.*;
import java.io.*;

public class Application extends Controller {
	// Test case
	private List<String> names = new ArrayList<>();
	private Socket clientSocket;
	private ServerSocket server;
	private int userCount = 0;

	public Result greet(String arg) {
		names.add(arg);
		return ok("Hello, " + names);
	}

	private HashMap<Integer, ArrayList<Event>> sessions = new HashMap<>();

	public int generateID() {
		// Creates new ID of length 6. ex: "XXXXXX, 153045, 987540, ..."
		String sessionID = "";
		for (int i = 0; i < 6; i++) {
			sessionID += Math.round(Math.random() * 9);
		}
		// Check if ID already exists
		if (sessions.containsValue(Integer.parseInt(sessionID))) {
			return generateID();
		}
		return Integer.parseInt(sessionID);
	}

	public Result newSession() {
		// User clicks new session button
		// Creates new empty session with generated ID
		int sessionID = generateID();
		ArrayList<Event> events = new ArrayList<>();

		// checkCurrent()
		// Add all events that happened before session went live.
		sessions.put(sessionID, events);
		return ok("New session created. Session ID: " + sessionID);
	}

	public Result listSessions() {
		// testing purposes
		String data = "";
		for(int session : sessions.keySet()) {
			data += "ID: " + session + " Event count: " + sessions.get(session).size() + "\n";
		}
		return ok("Current sessions:\n" + data);
	}

	public Result add(Integer sessionID, String eventName) {
		ArrayList<Event> session = sessions.get(sessionID);
		if (session == null)
			return ok("Invalid session.");
		else {
			Event E = new Event();
			E.setName(eventName);
			session.add(E);
			sessions.replace(sessionID, session);
			return ok("added.");
		}
	}

	public Result connect(int sessionID) {
		ArrayList<Event> session = sessions.get(sessionID);
		if (session == null) {
			return ok("Invalid ID: " + sessionID);
		} else {
			userCount++;
			return ok("Connected " + userCount + "\n" + buildEventData(sessionID));
		}
	}

	public Result checkCurrent(int sessionID) {
		return ok("TEST");
	}

	public String buildEventData(int sessionID) {
		String events = "";
		ArrayList<Event> session = sessions.get(sessionID);
		for (Event e : session) {
			events += e.getName() + "\n";
		}
		return events;
	}
}
