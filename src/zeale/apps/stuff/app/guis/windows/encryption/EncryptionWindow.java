package zeale.apps.stuff.app.guis.windows.encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Labeled;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.app.guis.windows.HomeWindow;

public class EncryptionWindow extends Window {

	@Override
	public void destroy() {
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("EncryptionGUI.fxml"));
		loader.setController(this);
		try {
			Parent root = loader.load();
			stage.setScene(new Scene(root));
			root.getStylesheets().addAll(properties.popButtonStylesheet.get(), properties.themeStylesheet.get(),
					"zeale/apps/stuff/app/guis/windows/encryption/Encryption.css");
		} catch (IOException e) {
			throw new WindowLoadFailureException("Failed to load the UI for the Encryption Window.", e);
		}
	}

	private @FXML void goHome(ActionEvent event) {
		try {
			Stuff.displayWindow(new HomeWindow());
		} catch (WindowLoadFailureException e) {
			Logging.err(e);
		}
	}

	private @FXML TextArea inputField, outputField;
	private @FXML TextField keyField;// TODO
	private @FXML TilePane algorithmSelectionPane;
	private final ToggleGroup algorithmSelectionToggleGroup = new ToggleGroup();

	private final static Object RADIO_BUTTON_ALGORITHM_MAP_KEY = new Object();

	private EncryptionAlgorithm getAlgorithm(Toggle toggle) {
		return (EncryptionAlgorithm) toggle.getProperties().get(RADIO_BUTTON_ALGORITHM_MAP_KEY);
	}

	private @FXML void initialize() {
		for (EncryptionAlgorithms ea : EncryptionAlgorithms.values()) {
			RadioButton button = new RadioButton(ea.algorithmName());
			button.getProperties().put(RADIO_BUTTON_ALGORITHM_MAP_KEY, ea);
			algorithmSelectionToggleGroup.getToggles().add(button);
			algorithmSelectionPane.getChildren().add(button);
		}
		algorithmSelectionToggleGroup.selectToggle(algorithmSelectionToggleGroup.getToggles().get(0));
	}

	private @FXML void encryptInput() {
		Toggle selectedToggle = algorithmSelectionToggleGroup.getSelectedToggle();
		EncryptionAlgorithm algorithm = getAlgorithm(selectedToggle);
		try {
			outputField.setText(algorithm.hexEncrypt(keyField.getText(), inputField.getText()));
		} catch (InvalidKeyException e) {
			Logging.err("The " + (selectedToggle instanceof Labeled
					? (algorithm instanceof EncryptionAlgorithms ? ((EncryptionAlgorithms) algorithm).algorithmName()
							: ((Labeled) selectedToggle).getText())
					: "currently selected encryption") + " algorithm can't be used with your Java installation.");
		} catch (GeneralSecurityException e) {
			Logging.err(e);
		} catch (UnsupportedOperationException e) {
			Logging.err(selectedToggle instanceof Labeled
					? "The " + ((RadioButton) selectedToggle).getText()
							+ " algorithm is not available with your Java installation."
					: algorithm instanceof EncryptionAlgorithms
							? "The " + ((EncryptionAlgorithms) algorithm).algorithmName()
									+ " algorithm is not available with your Java installation."
							: "The currently selected algorithm is not available with your Java installation.");
		}
	}

	private @FXML void decryptOutput() {
		Toggle selectedToggle = algorithmSelectionToggleGroup.getSelectedToggle();
		EncryptionAlgorithm algorithm = getAlgorithm(selectedToggle);
		try {
			inputField.setText(algorithm.hexDecrypt(keyField.getText(), outputField.getText()));
		} catch (BadPaddingException e) {
			Logging.err("The key you gave is invalid.");
		} catch (GeneralSecurityException e) {
			Logging.err(e);
		} catch (UnsupportedOperationException e) {
			Logging.err(selectedToggle instanceof Labeled
					? "The " + ((Labeled) selectedToggle).getText()
							+ " algorithm is not available with your Java installation."
					: algorithm instanceof EncryptionAlgorithms
							? "The " + ((EncryptionAlgorithms) algorithm).algorithmName()
									+ " algorithm is not available with your Java installation."
							: "The currently selected algorithm is not available with your Java installation.");
		}
	}

}
