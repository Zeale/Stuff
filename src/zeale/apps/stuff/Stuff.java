package zeale.apps.stuff;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.guis.windows.Window.WindowLoadFailureException;
import zeale.apps.stuff.api.installation.ProgramArguments;
import zeale.apps.stuff.app.guis.windows.HomeWindow;
import zeale.apps.stuff.app.guis.windows.installsetup.InstallSetupWindow1;
import zeale.apps.stuff.app.guis.windows.installsetup.InstallSetupWindow2;
import zeale.apps.stuff.utilities.java.references.PhoenixReference;
import zeale.apps.tools.console.std.StandardConsole;
import zeale.apps.tools.console.std.StandardConsole.StandardConsoleView;

public class Stuff extends Application {

	private static final StandardConsole PROGRAM_CONSOLE = new StandardConsole();

	private static final PhoenixReference<Image> windowIcon = new PhoenixReference<Image>() {
		@Override
		protected Image generate() {
			return new Image("zeale/apps/stuff/rsrc/app/guis/appicon.png");
		}
	};

	private static final PhoenixReference<StandardConsoleView> PROGRAM_CONSOLE_VIEW = new PhoenixReference<StandardConsoleView>() {

		@Override
		protected StandardConsoleView generate() {
			return PROGRAM_CONSOLE.getView(makeStage());
		}

	};

	public static void displayConsole() {
		Stage stage = PROGRAM_CONSOLE_VIEW.get().getStage();
		stage.show();
		stage.requestFocus();
	}

	public static Stage makeStage() {
		Stage stage = new Stage();
		stage.getIcons().add(windowIcon.get());
		return stage;
	}

	/**
	 * The program's installation directory. This is laxly detected (as of now) by
	 * simply getting the program's working directory. Some sort of storage API will
	 * need to be made later.
	 */
	public static final File INSTALLATION_DIRECTORY = new File("").getAbsoluteFile(),
			PROPERTIES_FILE = new File(INSTALLATION_DIRECTORY, "properties.stf.dat");

	private static Stage stage;

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

	@Override
	public void start(Stage primaryStage) throws Exception {
		(stage = primaryStage).getIcons().add(windowIcon.get());
		// When the primary window is closed, we shut down the application. (This
		// behavior is very likely to change later.

		/// close exit primary-window
		primaryStage.setOnCloseRequest(event -> Platform.exit());
		primaryStage.show();

		Parameters args = getParameters();

		if (args.getNamed().containsKey(ProgramArguments.INSTALLATION_CLEANUP)) {
			try {
				new File(args.getNamed().get(ProgramArguments.INSTALLATION_CLEANUP)).delete();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		(args.getUnnamed().contains(ProgramArguments.INSTALLATION_STAGE_1) ? new InstallSetupWindow1()
				: args.getUnnamed().contains(ProgramArguments.INSTALLATION_STAGE_2) ? new InstallSetupWindow2()
						: new HomeWindow()).display(primaryStage);
	}
}
