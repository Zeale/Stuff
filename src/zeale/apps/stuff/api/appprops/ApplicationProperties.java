package zeale.apps.stuff.api.appprops;

import java.util.WeakHashMap;

import org.alixia.javalibrary.util.KeyMap;
import org.alixia.javalibrary.util.ObservableKeyMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class ApplicationProperties extends ObservableKeyMap<Object, ObservableMap<KeyMap.Key<?>, Object>> {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public final static Key<String> THEME_STYLESHEET = key(), POP_BUTTON_STYLESHEET = key();

	public final LocalKey<String> themeStylesheet = lk(THEME_STYLESHEET,
			"zeale/apps/stuff/api/guis/windows/stylesheets/BasicStyles.css"),
			popButtonStylesheet = lk(POP_BUTTON_STYLESHEET,
					"zeale/apps/stuff/api/guis/windows/stylesheets/Pop%20Button.css");

	public ApplicationProperties() {
		super(FXCollections.observableMap(new WeakHashMap<>()));
	}

}
