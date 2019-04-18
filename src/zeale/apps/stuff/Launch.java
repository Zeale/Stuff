package zeale.apps.stuff;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
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

		if (getParameters().getNamed().containsKey("~install-cleanup")) {
			// TODO Clean old directory.
		}

		(!Stuff.PROPERTIES_FILE.exists() || getParameters().getUnnamed().contains("~install") ? new InstallSetupWindow()
				: new HomeWindow()).display(primaryStage);
	}
}
