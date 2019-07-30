package zeale.apps.stuff.app.guis.windows.modules;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import zeale.applicationss.notesss.utilities.Utilities;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.utilities.java.references.LazyReference;

public class ModuleWindow extends Window {

	/**
	 * This action ditches references to previous, possibly launched modules. This
	 * action will still take place even if the modules are running, in which case
	 * they will continue to run and will use their {@link URLClassLoader}s for
	 * loading classes they need further down the road, but the references that
	 * {@link Stuff} keeps to those class loaders and classes etc., will be
	 * destroyed, so the modules (and their classes) will become available for
	 * garbage collection once they finally stop functioning.
	 */
	private @FXML void reloadAllModules() {
		LOADED_MODULES.regenerate();
	}

	private final class ModuleItem extends VBox {

		private final MenuItem deleteModule = new MenuItem("Delete");
		private final ContextMenu rightClickMenu = new ContextMenu(deleteModule);
		private final Module module;

		{
			setSpacing(10);
			setAlignment(Pos.CENTER);
			setFillWidth(true);
			setPickOnBounds(true);
		}

		private final ImageView icon;

		public ModuleItem(Module module) {
			getStyleClass().add("module-item");
			Text title = new Text(module.getName());
			title.getStyleClass().add("text");
			getChildren().addAll(icon = new ImageView(module.getIcon()), title);
			icon.getStyleClass().add("image-view");
			title.setStyle("-fx-font-size: 1.4em;");
			setOnMouseClicked(event -> {
				if (event.getButton() == MouseButton.SECONDARY) {
					rightClickMenu.show(this, event.getScreenX(), event.getScreenY());
					event.consume();
				} else if (event.getButton() == MouseButton.PRIMARY)
					try {
						module.load().launch();
					} catch (ModuleLoadException e1) {
						Logging.err("Failed to launch the module: \"" + module.getName() + "\".");
						Logging.err(e1);
					} catch (Exception e2) {
						Logging.err("An unexpected error occurred while trying to launch the module: \""
								+ module.getName() + "\".");
						Logging.err(e2);
					}
			});

			icon.setPreserveRatio(true);
			icon.setFitWidth(128);
			moduleViewMapping.put(this.module = module, this);
			if (fits(getName(), searchField.getText()))
				moduleBox.getChildren().add(this);

			deleteModule.setOnAction(__ -> delete());

		}

		public void delete() {
			if (LOADED_MODULES.exists())
				LOADED_MODULES.get().remove(module);
			module.delete();
		}

		public String getName() {
			return module.getName();
		}

		public void remove() {
			synchronized (ModuleWindow.this) {
				moduleBox.getChildren().remove(this);
				moduleViewMapping.remove(module);
			}
		}

	}

	// TODO Add drag and drop functionality.
	private static final DropShadow DEFAULT_MODULE_HOVER_EFFECT = new DropShadow();

	static {
		DEFAULT_MODULE_HOVER_EFFECT.setSpread(0.35);
		DEFAULT_MODULE_HOVER_EFFECT.setRadius(35);
	}

	private final static LazyReference<File> MODULE_INSTALLATION_DIRECTORY = LazyReference.create(() -> {
		File file = new File(Stuff.INSTALLATION_DIRECTORY, "Modules");
		file.mkdirs();
		return file;
	});

