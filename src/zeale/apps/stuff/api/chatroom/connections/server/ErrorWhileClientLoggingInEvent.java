package zeale.apps.stuff.api.chatroom.connections.server;

import java.time.Instant;

import org.alixia.javalibrary.networking.sockets.Client;

import zeale.apps.stuff.api.chatroom.events.EventType;

public class ErrorWhileClientLoggingInEvent extends ChatroomServerConnectionEvent {

	/**
	 * The {@link Exception} thrown while reading login information.
	 */
	private final Exception exception;

	public final Exception getException() {
		return exception;
	}

	public static final EventType<ErrorWhileClientLoggingInEvent> ERROR_WHILE_CLIENT_LOGGING_IN_EVENT = new EventType<>(
			CHATROOM_SERVER_CONNECTION_EVENT);

	public ErrorWhileClientLoggingInEvent(ChatroomServer server, Client client, Exception exception) {
		super(server, client);
		this.exception = exception;
	}

	public ErrorWhileClientLoggingInEvent(Instant timestamp, ChatroomServer server, Client client,
			Exception exception) {
		super(timestamp, server, client);
		this.exception = exception;
	}

}
