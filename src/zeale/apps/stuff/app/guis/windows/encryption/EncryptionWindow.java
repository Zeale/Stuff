package zeale.apps.stuff.app.guis.windows.encryption;

import java.security.GeneralSecurityException;

import javafx.fxml.FXML;
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
		// TODO Auto-generated method stub

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
