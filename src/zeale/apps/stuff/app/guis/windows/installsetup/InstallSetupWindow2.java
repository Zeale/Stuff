package zeale.apps.stuff.app.guis.windows.installsetup;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.app.guis.windows.HomeWindow;
import zeale.apps.tools.console.api.texts.Text;

public class InstallSetupWindow2 extends Window {

	@Override
	public void destroy() {
		stage.setMinHeight(0);
		stage.setMinWidth(0);
		stage.setMaxHeight(Double.MAX_VALUE);
		stage.setMaxWidth(Double.MAX_VALUE);
		stage.setAlwaysOnTop(false);
	}

	private Stage stage;

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		this.stage = stage;

		Text prompt = new Text(
				"Please select the locations where you'd like to include a shortcut to the program, if any.");
		CheckBox startMenu = new CheckBox("Start Menu"), desktop = new CheckBox("Desktop");
		VBox checkBoxBox = new VBox(startMenu, desktop);

		Button continueButton = new Button("Continue");

		VBox box = new VBox(20, prompt, checkBoxBox, continueButton);

		stage.setScene(new Scene(box));
		stage.show();

		stage.setMinHeight(300);
		stage.setMinWidth(200);
		stage.setMaxHeight(800);
		stage.setMaxWidth(1000);
		stage.centerOnScreen();
		stage.setAlwaysOnTop(true);
	}

}
