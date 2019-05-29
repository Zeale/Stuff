package zeale.apps.stuff.app.guis.windows.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
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

	private @FXML void encryptInput() {

	}

	private @FXML void decryptOutput() {

	}

	private static String encrypt(String input) {
		
	}

	private static byte[] hashKey(String key, String algorithm) {
		
	}

}
