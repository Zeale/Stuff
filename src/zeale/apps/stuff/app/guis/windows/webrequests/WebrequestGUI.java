package zeale.apps.stuff.app.guis.windows.webrequests;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.app.guis.windows.HomeWindow;

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

	private @FXML void goHome(ActionEvent event) {
		try {
			Stuff.displayWindow(new HomeWindow());
		} catch (WindowLoadFailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	private @FXML TextArea finalizedRequestBox, bodyBox, responseBox, errorBox;
	private @FXML WebView renderView;
	private @FXML TextField urlPrompt, userAgentPrompt, parameterPrompt, methodPrompt;
	private @FXML Button sendButton;

	private final BooleanProperty sendable = new SimpleBooleanProperty();

	private @FXML void initialize() {

		sendButton.disableProperty().bind(sendable);

		finalizedRequestBox.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {

			@Override
			public String call() throws Exception {
				StringBuilder request = new StringBuilder();
				request.append(methodPrompt.getText());
				if (!urlPrompt.getText().isEmpty()) {
					
				}
				return null;
			}
		}, bodyBox.textProperty(), urlPrompt.textProperty(), userAgentPrompt.textProperty(),
				parameterPrompt.textProperty(), methodPrompt.textProperty()));
	}

	private @FXML synchronized void send(ActionEvent e) {
		// TODO Code
	}

	private @FXML void clearErrorLog(ActionEvent e) {
		errorBox.clear();
	}

}
