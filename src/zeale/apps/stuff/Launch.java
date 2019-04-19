package zeale.apps.stuff;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import zeale.apps.stuff.api.installation.ProgramArguments;
import zeale.apps.stuff.app.guis.windows.HomeWindow;
import zeale.apps.stuff.app.guis.windows.installsetup.InstallSetupWindow1;
import zeale.apps.stuff.app.guis.windows.installsetup.InstallSetupWindow2;

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
