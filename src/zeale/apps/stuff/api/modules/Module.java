package zeale.apps.stuff.api.modules;

import java.lang.ref.WeakReference;
import java.net.URLClassLoader;

/**
 * A subtype of this class, specified in a module's
 * {@link #STUFF_MODULE_INTERNAL_MANIFEST_LOCATION module manifest}, is
 * instantiated when the module it represents is invoked. The first time this
 * happens, the module will have been loaded from the file. A new
 * {@link URLClassLoader} is created to load it, and the module is launched off
 * of that. This class loader is held with a {@link WeakReference}, however, so
 * once the module is done processing, there are no guarantees that the entire
 * module isn't unloaded from memory, as the objects, classes, and classloader
 * involved with the module are considered "inaccessible by a thread" by the
 * JVM.
 * 
 * @author Zeale
 *
 */
public abstract class Module {

	// TODO Upgrade module loading API.

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
