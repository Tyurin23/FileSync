package ru.tyurin.filesync.client.util.event;

/**
 * User: tyurin
 * Date: 7/10/13
 * Time: 3:01 PM
 */
public class Event {

	private EventType type;

	public Event(EventType type) {
		this.type = type;
	}

	public EventType getType() {
		return type;
	}
}
