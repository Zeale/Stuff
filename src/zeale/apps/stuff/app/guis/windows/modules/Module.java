package zeale.apps.stuff.app.guis.windows.modules;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

import branch.alixia.unnamed.Datamap;
import javafx.scene.image.Image;

class Module {

	private static final String STUFF_MODULE_INTERNAL_MANIFEST_LOCATION = "STUFF-MODULE-MANIFEST/Manifest.mf",
			ICON_MANIFEST_KEY = "icon", LAUNCH_CLASS_MANIFEST_KEY = "launch-class", NAME_MANIFEST_KEY = "name";
	private final Image icon;
	private final String name;
	private final Class<? extends zeale.apps.stuff.api.modules.Module> launchClass;
	private final File file;
	private final URLClassLoader loader;

	@SuppressWarnings("unchecked")
	public Module(File file) throws IOException, ModuleLoadException {
		try (ZipFile jar = new JarFile(this.file = file)) {
			Datamap datamap = Datamap.read(jar.getInputStream(jar.getEntry(STUFF_MODULE_INTERNAL_MANIFEST_LOCATION)));

			name = datamap.get(NAME_MANIFEST_KEY);
			String launchClass = datamap.get(LAUNCH_CLASS_MANIFEST_KEY);
			String ico = datamap.get(ICON_MANIFEST_KEY);

			if (name == null)
				throw new ModuleLoadException("Invalid Module manifest file. The manifest must contain a name.");
			if (launchClass == null)
				throw new ModuleLoadException(
						"Invalid module manifest file. The manifest must denote a launch class for the module.");
			if (ico == null)
				throw new ModuleLoadException("Invalid module manifest file. The manfiest must contain an icon.");

			Class<?> cls;
			try {
				cls = (loader = new URLClassLoader(new URL[] { file.toURI().toURL() })).loadClass(launchClass);
			} catch (ClassNotFoundException e) {
				throw new ModuleLoadException("Failed to load the launch class for the module: \"" + name
						+ "\"; the class could not be found.", e);
			}

			if (!zeale.apps.stuff.api.modules.Module.class.isAssignableFrom(cls))
				throw new ModuleLoadException("The loaded launch class for the module: \"" + name
						+ "\" is not an instance of the Module class.");
			this.launchClass = (Class<? extends zeale.apps.stuff.api.modules.Module>) cls;

			icon = ico.startsWith("internal://") ? new Image(jar.getInputStream(jar.getEntry(ico = ico.substring(11))))
					: new Image(ico);

		}

	}

	public URLClassLoader getLoader() {
		return loader;
	}

	public Image getIcon() {
		return icon;
	}

	public Class<? extends zeale.apps.stuff.api.modules.Module> getLaunchClass() {
		return launchClass;
	}

	public String getName() {
		return name;
	}

	protected void delete() {
		file.delete();
	}

}
