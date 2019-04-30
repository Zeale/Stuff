package zeale.apps.stuff.app.guis.windows.webrequests;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

public class WebrequestGUI extends Window {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	private @FXML TextField requestDialog;

	private @FXML void selectCustomRequest(ActionEvent e) {
		requestDialog.setDisable(false);
	}

	private @FXML void selectRequest(ActionEvent e) {
		requestDialog.setDisable(true);
		Object source = e.getSource();
		if (source instanceof MenuItem)
			requestDialog.setText(((MenuItem) source).getText());
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("WebrequestGUI.fxml"));
		loader.setController(this);
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			throw new WindowLoadFailureException("Failed to load the UI for the Web Request Window.", e);
		}
	}

}
