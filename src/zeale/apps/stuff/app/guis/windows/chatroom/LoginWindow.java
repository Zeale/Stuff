package zeale.apps.stuff.app.guis.windows.chatroom;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

class LoginWindow extends Window {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}
	
	private @FXML void login() {
		
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginGUI.fxml"));
		loader.setController(this);
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (Exception e) {
			throw new WindowLoadFailureException("Failed to load the UI for the Chatroom's Login window.", e);
		}
	}

}
