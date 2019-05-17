package zeale.apps.stuff.app.guis.windows.webrequests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
	private @FXML TextField urlPrompt, userAgentPrompt, hostPrompt, acceptLanguagePrompt, connectionPrompt,
			methodPrompt;
	private @FXML Button sendButton, stopButton;

	private Thread sendThread = new Thread();

	private @FXML void initialize() {

		finalizedRequestBox.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {

			@Override
			public String call() throws Exception {
				try {
					StandardWebRequestMethods method = StandardWebRequestMethods
							.valueOf(methodPrompt.getText().toUpperCase());
					Map<String, String> parameters = new HashMap<>();
					if (!hostPrompt.getText().isEmpty())
						parameters.put("Host", hostPrompt.getText());
					if (!acceptLanguagePrompt.getText().isEmpty())
						parameters.put("Accept-Language", acceptLanguagePrompt.getText());
					if (!connectionPrompt.getText().isEmpty())
						parameters.put("Connection", connectionPrompt.getText());
					return method.preview(urlPrompt.getText(), userAgentPrompt.getText(), parameters,
							bodyBox.getText());
				} catch (IllegalArgumentException | WebRequestException e) {
					return "";
				}
			}
		}, bodyBox.textProperty(), urlPrompt.textProperty(), userAgentPrompt.textProperty(), hostPrompt.textProperty(),
				methodPrompt.textProperty(), acceptLanguagePrompt.textProperty(), connectionPrompt.textProperty()));
	}

	@SuppressWarnings("deprecation")
	private @FXML synchronized void stop() {
		if (sendThread.isAlive())
			// TODO Use interrupt() and see where bottlenecks are?
			sendThread.stop();
	}

	private @FXML synchronized void send(ActionEvent e) {
		if (sendThread.isAlive())
			Logging.err("A current request is in progress. Please stop it to attempt to send another request.");
		else {

			sendButton.setDisable(true);
			stopButton.setDisable(false);

			Map<String, String> parameters = new HashMap<>();
			if (!hostPrompt.getText().isEmpty())
				parameters.put("Host", hostPrompt.getText());
			if (!acceptLanguagePrompt.getText().isEmpty())
				parameters.put("Accept-Language", acceptLanguagePrompt.getText());
			if (!connectionPrompt.getText().isEmpty())
				parameters.put("Connection", connectionPrompt.getText());
			StandardWebRequestMethods method;
			try {
				method = StandardWebRequestMethods.valueOf(methodPrompt.getText());
			} catch (IllegalArgumentException e1) {
				Logging.err("Custom methods are not yet supported.");
				sendButton.setDisable(false);
				stopButton.setDisable(true);
				return;
			}

			sendThread = new Thread(() -> {
				try {
					String result = method.send(urlPrompt.getText(), userAgentPrompt.getText(), parameters,
							bodyBox.getText());
					Platform.runLater(() -> {
						responseBox.setText(result);
						renderView.getEngine().loadContent(result.substring(result.indexOf("\r\n\r\n") + 2));
					});
				} catch (WebRequestException e1) {
					Logging.err(e1.getMessage());
				} finally {// When interrupted, an exception is thrown.
					sendButton.setDisable(false);
					stopButton.setDisable(true);
				}
			});
			sendThread.setDaemon(true);
			sendThread.start();
		}
	}

	private @FXML void clearErrorLog(ActionEvent e) {
		errorBox.clear();
	}

}
