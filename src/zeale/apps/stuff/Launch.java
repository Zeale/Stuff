package zeale.apps.stuff;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import zeale.apps.stuff.api.installation.InstallationData;
import zeale.apps.stuff.app.guis.windows.HomeWindow;
import zeale.apps.stuff.app.guis.windows.installsetup.InstallSetupWindow;

public class Launch extends Application {

	public static void main(String[] args) {
		Platform.setImplicitExit(false);
		launch(Launch.class, args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// When the primary window is closed, we shut down the application. (This
		// behavior is very likely to change later.
		/// close exit primary-window
		primaryStage.setOnCloseRequest(event -> Platform.exit());
		primaryStage.show();

		Parameters args = getParameters();

		if (args.getNamed().containsKey(InstallationData.INSTALLATION_CLEANUP_DIRECTIVE_ARGUMENT)) {
			try {
				new File(args.getNamed().get(InstallationData.INSTALLATION_CLEANUP_DIRECTIVE_ARGUMENT)).delete();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		(args.getUnnamed().contains(InstallationData.INSTALLATION_DIRECTIVE_ARGUMENT) ? new InstallSetupWindow()
				: new HomeWindow()).display(primaryStage);
	}
}
