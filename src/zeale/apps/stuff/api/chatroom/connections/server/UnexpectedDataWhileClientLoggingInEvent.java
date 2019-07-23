package zeale.apps.stuff.api.chatroom.connections.server;

import java.io.Serializable;
import java.time.Instant;

import org.alixia.javalibrary.networking.sockets.Client;

import zeale.apps.stuff.api.chatroom.events.EventType;

public class UnexpectedDataWhileClientLoggingInEvent extends ChatroomServerConnectionEvent {
	private final Serializable data;
	public static final EventType<UnexpectedDataWhileClientLoggingInEvent> UNEXPECTED_DATA_WHILE_CLIENT_LOGGING_IN_EVENT = new EventType<>(
			CHATROOM_SERVER_CONNECTION_EVENT);

	public final Serializable getData() {
		return data;
	}

	public UnexpectedDataWhileClientLoggingInEvent(ChatroomServer server, Client client, Serializable data) {
		super(server, client);
		this.data = data;
	}

	public UnexpectedDataWhileClientLoggingInEvent(Instant timestamp, ChatroomServer server, Client client,
			Serializable data) {
		super(timestamp, server, client);
		this.data = data;
	}

}
