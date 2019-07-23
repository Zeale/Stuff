package zeale.apps.stuff.api.chatroom.connections.server;

import java.time.Instant;

import org.alixia.javalibrary.networking.sockets.Client;

import zeale.apps.stuff.api.chatroom.events.EventType;

public class TimeoutWhileClientLoggingInEvent extends ChatroomServerConnectionEvent {

	public static final EventType<TimeoutWhileClientLoggingInEvent> TIMEOUT_WHILE_CLIENT_LOGGING_IN_EVENT = new EventType<>(
			CHATROOM_SERVER_CONNECTION_EVENT);

	public TimeoutWhileClientLoggingInEvent(ChatroomServer server, Client client) {
		super(server, client);
	}

	public TimeoutWhileClientLoggingInEvent(Instant timestamp, ChatroomServer server, Client client) {
		super(timestamp, server, client);
	}

}
