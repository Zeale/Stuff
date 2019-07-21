package zeale.apps.stuff.api.chatroom.events;

public final class EventType<E extends Event> {
	public static final EventType<Event> EVENT = new EventType<>();
	private final EventType<? super E> parent;

	private EventType() {
		parent = null;
	}

	public EventType(EventType<? super E> parent) {
		if (parent == null)
			throw null;
		this.parent = parent;
	}

	public EventType<? super E> getParent() {
		return parent;
	}
}
