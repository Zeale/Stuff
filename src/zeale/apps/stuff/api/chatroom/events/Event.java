package zeale.apps.stuff.api.chatroom.events;

import java.time.Instant;

public class Event {
	private final Instant timestamp;

	public Event(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public Event() {
		this(Instant.now());
	}

}
