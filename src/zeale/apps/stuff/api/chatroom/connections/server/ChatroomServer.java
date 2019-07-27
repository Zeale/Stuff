package zeale.apps.stuff.api.chatroom.connections.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;

import org.alixia.javalibrary.networking.sockets.Client;
import org.alixia.javalibrary.networking.sockets.Server;
import org.alixia.javalibrary.util.Box;

import zeale.apps.stuff.api.chatroom.ChatroomAPI;
import zeale.apps.stuff.api.chatroom.connections.client.ChatroomClient;
import zeale.apps.stuff.api.chatroom.connections.messages.EndConnectionMessage;
import zeale.apps.stuff.api.chatroom.events.Event;
import zeale.apps.stuff.api.chatroom.events.EventHandler;
import zeale.apps.stuff.api.chatroom.events.EventManager;
import zeale.apps.stuff.api.chatroom.events.EventType;

/**
 * A server that runs on this machine, awaiting for connection attempts from
 * {@link ChatroomClient}s. When a successful connection is established, the
 * server waits for the {@link ChatroomClient} to send a {@link String}
 * representing supported protocol version information. Once the client sends
 * this, the server sends its supported protocol version back, and then the
 * server awaits any query or action from the client.
 * 
 * @author Zeale
 *
 */
public class ChatroomServer implements Closeable {

	private ChatroomConnectionListener listener;
	private final EventManager<Event> eventManager = new EventManager<>();
	protected final int port;// Later it can be made so that the port can be changed.

	private boolean daemon;

	public void setDaemon(boolean daemon) {
		listener.setDaemon(this.daemon = daemon);
	}

	public boolean isDaemon() {
		return daemon;
	}

	public EventManager<Event> getEventManager() {
		return eventManager;
	}

	public <T extends Event> void register(EventType<T> type, EventHandler<? super T> handler) {
		eventManager.register(type, handler);
	}

	public <T extends Event> void unregsiter(EventType<T> type, EventHandler<? super T> handler) {
		eventManager.unregsiter(type, handler);
	}

	public ChatroomServer() throws IOException {
		this(ChatroomAPI.getDefaultConnectionPort());
	}

	public ChatroomServer(int port) throws IOException {
		this.port = port;
		reinstateListener();
	}

	protected String getVersion() {
		return ChatroomAPI.getCurrentVersion();
	}

	protected boolean acceptVersion(String version) {
		return ChatroomAPI.getCurrentVersion().contentEquals(version);
	}

	/**
	 * Handles an incoming connection. By default, this method calls
	 * {@link #converseVersion(Client)}. If that returns <code>true</code>, then
	 * this method calls {@link #converseLogin(Client)}.
	 * {@link #converseLogin(Client)} is tasked with both handling log in attempts
	 * by the client and actually logging the client in.
	 * 
	 * @param connection
	 */
	protected void handleIncomingConnection(Client connection) {
		eventManager.fire(IncomingClientEvent.INCOMING_CLIENT_EVENT,
				new IncomingClientEvent(ChatroomServer.this, connection));// Incoming Connection.
		if (converseVersion(connection))// Runs if version convo was ok.
			converseLogin(connection);
	}

	protected boolean converseVersion(Client client) {
		try {
			Box<Serializable> result = client.read(5000);// Expect String with version information.
			if (result == null) {
				eventManager.fire(TimeoutWhileClientLoggingInEvent.TIMEOUT_WHILE_CLIENT_LOGGING_IN_EVENT,
						new TimeoutWhileClientLoggingInEvent(ChatroomServer.this, client));
				client.trySend(EndConnectionMessage.STREAM_ERROR_OCCURRED);
				client.tryClose();
			} else if (!(result.value instanceof String)) {
				eventManager.fire(UnexpectedDataWhileClientLoggingInEvent.UNEXPECTED_DATA_WHILE_CLIENT_LOGGING_IN_EVENT,
						new UnexpectedDataWhileClientLoggingInEvent(ChatroomServer.this, client, result.value));
				client.trySend(EndConnectionMessage.UNEXPECTED_DATA_RECEIVED);
				client.tryClose();
			} else if (acceptVersion((String) result.value)) {
				// The version is acceptable. Send back this server's version.
				client.send(getVersion());// Catch block handles this as well.
				return true;
			} else {
				eventManager.fire(IncompatibleClientVersionEvent.INCOMPATIBLE_CLIENT_VERSION_EVENT,
						new IncompatibleClientVersionEvent(ChatroomServer.this, client, (String) result.value));
				client.trySend(EndConnectionMessage.INCOMPATIBLE_VERSION);
				client.tryClose();
			}
		} catch (ClassNotFoundException | IOException e) {
			eventManager.fire(ErrorWhileClientLoggingInEvent.ERROR_WHILE_CLIENT_LOGGING_IN_EVENT,
					new ErrorWhileClientLoggingInEvent(ChatroomServer.this, client, e));
			client.trySend(EndConnectionMessage.STREAM_ERROR_OCCURRED);
			client.tryClose();
		}
		return false;
	}

	protected void converseLogin(Client client) {
		try {
			Box<Serializable> result = client.read(10000);// Expect log in information in 10 seconds...
			// TODO Finish conversation.
		} catch (ClassNotFoundException | IOException e) {
			eventManager.fire(ErrorWhileClientLoggingInEvent.ERROR_WHILE_CLIENT_LOGGING_IN_EVENT,
					new ErrorWhileClientLoggingInEvent(ChatroomServer.this, client, e));
			client.trySend(EndConnectionMessage.STREAM_ERROR_OCCURRED);
			client.tryClose();
		}
	}

	private final void reinstateListener() throws IOException {
		listener = makeListener();
	}

	protected ChatroomConnectionListener makeListener() throws IOException {
		return new ChatroomConnectionListener(new Server(new ServerSocket(port))) {
			@Override
			protected void handleIncomingConnection(Client connection) {
				ChatroomServer.this.handleIncomingConnection(connection);
			}
		};
	}

	/**
	 * Launches this {@link ChatroomServer} which opens it to accept connections, on
	 * a freshly created thread. Once a connection is accepted, <b>a new thread is
	 * automatically created to handle it</b>. On this thread, the user is and the
	 * {@link #handleUserConnected(ChatroomUser)} method is called, on that thread,
	 * with a new {@link ChatroomUser} object representing the newly connected user.
	 * 
	 * @throws IllegalStateException In case this {@link ChatroomServer} is already
	 *                               running.
	 */
	public void launch() throws IllegalStateException {
		listener.open();
	}

	@Override
	public void close() throws IOException {
		listener.close();
		listener = null;
	}

}
