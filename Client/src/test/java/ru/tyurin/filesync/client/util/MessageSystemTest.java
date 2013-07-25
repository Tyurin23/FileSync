package ru.tyurin.filesync.client.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.tyurin.filesync.client.util.event.Event;
import ru.tyurin.filesync.client.util.event.EventListener;
import ru.tyurin.filesync.client.util.event.EventType;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


/**
 * User: tyurin
 * Date: 7/11/13
 * Time: 1:10 PM
 */
public class MessageSystemTest extends MessageSystem {

	MessageSystem system;
	EventListener listener;


	@Before
	public void setUp() throws Exception {
		system = MessageSystem.getInstance();
		listener = mock(EventListener.class);


	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testAddEvent() throws Exception {
		final EventType type = EventType.FILE_CHANGED;
		final Event ev = new Event(type);
		system.addListener(listener, type);


		system.addEvent(ev);
		system.tick();
		verify(listener).listen(ev);

		reset(listener);
		system.tick();

		verify(listener, never()).listen(any(Event.class));

	}

	@Test
	public void testSendToListeners() throws Exception {

	}

	@Test
	public void testAddListener() throws Exception {

	}

	@Test
	public void testGetInstance() throws Exception {

	}

	@Test
	public void testRun() throws Exception {

	}
}
