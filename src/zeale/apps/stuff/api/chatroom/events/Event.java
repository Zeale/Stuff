package zeale.apps.stuff.api.chatroom.events;

import java.time.Instant;

public class Event {
	private final Instant timestamp;
	private boolean consumed;

	public boolean isConsumed() {
		return consumed;
	}

	public void setConsumed(boolean consumed) {
		this.consumed = consumed;
	}

	public void consume() {
		setConsumed(true);
	}

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
