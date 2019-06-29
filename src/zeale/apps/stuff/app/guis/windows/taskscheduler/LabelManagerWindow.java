package zeale.apps.stuff.app.guis.windows.taskscheduler;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

public class LabelManagerWindow extends Window {

	private @FXML TilePane labelView;
	private @FXML TextField labelSearch;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		// TODO Auto-generated method stub

	}

}
