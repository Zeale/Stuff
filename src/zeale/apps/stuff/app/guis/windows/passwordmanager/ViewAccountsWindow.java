package zeale.apps.stuff.app.guis.windows.passwordmanager;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

public class ViewAccountsWindow extends Window {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewAccountsGUI.fxml"));
		loader.setController(this);
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			throw new WindowLoadFailureException("Failed to load the Password Manager Account Viewer window's UI.", e);
		}
	}

}
