package zeale.apps.stuff.api.chatroom.connections.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.alixia.javalibrary.networking.sockets.Client;

import zeale.apps.stuff.api.chatroom.ChatroomAPI;

public class ChatroomConnection {
	private final Client client;

	public ChatroomConnection(String host, int port) throws UnknownHostException, IOException {
		this(new Client(new Socket(host, port)));
	}

	public ChatroomConnection(String host) throws UnknownHostException, IOException {
		this(host, ChatroomAPI.getDefaultConnectionPort());
	}

	public ChatroomConnection(int port) throws UnknownHostException, IOException {
		this(ChatroomAPI.getDefaultConnectionAddress(), port);
	}

	public ChatroomConnection() throws UnknownHostException, IOException {
		this(ChatroomAPI.getDefaultConnectionPort());
	}

	public ChatroomConnection(Client client) {
		this.client = client;
	}

	public void open() throws IllegalStateException {
		
	}

	public IOException close() {

	}

}
