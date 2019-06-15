package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableView;
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

	private @FXML TableView<Task> taskView;

	private ObjectProperty<Task> selectedTask = new SimpleObjectProperty<>();

	private @FXML void initialize() {
		editSync1.selectedProperty().bindBidirectional(editSync2.selectedProperty());
		editSync1.selectedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (newValue) {
				AnchorPane.setBottomAnchor(editDescription, 50d);
				editFlush.setVisible(false);
				Task task = selectedTask.get();
				if (task != null) {
					task.completedProperty().bind(editComplete.selectedProperty());
					task.urgentProperty().bind(editUrgent.selectedProperty());
					task.nameProperty().bind(editName.textProperty());
					task.descriptionProperty().bind(editDescription.textProperty());
				}
			} else {
				AnchorPane.setBottomAnchor(editDescription, 200d);
				editFlush.setVisible(true);
				Task task = selectedTask.get();
				if (task != null) {
					task.completedProperty().unbind();
					task.urgentProperty().unbind();
					task.nameProperty().unbind();
					task.descriptionProperty().unbind();
				}
			}
		});

		selectedTask.addListener((ChangeListener<Task>) (observable, oldValue, newValue) -> {
			if (!editSync1.isSelected())
				return;
			if (oldValue != null) {
				oldValue.completedProperty().unbind();
				oldValue.urgentProperty().unbind();
				oldValue.nameProperty().unbind();
				oldValue.descriptionProperty().unbind();
			}
			if (newValue != null) {
				newValue.completedProperty().bind(editComplete.selectedProperty());
				newValue.urgentProperty().bind(editUrgent.selectedProperty());
				newValue.nameProperty().bind(editName.textProperty());
				newValue.descriptionProperty().bind(editDescription.textProperty());
			}
		});

		editFlush.setOnAction(event -> {
			Task task = selectedTask.get();
			if (task != null) {
				task.setName(editName.getText());
				task.setDescription(editDescription.getText());
				task.setUrgent(editUrgent.isSelected());
				task.setCompleted(editComplete.isSelected());
			}
			try {
				task.flush();
			} catch (FileNotFoundException e) {
				Logging.err("Failed to save the task, \"" + task.getName() + "\" to the file.");
				Logging.err(e);
			}
		});
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
