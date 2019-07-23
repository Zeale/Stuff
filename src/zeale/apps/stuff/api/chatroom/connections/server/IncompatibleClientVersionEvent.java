package zeale.apps.stuff.api.chatroom.connections.server;

import java.time.Instant;

import org.alixia.javalibrary.networking.sockets.Client;

import zeale.apps.stuff.api.chatroom.events.Event;
import zeale.apps.stuff.api.chatroom.events.EventType;

/**
 * An {@link Event} that occurs when a {@link ChatroomServer} receives a
 * connection request from a client, but determines that the client's specified
 * version is incompatible with the {@link ChatroomServer}.
 * 
 * @author Zeale
 *
 */
public class IncompatibleClientVersionEvent extends ChatroomServerConnectionEvent {
	public static final EventType<IncompatibleClientVersionEvent> INCOMPATIBLE_CLIENT_VERSION_EVENT = new EventType<>(
			CHATROOM_SERVER_CONNECTION_EVENT);
	private final String version;

	/**
	 * The {@link #getClient() client}'s version.
	 * 
	 * @return The version that was specified by the {@link Client}.
	 */
	public final String getVersion() {
		return version;
	}

	public IncompatibleClientVersionEvent(ChatroomServer server, Client client, String version) {
		super(server, client);
		this.version = version;
	}

	public IncompatibleClientVersionEvent(Instant timestamp, ChatroomServer server, Client client, String version) {
		super(timestamp, server, client);
		this.version = version;
	}

}
