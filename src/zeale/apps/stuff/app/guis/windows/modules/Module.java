package zeale.apps.stuff.app.guis.windows.modules;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

class Module {

	private final String icon, name;
	private final File file;
	private final URLClassLoader loader;

	public Module(String icon, String name, File file) throws MalformedURLException {
		this.icon = icon;
		this.name = name;
		this.file = file;
		loader = new URLClassLoader(new URL[] { file.toURI().toURL() });
	}

	public URLClassLoader getLoader() {
		return loader;
	}

	public String getIcon() {
		return icon;
	}

	public String getName() {
		return name;
	}

	protected void delete() {
		file.delete();
	}

}
