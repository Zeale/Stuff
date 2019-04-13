package zeale.apps.stuff.api.guis.windows;

import org.alixia.javalibrary.util.KeyMap;

import javafx.scene.Scene;
import javafx.stage.Stage;
import zeale.applicationss.notesss.utilities.colors.ColorList;
import zeale.apps.stuff.api.appprops.ApplicationProperties;

/**
 * A {@link Window} should aim to stylize itself using the
 * 
 * @author Zeale
 *
 */
public abstract class Window {

	public static final ApplicationProperties DEFAULT_APPLICATION_PROPERTIES = new ApplicationProperties();
	protected static final KeyMap<Object>.Key<ColorList<?>> WINDOW_COLORS = DEFAULT_APPLICATION_PROPERTIES
			.put(ColorList.ORANGE_BLUE_BLACK);

	private static final Object STAGE_WINDOW_KEY = new Object();

	public static boolean clean(Stage stage) {
		if (stage.getProperties().containsKey(STAGE_WINDOW_KEY)) {
			((Window) stage.getProperties().get(STAGE_WINDOW_KEY)).destroy();
			stage.getProperties().remove(STAGE_WINDOW_KEY);
			return true;
		}
		return false;
	}

	/**
	 * Cleans up this window. This method should refrain from calling any methods on
	 * the {@link Stage} that this {@link Window} was originally displayed with, and
	 * should be callable multiple times with subsequent calls causing no
	 * detrimental side effects or unexpected behavior. A single call should stille
	 * be sufficient to destroy and clean up a {@link Window} however. This method
	 * does not have to (and should not) change the {@link Scene} of the
	 * {@link Stage} that this {@link Window} was originally displayed with.
	 */
	public abstract void destroy();

	/**
	 * Shows this {@link Window} on the specified {@link Stage}. A {@link Window}
	 * may only be shown on a single stage, and may only be shown once. If this
	 * method is called more than once an exception will be thrown.
	 * 
	 * @param stage The {@link Stage} to show on.
	 */
	public final void display(Stage stage) {
		display(stage, DEFAULT_APPLICATION_PROPERTIES);
	}

	private boolean called;

	public final synchronized void display(Stage stage, ApplicationProperties properties) {
		if (called)
			throw new RuntimeException("Cannot \"show\" a Window object twice.");
		if (stage.getProperties().containsKey(STAGE_WINDOW_KEY)) {
			((Window) stage.getProperties().get(STAGE_WINDOW_KEY)).destroy();
		}
		stage.getProperties().put(STAGE_WINDOW_KEY, this);
		show(stage, properties);
		called = true;
	}

	protected abstract void show(Stage stage, ApplicationProperties properties);
}
