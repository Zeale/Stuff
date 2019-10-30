package zeale.apps.stuff.app.guis.windows.modules;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import branch.alixia.unnamed.Datamap;
import javafx.scene.image.Image;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.utilities.java.references.LazyReference;
import zeale.apps.stuff.utilities.java.references.SporadicPhoenixReference;

class Module {

	private final Image icon;
	private final String name, launchClass;
	private final URL location;

	// This is available for gc when loadedLaunchClass is ungenerated or is too.
	private final LazyReference<URLClassLoader> loader = new LazyReference<URLClassLoader>() {

		@Override
		protected URLClassLoader generate() {
			return new URLClassLoader(new URL[] { location }) {
				@Override
				protected void finalize() throws Throwable {
					super.finalize();// In case this method is overridden by parent in future Java versions.
					try {
						close();
					} catch (IOException e) {
						Logging.err("Module Jar loader cleanup couldn't be performed for the \"" + name + "\" module.");
						Logging.err(e);
					}
				}
			};
		}
	};

	// This class can only be garbage collected once all instances of it can be.
	private final SporadicPhoenixReference<Class<? extends zeale.apps.stuff.api.modules.Module>> loadedLaunchClass = new SporadicPhoenixReference<Class<? extends zeale.apps.stuff.api.modules.Module>>() {

		@SuppressWarnings("unchecked")
		@Override
		protected Class<? extends zeale.apps.stuff.api.modules.Module> generate() throws ClassNotFoundException {
			return (Class<? extends zeale.apps.stuff.api.modules.Module>) loader.get().loadClass(launchClass);
		}

	};
	private final File file;

	/**
	 * @throws IOException In case an IO error occurs while attempting to create
	 *                     this {@link Module}.
	 */
	public Module(File file) throws IOException, ModuleLoadException {
		try (ZipFile jar = new JarFile(this.file = file)) {
			ZipEntry entry = jar.getEntry(zeale.apps.stuff.api.modules.Module.STUFF_MODULE_INTERNAL_MANIFEST_LOCATION);
			if (entry == null)
				throw new ModuleLoadException("Invalid module; The manifest file could not be located inside the jar.");
			Datamap datamap = Datamap.read(jar.getInputStream(entry));

			name = datamap.get(zeale.apps.stuff.api.modules.Module.NAME_MANIFEST_KEY);
			launchClass = datamap.get(zeale.apps.stuff.api.modules.Module.LAUNCH_CLASS_MANIFEST_KEY);
			String ico = datamap.get(zeale.apps.stuff.api.modules.Module.ICON_MANIFEST_KEY);
			location = file.toURI().toURL();

			if (name == null)
				throw new ModuleLoadException("Invalid Module manifest file. The manifest must contain a name.");
			if (launchClass == null)
				throw new ModuleLoadException(
						"Invalid module manifest file. The manifest must denote a launch class for the module.");
			if (ico == null)
				throw new ModuleLoadException("Invalid module manifest file. The manfiest must contain an icon.");
			if (ico.startsWith("internal://")) {
				entry = jar.getEntry(ico.substring(11));
				if (entry == null)
					throw new ModuleLoadException("The icon for the module, \"" + name + "\", was not found.");
				icon = new Image(jar.getInputStream(entry));
			} else
				icon = new Image(ico);

		} catch (Exception e) {
			throw new ModuleLoadException("An unexpected error occurred while loading a module.");
		}

	}

	protected void delete() {
		file.delete();
	}

	public Image getIcon() {
		return icon;
	}

	public URLClassLoader getLoader() {
		return loader.get();
	}

	public String getName() {
		return name;
	}

	public zeale.apps.stuff.api.modules.Module load() throws ModuleLoadException {
		Class<?> cls;
		try {
			cls = loadedLaunchClass.get();
		} catch (Exception e) {
			throw new ModuleLoadException(
					"Failed to load the launch class for the module: \"" + name + "\"; the class could not be found.",
					e);
		}

		if (!zeale.apps.stuff.api.modules.Module.class.isAssignableFrom(cls))
			throw new ModuleLoadException(
					"The loaded launch class for the module: \"" + name + "\" is not an instance of the Module class.");
		@SuppressWarnings("unchecked")
		Class<? extends zeale.apps.stuff.api.modules.Module> clz = (Class<? extends zeale.apps.stuff.api.modules.Module>) cls;

		try {
			return clz.newInstance();// We should be careful with this.
		} catch (InstantiationException e) {
			throw new ModuleLoadException("Unable to instantiate the module, \"" + name + "\"'s module class.", e);
		} catch (IllegalAccessException e) {
			throw new ModuleLoadException("Unable to instantiate the module \"" + name
					+ "\". The Module Loader has no access to the module's class's constructor.", e);
		}
	}

	public void reload() throws ModuleLoadException {
		loader.regenerate();
		try {
			loadedLaunchClass.regenerate();
		} catch (Exception e) {
			throw new ModuleLoadException(e);
		}
	}

}
