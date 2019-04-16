package zeale.apps.stuff.app.guis.windows.calculator;

import java.io.IOException;

import org.alixia.libs.evaluator.Evaluator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

public final class CalculatorWindow extends Window {

	private @FXML Button searchButton;
	private @FXML TextField inputField;

	private @FXML void initialize() {

		try {
			ImageView searchIcon = new ImageView("/zeale/apps/stuff/rsrc/app/gui/windows/calculator/Search Icon.png");
			searchIcon.setPreserveRatio(true);
			searchIcon.setFitHeight(16);
			searchButton.setGraphic(searchIcon);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private @FXML void buttonPushed(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof Button) {
			inputField.appendText(((Button) source).getText());
		} else {
			// TODO Print error to console.
		}
	}

	private @FXML void functionPushed(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof Button) {
			inputField.appendText(((Button) source).getText() + "(");
		} else {
			// TODO Print error to console.
		}
	}

	private @FXML void clearInputField(ActionEvent event) {
		inputField.clear();
	}

	private @FXML void solve(ActionEvent event) {
		try {
			inputField.setText(Evaluator.solveToString(inputField.getText()));
		} catch (Exception e) {
			inputField.setText("~~" + e.getMessage());
		}
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
