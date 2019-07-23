package zeale.apps.stuff.api.chatroom.connections.server;

import java.io.IOException;
import java.time.Instant;

import org.alixia.javalibrary.networking.sockets.Client;

import zeale.apps.stuff.api.chatroom.events.EventType;

public class ErrorWhileClientLoggingInEvent extends ChatroomServerConnectionEvent {

	/**
	 * The {@link IOException} thrown while reading login information.
	 */
	private final IOException exception;

	public final IOException getException() {
		return exception;
	}

	public static EventType<ErrorWhileClientLoggingInEvent> ERROR_WHILE_CLIENT_LOGGING_IN_EVENT = new EventType<>(
			CHATROOM_SERVER_CONNECTION_EVENT);

	public ErrorWhileClientLoggingInEvent(ChatroomServer server, Client client, IOException exception) {
		super(server, client);
		this.exception = exception;
	}

	public ErrorWhileClientLoggingInEvent(Instant timestamp, ChatroomServer server, Client client,
			IOException exception) {
		super(timestamp, server, client);
		this.exception = exception;
	}

}
