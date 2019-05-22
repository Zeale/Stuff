package zeale.apps.stuff.app.guis.windows;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Menu;
import zeale.apps.stuff.app.guis.windows.calculator.CalculatorWindow;
import zeale.apps.stuff.app.guis.windows.experimentation.ExperimentalFeaturesMenu;
import zeale.apps.stuff.app.guis.windows.webrequests.WebrequestWindow;

public class HomeWindow extends Menu {

	@Override
	public void destroy() {

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) {
		super.show(stage, properties);
		// Calculator
		addImageNode("/zeale/apps/stuff/rsrc/app/guis/windows/calculator/Calculator.png", () -> new CalculatorWindow(),
				"Calculator");

		// Web Requests
		addImageNode("/zeale/apps/stuff/rsrc/app/guis/windows/webrequests/WorldWeb.png", () -> new WebrequestWindow(),
				"Web Requests");

		// Console
		Label consoleLabel = new Label("Console");
		consoleLabel.setTextFill(Color.RED);
		addImageNode("/zeale/apps/stuff/rsrc/app/guis/windows/console/Icon.png", (a) -> Stuff.displayConsole(),
				consoleLabel);

		// Experimental Features
		Label experimentalFeaturesLabel = new Label("Experimental Features");
		experimentalFeaturesLabel.setTextFill(Color.RED);
		addImageNode("zeale/apps/stuff/rsrc/app/guis/windows/experimentation/Experimentation.png",
				() -> new ExperimentalFeaturesMenu(), experimentalFeaturesLabel);
	}

}
