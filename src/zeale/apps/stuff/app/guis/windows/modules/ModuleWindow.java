package zeale.apps.stuff.app.guis.windows.modules;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.utilities.java.references.PhoenixReference;

public class ModuleWindow extends Window {

	// TODO Add drag and drop functionality.

	private final class ModuleItem {

		private final Module module;

		private final VBox box = new VBox();
		{
			box.setSpacing(10);
			box.setAlignment(Pos.CENTER);
			box.setFillWidth(true);
			moduleBox.getChildren().add(box);
		}

		public void remove() {
			moduleBox.getChildren().remove(box);
			moduleViewMapping.remove(module);
		}

		private final ImageView icon;

		public ModuleItem(Module module) {
			box.getChildren().addAll(icon = new ImageView(module.getIcon()), new Text(module.getName()));
			icon.setPreserveRatio(true);
			icon.setFitWidth(128);
			moduleViewMapping.put(this.module = module, this);
		}

	}

	private final static PhoenixReference<File> MODULE_INSTALLATION_DIRECTORY = PhoenixReference
			.create((Supplier<File>) () -> {
				File file = new File(Stuff.INSTALLATION_DIRECTORY, "Modules");
				file.mkdirs();
				return file;
			});

	private static final PhoenixReference<ObservableList<Module>> LOADED_MODULES = new PhoenixReference<ObservableList<Module>>() {

		@Override
		protected ObservableList<Module> generate() {
			MODULE_INSTALLATION_DIRECTORY.get().mkdirs();
			ObservableList<Module> modules = FXCollections.observableArrayList();

			File[] files = MODULE_INSTALLATION_DIRECTORY.get().listFiles();
			if (files == null)
				Logging.err(
						"Failed to load the modules from the disk; the module storage directory is not a directory: "
								+ MODULE_INSTALLATION_DIRECTORY.get().getAbsolutePath());
			else
				for (File f : files)
					if (f.getName().endsWith(".jar"))
						// Inside the Jar file there should be a text file, loadable as a Datamap, that
						// has a name, an optional icon, and a reference to a main class. The validity
						// of all of these will be verified during the Module class's construction.
						try {
							modules.add(new Module(f));
						} catch (IOException e) {
							Logging.err("Failed to load the module file: \"" + f.getName()
									+ "\". An IO error occurred while attempting to load the file. (Module file's path: \""
									+ f.getAbsolutePath() + "\")");
							Logging.err(e);
						} catch (ModuleLoadException e) {
							Logging.err("Failed to load the module file: \"" + f.getName() + "\". Error message: "
									+ e.getLocalizedMessage() + " (Module file's path: \"" + f.getAbsolutePath()
									+ "\")");
						}

			return modules;
		}
	};

	private @FXML FlowPane moduleBox;
	private final ObservableList<Module> loadedModules = LOADED_MODULES.get();// Strong reference uWu

	private final Map<Module, ModuleItem> moduleViewMapping = new HashMap<>();

	private @FXML void initialize() {
		for (Module m : loadedModules)
			new ModuleItem(m);

		loadedModules.addListener((ListChangeListener<Module>) c -> {
			while (c.next())
				if (c.wasAdded())
					for (Module m1 : c.getAddedSubList())
						new ModuleItem(m1);
				else if (c.wasRemoved())
					for (Module m2 : c.getRemoved())
						moduleViewMapping.get(m2).remove();
		});
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(ModuleWindow.class.getResource("ModuleGUI.fxml"));
		loader.setController(this);
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			Logging.err("Failed to show the Module window.");
			Logging.err(e);
		}
	}

	public static void loadModules(Collection<File> modules) {
		for (File f : modules) {
			if (f.getName().endsWith(".jar")) {
				File newFile = new File(MODULE_INSTALLATION_DIRECTORY.get(), f.getName());
				try {
					Files.copy(f.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					if (LOADED_MODULES.exists())
						LOADED_MODULES.get().add(new Module(f));
				} catch (IOException e) {
					Logging.err("Failed to copy the module: " + f.getAbsolutePath()
							+ " to the module directory, (which is at: "
							+ MODULE_INSTALLATION_DIRECTORY.get().getAbsolutePath() + "), so that it can be loaded.");
					Logging.err(e);
				} catch (ModuleLoadException e) {
					newFile.delete();
					Logging.err("Failed to load the module: " + f.getAbsolutePath());
					Logging.err(e);
				}
			}
		}
	}

	public static void loadModule(File module) {
		/* TODO */
	}

	public static void loadPackage(File pckge) {
		/* TODO */
	}

}
