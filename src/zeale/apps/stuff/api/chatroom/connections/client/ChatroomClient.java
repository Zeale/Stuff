package zeale.apps.stuff.api.chatroom.connections.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import org.alixia.javalibrary.networking.sockets.Client;
import org.alixia.javalibrary.util.Box;

//import zeale.apps.stuff.api.chatroom.events.Event;
//import zeale.apps.stuff.api.chatroom.events.EventHandler;
//import zeale.apps.stuff.api.chatroom.events.EventManager;
//import zeale.apps.stuff.api.chatroom.events.EventType;

public class ChatroomClient implements Closeable {
	private Client client;

	// TODO Update (events)

	public void send(Serializable item) throws IOException {
		client.send(item);
	}

	public IOException trySend(Serializable item) {
		return client.trySend(item);
	}

	public IOException testConnection() {
		return client.testConnection();
	}

	public Box<Serializable> read(int millisTimeout) throws ClassNotFoundException, IOException {
		return client.read(millisTimeout);
	}

	public Serializable read() throws ClassNotFoundException, IOException {
		return client.read();
	}

//	private final EventManager<Event> eventManager = new EventManager<>();
//
//	public EventManager<Event> getEventManager() {
//		return eventManager;
//	}
//
//	public <T extends Event> void register(EventType<T> type, EventHandler<? super T> handler) {
//		eventManager.register(type, handler);
//	}
//
//	public <T extends Event> void unregsiter(EventType<T> type, EventHandler<? super T> handler) {
//		eventManager.unregsiter(type, handler);
//	}

	private final int port;
	private final String host;

	public ChatroomClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public String getHost() {
		return host;
	}

	protected Client generateClient(String host, int port) throws UnknownHostException, IOException {
		return new Client(new Socket(host, port));
	}

	public void launch() throws IOException {
		if (client != null)
			throw new IllegalStateException("Chatroom client already started.");
		reinstateClient();
	}

	/**
	 * If an exception occurs while trying to {@link #generateClient(String, int)
	 * generate a new client}.
	 * 
	 * @throws IOException The {@link IOException} that occurred.
	 */
	public void reinstateClient() throws IOException {
		if (client != null)
			client.tryClose();
		client = generateClient(host, port);
	}

	@Override
	public void close() throws IOException {
		client.close();
	}

	public IOException tryClose() {
		try {
			close();
			return null;
		} catch (IOException e) {
			return e;
		}
	}

}
