package zeale.apps.stuff.app.guis.windows.calendar;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

public class CalendarWindow extends Window {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(CalendarWindow.class.getResource("CalendarGUI.fxml"));
		loader.setController(this);

		try {
			Parent root = loader.load();
			root.getStylesheets().addAll(properties.popButtonStylesheet.get(), properties.themeStylesheet.get());
			stage.setScene(new Scene(root));
		} catch (IOException e) {
			throw new WindowLoadFailureException(e);
		}
	}

	private @FXML void goHome() {
		Stuff.displayHome();
	}

}
