package zeale.apps.stuff.api.chatroom.connections;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;

import org.alixia.javalibrary.networking.sockets.Client;
import org.alixia.javalibrary.networking.sockets.Server;

import zeale.apps.stuff.api.chatroom.ChatroomAPI;

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

	public ChatroomServer() throws IOException {
		this(ChatroomAPI.getDefaultConnectionPort());
	}

	public ChatroomServer(int port) throws IOException {
		this(new ServerSocket(port));
	}

	public ChatroomServer(ServerSocket socket) {
		this(new Server(socket));
	}

	public ChatroomServer(Server server) {
		listener = new ChatroomConnectionListener(server) {

			@Override
			protected void handleIncomingConnection(Client connection) {
				// TODO Auto-generated method stub

			}
		};
	}

	/**
	 * Launches this {@link ChatroomServer} which opens it to accept connections, on
	 * a freshly created thread. Once a connection is accepted, <b>a new thread is
	 * automatically created to handle it</b>. On this thread, the user is and the
	 * {@link #handleUserConnected(ChatroomUser)} method is called, on that thread,
	 * with a new {@link ChatroomUser} object representing the newly connected user.
	 */
	public void launch() throws IllegalStateException {
		listener.open();
	}

	@Override
	public void close() throws IOException {
		listener.close();
	}

}
