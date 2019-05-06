package zeale.apps.stuff.app.guis.windows.updates;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.app.guis.windows.HomeWindow;

public class UpdateWindow extends Window {
	
	private @FXML VBox updateSelectorBox;

	private @FXML void initialize() {

	}
	
	private @FXML void goHome(ActionEvent e) {
		try {
			Stuff.displayWindow(new HomeWindow());
		} catch (WindowLoadFailureException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		// TODO Auto-generated method stub

	}

}
