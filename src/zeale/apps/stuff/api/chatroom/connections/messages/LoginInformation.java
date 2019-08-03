package zeale.apps.stuff.api.chatroom.connections.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LoginInformation extends Message {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public static final String SESSION_ID_KEY = "session-id", USERNAME_KEY = "username", PASSWORD_KEY = "password";

	private final Map<String, Serializable> data = new HashMap<>();

	public int size() {
		return data.size();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public boolean containsKey(Object key) {
		return data.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return data.containsValue(value);
	}

	public Serializable put(String key, Serializable value) {
		return data.put(key, value);
	}

	public Serializable remove(Object key) {
		return data.remove(key);
	}

	public void clear() {
		data.clear();
	}

	public Serializable replace(String key, Serializable value) {
		return data.replace(key, value);
	}

	public Serializable get(Object key) {
		return data.get(key);
	}

	@Override
	public String toString() {
		return data.toString();
	}

}
