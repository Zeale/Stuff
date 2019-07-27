package zeale.apps.stuff.api.chatroom.connections.server;

import java.io.IOException;
import java.net.ServerSocket;

import org.alixia.javalibrary.networking.sockets.Client;
import org.alixia.javalibrary.networking.sockets.Server;

import zeale.apps.stuff.api.chatroom.events.Event;
import zeale.apps.stuff.api.chatroom.events.EventHandler;
import zeale.apps.stuff.api.chatroom.events.EventManager;
import zeale.apps.stuff.api.chatroom.events.EventType;

/**
 * <p>
 * This class provides an abstraction directly above the {@link Server} class by
 * automatically waiting for connections on its own {@link Thread}, and then
 * calling {@link #handleIncomingConnection(Client)}, passing the new connection
 * as an argument, on new {@link Thread}s. Furthermore, it must be
 * {@link #open()}ed to start accepting connections, which will start its
 * acception thread.
 * </p>
 * <p>
 * Additionally, this class provides event handling for
 * {@link ConnectionErrorEvent}s, any of which will result in the closing of
 * this {@link ChatroomConnectionListener}.
 * </p>
 * <p>
 * The benefits of the {@link ChatroomServer} class over this one are that:
 * <ol>
 * <li>Whenever this {@link ChatroomConnectionListener} encounters an error, it
 * is considered dead and is closed, while a {@link ChatroomServer} will detect
 * this and will simply open another {@link ChatroomConnectionListener} in place
 * of the old one.</li>
 * <li>{@link ChatroomServer}s expect incoming connections to give certain bits
 * of information, like the version of the chatroom client, and then await log
 * in credentials. If received information is out of line, the
 * {@link ChatroomServer} will handle the situation accordingly.</li>
 * </ul>
 * </p>
 * 
 * @author Zeale
 *
 */
public abstract class ChatroomConnectionListener {

	public ChatroomConnectionListener(int port) throws IOException {
		this(new Server(new ServerSocket(port)));
	}

	public final class ConnectionErrorEvent extends Event {
		private final Exception exception;

		public ConnectionErrorEvent(Exception exception) {
			this.exception = exception;
		}

		public ChatroomConnectionListener getListener() {
			return ChatroomConnectionListener.this;
		}

		public Exception getException() {
			return exception;
		}
	}

	public static final EventType<ConnectionErrorEvent> CONNECTION_ERROR_EVENT = new EventType<>(EventType.EVENT);

	private final EventManager<Event> eventManager = new EventManager<>();

	public <T extends Event> void register(EventType<T> type, EventHandler<? super T> handler) {
		eventManager.register(type, handler);
	}

	public <T extends Event> void unregsiter(EventType<T> type, EventHandler<? super T> handler) {
		eventManager.unregsiter(type, handler);
	}

	private final Server server;
	private boolean closed;

	public ChatroomConnectionListener(Server server) {
		this.server = server;
	}

	private Thread acceptionThread = new Thread() {
		@Override
		public void run() {
			while (!closed) {
				try {
					Client client = server.acceptConnection();
					new Thread() {

						@Override
						public void run() {
							try {
								handleIncomingConnection(client);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.start();
				} catch (Exception e) {
					if (!consumeExceptions) {
						close();
						eventManager.fire(CONNECTION_ERROR_EVENT, new ConnectionErrorEvent(e));
					}
				}
			}
		}
	};

	/**
	 * <p>
	 * Sets the {@link Thread#setDaemon(boolean) daemon} status of the
	 * {@link Thread} object which is used to accept incoming connections.
	 * </p>
	 * <p>
	 * This method must be invoked before the thread has started.
	 * </p>
	 * 
	 * @param daemon Whether or not the {@link Thread} should be a daemon thread.
	 */
	public void setDaemon(boolean daemon) {
		acceptionThread.setDaemon(daemon);
	}

	/**
	 * @throws IllegalStateException In case this {@link ChatroomConnectionListener}
	 *                               has already been opened.
	 */
	public void open() throws IllegalStateException {
		acceptionThread.start();
	}

	protected abstract void handleIncomingConnection(Client connection);

	private volatile boolean consumeExceptions;

	public IOException close() {
		try {
			consumeExceptions = true;
			server.close();
			closed = true;
			return null;
		} catch (IOException e) {
			closed = true;
			return e;
		} finally {
			consumeExceptions = false;
		}
	}

}
