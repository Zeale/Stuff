package zeale.apps.stuff.api.chatroom.connections.server;

import java.time.Instant;

import zeale.apps.stuff.api.chatroom.events.EventType;

public class ChatroomServerReopenFailureEvent extends ChatroomServerEvent {
	public static final EventType<ChatroomServerReopenFailureEvent> CHATROOM_SERVER_REOPEN_FAILURE_EVENT = new EventType<>(
			CHATROOM_SERVER_EVENT);

	public ChatroomServerReopenFailureEvent(ChatroomServer server) {
		super(server);
	}

	public ChatroomServerReopenFailureEvent(Instant timestamp, ChatroomServer server) {
		super(timestamp, server);
	}
}
