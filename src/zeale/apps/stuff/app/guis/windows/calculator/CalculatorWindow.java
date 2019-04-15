package zeale.apps.stuff.app.guis.windows.calculator;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

public final class CalculatorWindow extends Window {

	private @FXML VBox leftAccordionWrapper;

	private @FXML void initialize() {
		StackPane.setAlignment(leftAccordionWrapper, Pos.CENTER_LEFT);
	}

	private @FXML void buttonPushed(ActionEvent event) {

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	// TODO Add a checked exception for window loading failures.
	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("CalculatorGUI.fxml"));
		loader.setController(this);
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			throw new WindowLoadFailureException("Failed to load the UI for the Calculator Window.", e);
		}
	}

}
