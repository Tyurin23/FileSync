package ru.tyurin.filesync.client.util;

import org.apache.log4j.Logger;
import ru.tyurin.filesync.client.util.event.Event;
import ru.tyurin.filesync.client.util.event.EventListener;
import ru.tyurin.filesync.client.util.event.EventType;

import java.util.*;

//TODO clear old events
public class MessageSystem implements Runnable {

	public static Logger LOG = Logger.getLogger(MessageSystem.class);

	private static MessageSystem system;


	private Map<EventType, Queue<Event>> events = new HashMap<>();
	private Map<EventType, List<EventListener>> listeners = new HashMap<>();

	protected MessageSystem() {
		super();
	}

	public synchronized void addEvent(Event ev) {
		if (events.containsKey(ev.getType())) {
			events.get(ev.getType()).add(ev);
		} else {
			Queue<Event> eventsQueue = new ArrayDeque<>();
			eventsQueue.offer(ev);
			events.put(ev.getType(), eventsQueue);
		}
	}


	public void sendToListeners(Event ev) {
		for (EventListener listener : listeners.get(ev.getType())) {
			listener.listen(ev);
		}
	}

	public void addListener(EventListener listener, EventType type) {
		if (listener == null) {
			throw new NullPointerException("Listener can't be null");
		}
		List<EventListener> listenersList = listeners.get(type);
		if (listenersList != null) {
			listenersList.add(listener);
		} else {
			listenersList = new ArrayList<>();
			listenersList.add(listener);
			listeners.put(type, listenersList);
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
			tick();
		}
		LOG.info("Message system stopped");
	}

	protected void tick() {
		for (Map.Entry<EventType, Queue<Event>> entry : events.entrySet()) {
			Event ev = entry.getValue().poll();
			if (ev != null) {
				sendToListeners(ev);
			}
		}
	}

}
