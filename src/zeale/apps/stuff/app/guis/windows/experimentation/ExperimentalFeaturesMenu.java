package zeale.apps.stuff.app.guis.windows.experimentation;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Menu;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.app.guis.windows.HomeWindow;

public class ExperimentalFeaturesMenu extends Menu {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) {
		super.show(stage, properties);
		Button backButton = new Button("<--");
		backButton.getStyleClass().add("pop-button");
		anchorPane.getChildren().add(backButton);
		AnchorPane.setTopAnchor(backButton, 14d);
		AnchorPane.setLeftAnchor(backButton, 14d);
		
		anchorPane.getStylesheets().add(getClass().getResource("/zeale/apps/stuff/app/guis/windows/stylesheets/Pop Button.css").toExternalForm());
		anchorPane.setStyle("-pop-button--text-color:gold;-pop-button--clicked-text-color:red;");
		
		backButton.setOnAction(event -> {
			try {
				Stuff.displayWindow(new HomeWindow());
			} catch (WindowLoadFailureException e) {
				Logging.err("Failed to display the home window.");
				Logging.err(e);
			}
		});
	}

}
