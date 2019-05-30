package zeale.apps.stuff.app.guis.windows.encryption;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

public class EncryptionWindow extends Window {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

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

	private EncryptionAlgorithms algorithm = EncryptionAlgorithms.AES;

	private @FXML void encryptInput() {
		try {
			outputField.setText(algorithm.hexEncrypt(keyField.getText(), inputField.getText()));
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private @FXML void decryptOutput() {
		try {
			inputField.setText(algorithm.hexDecrypt(keyField.getText(), outputField.getText()));
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static byte[] hashKey(String key, String algorithm) {
		// TODO
		return null;
	}

}
