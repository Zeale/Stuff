package zeale.apps.stuff.api.chatroom;

import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.chatroom.connections.ChatroomServer;

/**
 * This class (mostly) provides access to non-functional API features, such as
 * the {@link #DEFAULT_CONNECTION_PORT default port that ChatroomServer's use}.
 * 
 * @author Zeale
 *
 */
public final class ChatroomAPI {

	private ChatroomAPI() {
	}

	/**
	 * This is the address of "The Main Server," or, more specifically, my desktop.
	 * When you run {@link Stuff} and launch the chatroom, by default, it will try
	 * to connect to The Main Server. Since I wanted to make the API classes in
	 * these packages more independent and since my desktop may go offline for
	 * stupid reasons, the connection classes involved in this API are able to
	 * connect to any given address, searching for or hosting a chatroom server.
	 * This field is the default address.
	 */
	private final static String DEFAULT_CONNECTION_ADDRESS = "dusttoash.org";
	/**
	 * This is the default port that chatroom servers connect to.
	 */
	private final static int DEFAULT_CONNECTION_PORT = 42069;// Go figure why that was picked.

	/**
	 * Gets the {@link #DEFAULT_CONNECTION_ADDRESS default connection address}: (the
	 * address of The Main Server).
	 * 
	 * @return The default connection address.
	 */
	public static String getDefaultConnectionAddress() {
		return DEFAULT_CONNECTION_ADDRESS;
	}

	/**
	 * @return The {@link #DEFAULT_CONNECTION_PORT default port} used by
	 *         {@link ChatroomServer}s.
	 */
	public static int getDefaultConnectionPort() {
		return DEFAULT_CONNECTION_PORT;
	}

	private final static String CURRENT_VERSION = "1";

	public static String getCurrentVersion() {
		return CURRENT_VERSION;
	}

}
