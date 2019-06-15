package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.File;
import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.utilities.java.references.PhoenixReference;
import zeale.apps.tools.api.data.files.filesystem.storage.FileStorage;

public class TaskSchedulerWindow extends Window {

	private final static PhoenixReference<FileStorage> TASK_DATA_DIR = PhoenixReference.create(
			() -> new FileStorage(new File(new File(Stuff.INSTALLATION_DIRECTORY, "App Data"), "Task Scheduler"))
					.createChild("Tasks"));

	private @FXML TextField createName, editName;
	private @FXML TextArea createDescription, editDescription;
	private @FXML CheckBox createComplete, editComplete, createUrgent, editUrgent, editSync1, editSync2;
	private @FXML Button editFlush;

	private @FXML void initialize() {
		editSync1.selectedProperty().bindBidirectional(editSync2.selectedProperty());
		editSync1.selectedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (newValue) {
				AnchorPane.setBottomAnchor(editDescription, 50d);
				editFlush.setVisible(false);
			} else {
				AnchorPane.setBottomAnchor(editDescription, 200d);
				editFlush.setVisible(true);
			}
		});
		/* TODO */
	}

	private @FXML void reload() {
		/* TODO */
	}

	private @FXML void update() {
		/* TODO */
	}

	private @FXML void flush() {
		/* TODO */
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(TaskSchedulerWindow.class.getResource("TaskSchedulerMainGUI.fxml"));
		loader.setController(this);
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			Logging.err("Failed to load the Task Scheduler window's main GUI.");
			Logging.err(e);
		}
	}

}
