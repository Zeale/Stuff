package zeale.apps.stuff.api.guis.windows;

import org.alixia.javalibrary.util.KeyMap;

import javafx.stage.Stage;
import zeale.applicationss.notesss.utilities.colors.ColorList;
import zeale.apps.stuff.api.appprops.ApplicationProperties;

/**
 * A {@link Window} should aim to stylize itself using the
 * 
 * @author Zeale
 *
 */
public interface Window {

	static final ApplicationProperties DEFAULT_APPLICATION_PROPERTIES = new ApplicationProperties();
	static final KeyMap<Object>.Key<ColorList<?>> WINDOW_COLORS = DEFAULT_APPLICATION_PROPERTIES
			.put(ColorList.ORANGE_BLUE_BLACK);

	/**
	 * Cleans up this window.
	 */
	void destroy();

	/**
	 * Shows this {@link Window} on the specified {@link Stage}. A {@link Window}
	 * may only be shown on a single stage at once.
	 * 
	 * @param stage The {@link Stage} to show on.
	 */
	default void display(Stage stage) {
		display(stage, DEFAULT_APPLICATION_PROPERTIES);
	}

	void display(Stage stage, ApplicationProperties properties);
}
