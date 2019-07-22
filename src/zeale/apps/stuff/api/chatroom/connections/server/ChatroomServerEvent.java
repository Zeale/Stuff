package zeale.apps.stuff.api.chatroom.connections.server;

import java.time.Instant;

import zeale.apps.stuff.api.chatroom.events.Event;
import zeale.apps.stuff.api.chatroom.events.EventType;

public class ChatroomServerEvent extends Event {
	public static final EventType<ChatroomServerEvent> CHATROOM_SERVER_EVENT = new EventType<>(EventType.EVENT);
	private final ChatroomServer server;

	public ChatroomServer getServer() {
		return server;
	}

	public ChatroomServerEvent(ChatroomServer server) {
		this.server = server;
	}

	public ChatroomServerEvent(Instant timestamp, ChatroomServer server) {
		super(timestamp);
		this.server = server;
	}

}
