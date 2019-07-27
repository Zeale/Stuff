package zeale.apps.stuff.api.chatroom.connections.client;

import org.alixia.javalibrary.networking.sockets.Client;

import zeale.apps.stuff.api.chatroom.events.Event;
import zeale.apps.stuff.api.chatroom.events.EventHandler;
import zeale.apps.stuff.api.chatroom.events.EventManager;
import zeale.apps.stuff.api.chatroom.events.EventType;

public class ChatroomClient {
	private Client client;

	private final EventManager<Event> eventManager = new EventManager<>();

	public EventManager<Event> getEventManager() {
		return eventManager;
	}

	public <T extends Event> void register(EventType<T> type, EventHandler<? super T> handler) {
		eventManager.register(type, handler);
	}

	public <T extends Event> void unregsiter(EventType<T> type, EventHandler<? super T> handler) {
		eventManager.unregsiter(type, handler);
	}

	private final int port;
	private final String host;

	public ChatroomClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

}
