package zeale.apps.stuff.api.chatroom.connections.server;

import java.time.Instant;

import org.alixia.javalibrary.networking.sockets.Client;

import zeale.apps.stuff.api.chatroom.events.EventType;

public class IncomingClientEvent extends ChatroomServerEvent {
	private final Client client;
	public static final EventType<IncomingClientEvent> INCOMING_CLIENT_EVENT = new EventType<>(CHATROOM_SERVER_EVENT);

	public Client getClient() {
		return client;
	}

	public IncomingClientEvent(ChatroomServer server, Client client) {
		super(server);
		this.client = client;
	}

	public IncomingClientEvent(Instant timestamp, ChatroomServer server, Client client) {
		super(timestamp, server);
		this.client = client;
	}

}
