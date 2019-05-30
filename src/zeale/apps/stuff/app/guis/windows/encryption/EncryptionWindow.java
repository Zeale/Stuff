package zeale.apps.stuff.app.guis.windows.encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.BadPaddingException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;

public class EncryptionWindow extends Window {

	@Override
	public void destroy() {
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("EncryptionGUI.fxml"));
		loader.setController(this);
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			throw new WindowLoadFailureException("Failed to load the UI for the Encryption Window.", e);
		}
	}

	private @FXML TextArea inputField, outputField;
	private @FXML TextField keyField;// TODO
	private @FXML TilePane algorithmSelectionPane;
	private final ToggleGroup algorithmSelectionToggleGroup = new ToggleGroup();

	private @FXML void initialize() {
		for (Node n : algorithmSelectionPane.getChildren())
			if (n instanceof Toggle)
				algorithmSelectionToggleGroup.getToggles().add((Toggle) n);
		algorithmSelectionToggleGroup.selectToggle(algorithmSelectionToggleGroup.getToggles().get(0));
	}

	private @FXML void encryptInput() {
		EncryptionAlgorithms algorithm = EncryptionAlgorithms
				.valueOf(((RadioButton) algorithmSelectionToggleGroup.getSelectedToggle()).getText());
		try {
			outputField.setText(algorithm.hexEncrypt(keyField.getText(), inputField.getText()));
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private @FXML void decryptOutput() {
		EncryptionAlgorithms algorithm = EncryptionAlgorithms
				.valueOf(((RadioButton) algorithmSelectionToggleGroup.getSelectedToggle()).getText());
		try {
			inputField.setText(algorithm.hexDecrypt(keyField.getText(), outputField.getText()));
		} catch (BadPaddingException e) {
			Logging.wrn("The key you gave is invalid.");
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
