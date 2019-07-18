package zeale.apps.stuff.api.modules;

public abstract class Module {
	public static final String STUFF_MODULE_INTERNAL_MANIFEST_LOCATION = "STUFF-MODULE-MANIFEST/Manifest.mf",
			ICON_MANIFEST_KEY = "icon", LAUNCH_CLASS_MANIFEST_KEY = "launch-class", NAME_MANIFEST_KEY = "name";

	/**
	 * Launches this module. This method is called each time the user launches this
	 * module on a new instance of this class, on the JavaFX Thread.
	 *
	 * @throws Exception In case an exception occurs.
	 */
	public abstract void launch() throws Exception;
}