	private static final LazyReference<ObservableList<Module>> LOADED_MODULES = new LazyReference<ObservableList<Module>>() {

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

	private static boolean fits(String name, String query) {
		return name.toLowerCase().contains(query.toLowerCase());
	}

	public static void loadModule(File module) {
		File newFile = new File(MODULE_INSTALLATION_DIRECTORY.get(), module.getName());
		try {
			if (newFile.exists()) {
				Logging.err("The module: \"" + module.getAbsolutePath()
						+ "\" could not be loaded because a module with the same file name already exists.");
				return;
			}
			Files.copy(module.toPath(), newFile.toPath());
			if (LOADED_MODULES.exists())
				LOADED_MODULES.get().add(new Module(module));
		} catch (IOException e) {
			Logging.err("Failed to copy the module: " + module.getAbsolutePath()
					+ " to the module directory, (which is at: " + MODULE_INSTALLATION_DIRECTORY.get().getAbsolutePath()
					+ "), so that it can be loaded.");
			Logging.err(e);
		} catch (ModuleLoadException e) {
			newFile.delete();
			Logging.err("Failed to load the module: " + module.getAbsolutePath());
			Logging.err(e);
		}
	}

	public static void loadModules(Collection<File> modules) {
		for (File f : modules)
			if (f.getName().endsWith(".jar"))
				loadModule(f);
			else if (f.getName().endsWith(".zip"))
				loadZip(f);
	}

	public static void loadPackage(File pckge) {
		/* TODO */
	}

	private static void loadZip(File zip) {
		loadPackage(zip);
	}

	private @FXML FlowPane moduleBox;
	private @FXML TextField searchField;

	private @FXML Text dragInfoText;

	private @FXML StackPane dragBox;

	private final Map<Module, ModuleItem> moduleViewMapping = new HashMap<>();

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	private @FXML void goHome() {
		Stuff.displayHome();
	}

	/**
	 *
	 */
	private @FXML void initialize() {
		for (Module m : LOADED_MODULES.get())
			new ModuleItem(m);

		LOADED_MODULES.get().addListener((ListChangeListener<Module>) c -> {
			while (c.next())
				if (c.wasAdded())
					for (Module m1 : c.getAddedSubList())
						new ModuleItem(m1);
				else if (c.wasRemoved())
					for (Module m2 : c.getRemoved())
						moduleViewMapping.get(m2).remove();
		});

		// This may later need to be optimized by a method involving multiple lists.
		searchField.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			synchronized (this) {
				if (newValue.isEmpty())
					moduleBox.getChildren().setAll(moduleViewMapping.values());
				else if (newValue.contains(oldValue))
					for (Iterator<Node> iterator = moduleBox.getChildren().iterator(); iterator.hasNext();) {
						Node n = iterator.next();
						if (n instanceof ModuleItem)
							if (!fits(((ModuleItem) n).getName(), newValue))
								iterator.remove();// Don't remove the module permanently, just from the view.
					}
				else
					for (ModuleItem mi : moduleViewMapping.values()) {
						boolean fits = fits(mi.getName(), newValue);
						if (!moduleBox.getChildren().contains(mi)) {
							if (fits)
								moduleBox.getChildren().add(mi);
						} else if (!fits)
							moduleBox.getChildren().remove(mi);
					}
			}
		});

		dragInfoText.setFill(Color.DARKGRAY);
		dragBox.setOnDragOver(event -> {
			if (event.getDragboard().hasFiles())
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			event.consume();
		});
		dragBox.setOnDragEntered(event -> {
			if (event.getDragboard().hasFiles()) {
				int jarCount = 0;
				for (File f : event.getDragboard().getFiles())
					if (f.getName().endsWith(".jar"))
						jarCount++;

				if (jarCount == event.getDragboard().getFiles().size()) {
					dragBox.setBackground(Utilities.getBackgroundFromColor(Color.GREEN));
					if (jarCount == 1)
						dragInfoText.setText("Release your mouse button to attempt to load the jar file.");
					else
						dragInfoText
								.setText("Release your mouse button to attempt to load " + jarCount + " jar files.");
				} else if (jarCount == 0) {
					dragBox.setBackground(Utilities.getBackgroundFromColor(Color.RED));
					dragInfoText.setText("None of those files is a jar file.");
				} else {
					dragBox.setBackground(Utilities.getBackgroundFromColor(Color.GOLD));
					dragInfoText.setText(
							"Found " + jarCount + " jar files; release your mouse button to attempt to load them.");

				}
			} else {
				dragBox.setBackground(Utilities.getBackgroundFromColor(Color.FIREBRICK));
				dragInfoText.setText("This content does not contain any files!");
			}
			event.consume();
		});
		dragBox.setOnDragExited(event -> {
			dragBox.setBackground(Utilities.getBackgroundFromColor(Color.TRANSPARENT));
			dragInfoText.setText("Drag Modules Here");
			event.consume();
		});
		dragBox.setOnDragDropped(event -> {

			CHECK: if (event.getDragboard().hasFiles()) {
				for (File f : event.getDragboard().getFiles())
					if (f.getName().endsWith(".jar"))
						break CHECK;
				return;
			}

			ModuleWindow.loadModules(event.getDragboard().getFiles());
			dragBox.setBackground(Utilities.getBackgroundFromColor(Color.TRANSPARENT));
			dragInfoText.setText("Drag Modules Here");
			event.consume();
		});

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(ModuleWindow.class.getResource("ModuleGUI.fxml"));
		loader.setController(this);
		try {
			Parent root = loader.load();
			root.getStylesheets().addAll(properties.popButtonStylesheet.get(), properties.themeStylesheet.get(),
					"zeale/apps/stuff/app/guis/windows/modules/ModuleWindow.css");
			stage.setScene(new Scene(root));
		} catch (IOException e) {
			Logging.err("Failed to show the Module window.");
			Logging.err(e);
		}
	}

}
