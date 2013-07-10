package ru.tyurin.util;

import org.apache.log4j.Logger;
import ru.tyurin.util.event.Event;
import ru.tyurin.util.event.EventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TODO clear old events
public class MessageSystem implements Runnable {

	public static Logger LOG = Logger.getLogger(MessageSystem.class);

	private static MessageSystem system;

	private boolean refreshFileSystem = false;

	private Map<EventType, List<Event>> events = new HashMap<>();

	private MessageSystem() {
		super();
	}

	public synchronized void addEvent(Event ev) {
		if (events.containsKey(ev.getType())) {
			events.get(ev.getType()).add(ev);
		} else {
			List<Event> eventsList = new ArrayList<>();
			eventsList.add(ev);
			events.put(ev.getType(), eventsList);
		}
	}

	public List<Event> getEvent(EventType type) {
		if (events.containsKey(type)) {
			return new ArrayList<>(events.get(type));
		} else {
			return new ArrayList<>();
		}
	}


	public void refreshFileSystem() {
		refreshFileSystem = true;
	}

	public boolean isRefreshFileSystem() {
		try {
			return refreshFileSystem;
		} finally {
			refreshFileSystem = false;
		}

	}

	public static synchronized MessageSystem getInstance() {
		if (system == null) {
			system = new MessageSystem();
		}
		return system;
	}

	@Override
	public void run() {
		LOG.info("Starting message system...");
		while (!Thread.currentThread().isInterrupted()) {
		}
		LOG.info("Message system stopped");
	}
}
