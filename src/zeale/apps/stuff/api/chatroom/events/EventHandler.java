package zeale.apps.stuff.api.chatroom.events;

public interface EventHandler<E extends Event> {
	void handle(E event);

	static <E extends Event> EventHandler<E> listener(Runnable onFired) {
		return x -> onFired.run();
	}

}
