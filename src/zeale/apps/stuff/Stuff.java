package zeale.apps.stuff;

import java.io.File;

import org.alixia.chatroom.api.items.LateLoadItem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.guis.windows.Window.WindowLoadFailureException;
import zeale.apps.stuff.api.installation.ProgramArguments;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.api.modules.Module;
import zeale.apps.stuff.app.console.StuffBasicConsoleLogic;
import zeale.apps.stuff.app.guis.windows.HomeWindow;
import zeale.apps.stuff.app.guis.windows.installsetup.InstallSetupWindow1;
import zeale.apps.stuff.app.guis.windows.installsetup.InstallSetupWindow2;
import zeale.apps.stuff.utilities.java.references.PhoenixReference;
import zeale.apps.tools.console.std.StandardConsole;
import zeale.apps.tools.console.std.StandardConsole.StandardConsoleView;

public class Stuff extends Application {

	public static final StandardConsole PROGRAM_CONSOLE = new StandardConsole();

	private static Parameters programParameters;

	public static Parameters getProgramParameters() {
		return programParameters;
	}

	static {
		PROGRAM_CONSOLE.applyLogic(new StuffBasicConsoleLogic(PROGRAM_CONSOLE));
	}

	private static final PhoenixReference<Image> windowIcon = new PhoenixReference<Image>() {
		@Override
		protected Image generate() {
			return new Image("zeale/apps/stuff/rsrc/app/guis/appicon.png");
		}
	};

	@Override
	public void init() throws Exception {
		programParameters = getParameters();
	}

	public static void displayHome() throws RuntimeException {
		try {
			Stuff.displayWindow(new HomeWindow());
		} catch (WindowLoadFailureException e) {
			Logging.err(e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * The program's default view for its console. This should be accessed on the
	 * JavaFX Application thread in case a new view needs to be made.
	 */
	private static final LateLoadItem<StandardConsoleView> PROGRAM_CONSOLE_VIEW = new LateLoadItem<>(() -> {
		Stage stage = makeStage();
		stage.setHeight(800);
		stage.setWidth(1000);
		stage.setMinHeight(650);
		stage.setMinWidth(800);
		return PROGRAM_CONSOLE.getView(stage);
	});

	/**
	 * The program's installation directory. This is laxly detected (as of now) by
	 * simply getting the program's working directory. Some sort of storage API will
	 * need to be made later.
	 */
	public static final File INSTALLATION_DIRECTORY = new File("").getAbsoluteFile(),
			PROPERTIES_FILE = new File(INSTALLATION_DIRECTORY, "properties.stf.dat"),
			APPLICATION_DATA = new File(INSTALLATION_DIRECTORY, "App Data"),
			MODULE_DATA = new File(INSTALLATION_DIRECTORY, "Module Data");

	private static Stage stage;

	public static void displayConsole() {
		if (!Platform.isFxApplicationThread())
			Platform.runLater(Stuff::displayConsole);
		else {
			Stage stage = PROGRAM_CONSOLE_VIEW.get().getStage();
			stage.show();
			stage.requestFocus();
		}
	}

	public static void displayWindow(Window window) throws WindowLoadFailureException {
		window.display(stage);
	}

	public static void displayWindow(Window window, ApplicationProperties props) throws WindowLoadFailureException {
		window.display(stage, props);
	}

	public static void main(String[] args) {
		Platform.setImplicitExit(false);
		launch(Stuff.class, args);
	}

	/**
	 * Creates a new {@link Stage} and styles it to fit with the application. This
	 * function must be called on the application thread.
	 *
	 * @return The newly created {@link Stage}.
	 * @throws InterruptedException In case the thread is interrupted while the FX
	 *                              Application thread makes the stage.
	 */
	public static Stage makeStage() {
		Stage stage = new Stage();
		prepareStage(stage);
		return stage;
	}

	private static void prepareStage(Stage stage) {
		stage.getIcons().add(windowIcon.get());
		stage.setFullScreenExitHint("Press F11 to exit fullscreen mode.");
		stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (event.getCode() == KeyCode.F11) {
				stage.setFullScreen(!stage.isFullScreen());
				event.consume();
			}
		});
		stage.setTitle("Stuff");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void start(Stage primaryStage) throws Exception {
		prepareStage(stage = primaryStage);
		// When the primary window is closed, we shut down the application. (This
		// behavior is very likely to change later.

		/// close exit primary-window
		primaryStage.setOnCloseRequest(event -> Platform.exit());
		primaryStage.show();

		Parameters args = getParameters();

		if (args.getNamed().containsKey(ProgramArguments.INSTALLATION_CLEANUP)) {
			File file = new File(args.getNamed().get(ProgramArguments.INSTALLATION_CLEANUP));
			try {
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
				Logging.wrn("Failed to delete temporary files needed for installation.");
				Logging.wrn("File location: " + file);
			}
		}
		if (args.getNamed().containsKey(ProgramArguments.LAUNCH_MODULE)) {
			Class<?> cls = Class.forName(args.getNamed().get(ProgramArguments.LAUNCH_MODULE));
			try {
				if (Module.class.isAssignableFrom(cls))
					((Class<? extends Module>) cls).newInstance().launch();
				else
					throw new RuntimeException(
							"The specified module class is not a subclass of Module, and so, cannot be loaded as a module. (Class: "
									+ args.getNamed().get(ProgramArguments.LAUNCH_MODULE) + ")");
			} catch (Exception e) {
				throw new RuntimeException(
						"An error occurred while trying to launch the module class specified via a command line argument: "
								+ args.getNamed().get(ProgramArguments.LAUNCH_MODULE),
						e);
			}
		} else
			(args.getUnnamed().contains(ProgramArguments.INSTALLATION_STAGE_1) ? new InstallSetupWindow1()
					: args.getUnnamed().contains(ProgramArguments.INSTALLATION_STAGE_2) ? new InstallSetupWindow2()
							: new HomeWindow()).display(primaryStage);
	}

	@Override
	public void stop() throws Exception {
		Window.destroyStage(stage);
	}
}
