package zeale.apps.stuff.api.chatroom.connections.server;

import java.time.Instant;

import org.alixia.javalibrary.networking.sockets.Client;

import zeale.apps.stuff.api.chatroom.events.EventType;

public class IncomingClientEvent extends ChatroomServerConnectionEvent {

	public IncomingClientEvent(ChatroomServer server, Client client) {
		super(server, client);
	}

	public IncomingClientEvent(Instant timestamp, ChatroomServer server, Client client) {
		super(timestamp, server, client);
	}

	public static final EventType<IncomingClientEvent> INCOMING_CLIENT_EVENT = new EventType<>(
			CHATROOM_SERVER_CONNECTION_EVENT);

}
