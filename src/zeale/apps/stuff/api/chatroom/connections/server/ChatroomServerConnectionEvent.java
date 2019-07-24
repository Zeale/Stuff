package zeale.apps.stuff.api.chatroom.connections.server;

import java.time.Instant;

import org.alixia.javalibrary.networking.sockets.Client;

import zeale.apps.stuff.api.chatroom.events.EventType;

public class ChatroomServerConnectionEvent extends ChatroomServerEvent {
	public static final EventType<ChatroomServerConnectionEvent> CHATROOM_SERVER_CONNECTION_EVENT = new EventType<>(
			CHATROOM_SERVER_EVENT), CLIENT_DISCONNECTED_EVENT = new EventType<>(CHATROOM_SERVER_CONNECTION_EVENT);
	private final Client client;

	public Client getClient() {
		return client;
	}

	public ChatroomServerConnectionEvent(ChatroomServer server, Client client) {
		super(server);
		this.client = client;
	}

	public ChatroomServerConnectionEvent(Instant timestamp, ChatroomServer server, Client client) {
		super(timestamp, server);
		this.client = client;
	}
}
