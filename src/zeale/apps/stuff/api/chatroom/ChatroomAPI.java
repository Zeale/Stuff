package zeale.apps.stuff.api.chatroom;

public final class ChatroomAPI {
	private final static String DEFAULT_CONNECTION_ADDRESS = "dusttoash.org";
	private final static int DEFAULT_CONNECTION_PORT = 42069;// Go figure why that was picked.

	public static String getDefaultConnectionAddress() {
		return DEFAULT_CONNECTION_ADDRESS;
	}

	public static int getDefaultConnectionPort() {
		return DEFAULT_CONNECTION_PORT;
	}
}
