package zeale.apps.stuff.api.chatroom.events;

public interface EventHandler<E extends Event> {
	void handle(E event);
}
