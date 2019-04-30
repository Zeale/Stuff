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

			private boolean sendable = true;

			private String blockSend() {
				WebrequestGUI.this.sendable.set(sendable = false);
				return "???";
			}

			private void unblockSend() {
				if (sendable)
					WebrequestGUI.this.sendable.set(true);
			}

			@Override
			public String call() throws Exception {
				sendable = true;
				synchronized (WebrequestGUI.this) {

					// Request
					String txt = methodPrompt.getText();
					if (txt.isEmpty())
						txt = blockSend();
					else
						for (char c : txt.toCharArray())
							if (Character.isWhitespace(c)) {
								txt = blockSend();
								break;
							}
					finalizedRequestBox.setText(txt + " HTTP/1.1");

					// TODO Move this stuff to methods.
					//Query (either 
					String postContent = null;

					try {
						URL url = new URL(urlPrompt.getText());
						finalizedRequestBox.appendText(url.getPath());
						if (txt.equals("GET"))
							finalizedRequestBox.appendText(url.getQuery());
						else
							postContent = url.getQuery();
					} catch (Exception e) {
						finalizedRequestBox.appendText(blockSend());
					}

					unblockSend();

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
