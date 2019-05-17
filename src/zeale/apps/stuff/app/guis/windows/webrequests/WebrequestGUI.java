package zeale.apps.stuff.app.guis.windows.webrequests;

import java.io.IOException;
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
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.app.guis.windows.HomeWindow;
import zeale.apps.stuff.app.guis.windows.webrequests.WebRequestMethod.WebRequestException;

public class WebrequestGUI extends Window {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	private @FXML void selectCustomRequest(ActionEvent e) {
		methodPrompt.setDisable(false);
	}

	private @FXML void selectRequest(ActionEvent e) {
		methodPrompt.setDisable(true);
		Object source = e.getSource();
		if (source instanceof MenuItem)
			methodPrompt.setText(((MenuItem) source).getText());
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
				try {
					StandardWebRequestMethods method = StandardWebRequestMethods
							.valueOf(methodPrompt.getText().toUpperCase());
					return method.preview(urlPrompt.getText(), userAgentPrompt.getText(), null, bodyBox.getText());
				} catch (IllegalArgumentException | WebRequestException e) {
					return "";
				}
			}
		}, bodyBox.textProperty(), urlPrompt.textProperty(), userAgentPrompt.textProperty(),
				parameterPrompt.textProperty(), methodPrompt.textProperty()));
	}

	private @FXML synchronized void send(ActionEvent e) {
		try {
			StandardWebRequestMethods method = StandardWebRequestMethods.valueOf(methodPrompt.getText());
			try {
				String result = method.send(urlPrompt.getText(), userAgentPrompt.getText(), null, bodyBox.getText());
				responseBox.setText(result);
				renderView.getEngine().loadContent(result.substring(result.indexOf("\r\n\r\n") + 2));
			} catch (WebRequestException e2) {
				Logging.err(e2.getMessage());
			}
		} catch (IllegalArgumentException e1) {
			Logging.err("Custom methods are not yet supported.");
		}
	}

	private @FXML void clearErrorLog(ActionEvent e) {
		errorBox.clear();
	}

}
