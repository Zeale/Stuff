package zeale.apps.stuff.api.chatroom.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class EventManager<E extends Event> {

	private final Map<EventType<? extends E>, Collection<EventHandler<?>>> handlerMap = new HashMap<>();

	public <T extends E> void register(EventType<T> type, EventHandler<? super T> handler) {
		if (handlerMap.containsKey(type)) {
			handlerMap.get(type).add(handler);
		} else {
			ArrayList<EventHandler<?>> handlers = new ArrayList<>();
			handlerMap.put(type, handlers);
			handlers.add(handler);
		}
	}

	public <T extends E> void unregsiter(EventType<T> type, EventHandler<? super T> handler) {
		if (handlerMap.containsKey(type)) {
			Collection<EventHandler<?>> handlers = handlerMap.get(type);
			if (handlers.remove(handler) && handlers.isEmpty())
				handlerMap.remove(type);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends E> void fire(EventType<T> type, T event) {
		EventType<E> currType = (EventType<E>) type;
		while (currType != null) {
			if (handlerMap.containsKey(currType))
				for (EventHandler<?> eh : handlerMap.get(currType))
					try {
						((EventHandler<E>) eh).handle(event);
					} catch (Exception e) {
						e.printStackTrace();
					}
			currType = event.isConsumed() ? null : (EventType<E>) currType.getParent();
		}
	}

}
