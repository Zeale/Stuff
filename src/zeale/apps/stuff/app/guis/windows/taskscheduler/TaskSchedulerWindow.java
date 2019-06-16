package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Supplier;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class TaskSchedulerWindow extends Window {

	private final static PhoenixReference<File> TASK_DATA_DIR = PhoenixReference
			.create((Supplier<File>) () -> new File(Stuff.INSTALLATION_DIRECTORY, "App Data/Task Scheduler/Tasks"));

	private final static PhoenixReference<ObservableList<Task>> TASK_LIST = new PhoenixReference<ObservableList<Task>>() {

		@Override
		protected ObservableList<Task> generate() {
			TASK_DATA_DIR.get().mkdirs();
			ObservableList<Task> list = FXCollections.observableArrayList();
			File[] listFiles = TASK_DATA_DIR.get().listFiles();
			if (listFiles == null)
				Logging.err("Failed to load the Tasks from the disk; the task storage directory is not a directory: "
						+ TASK_DATA_DIR.get().getAbsolutePath());
			else
				for (File f : listFiles) {
					try {
						list.add(Task.load(f));
					} catch (Exception e) {
						Logging.err("Failed to load a Task from the file: " + f.getAbsolutePath());
					}
				}
			return list;
		}
	};

	private @FXML TextField createName, editName;
	private @FXML TextArea createDescription, editDescription;
	private @FXML CheckBox createComplete, editComplete, createUrgent, editUrgent, editSync1, editSync2;
	private @FXML Button editFlush;

	private @FXML TableView<Task> taskView;

	private ReadOnlyObjectProperty<Task> selectedTask;

	private @FXML void initialize() {
		selectedTask = taskView.getSelectionModel().selectedItemProperty();
		editSync1.selectedProperty().bindBidirectional(editSync2.selectedProperty());
		editSync1.selectedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (newValue) {
				AnchorPane.setBottomAnchor(editDescription, 50d);
				editFlush.setVisible(false);
				Task task = selectedTask.get();
				if (task != null) {/* ~PROPERTIES */
					task.completedProperty().bind(editComplete.selectedProperty());
					task.urgentProperty().bind(editUrgent.selectedProperty());
					task.nameProperty().bind(editName.textProperty());
					task.descriptionProperty().bind(editDescription.textProperty());
				}
			} else {
				AnchorPane.setBottomAnchor(editDescription, 200d);
				editFlush.setVisible(true);
				Task task = selectedTask.get();
				if (task != null) {/* ~PROPERTIES */
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
			if (oldValue != null) {/* ~PROPERTIES */
				oldValue.completedProperty().unbind();
				oldValue.urgentProperty().unbind();
				oldValue.nameProperty().unbind();
				oldValue.descriptionProperty().unbind();
			}
			if (newValue != null) {/* ~PROPERTIES */
				newValue.completedProperty().bind(editComplete.selectedProperty());
				newValue.urgentProperty().bind(editUrgent.selectedProperty());
				newValue.nameProperty().bind(editName.textProperty());
				newValue.descriptionProperty().bind(editDescription.textProperty());
			}
		});

		editFlush.setOnAction(event -> {
			Task task = selectedTask.get();
			if (task != null) {/* ~PROPERTIES */
				task.setName(editName.getText());
				task.setDescription(editDescription.getText());
				task.setUrgent(editUrgent.isSelected());
				task.setCompleted(editComplete.isSelected());
				try {
					task.flush();
				} catch (FileNotFoundException e) {
					Logging.err("Failed to save the task, \"" + task.getName() + "\" to the file.");
					Logging.err(e);
				}
			}
		});

		ChangeListener<Boolean> listener = (ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (editSync1.isSelected() && !newValue && selectedTask.get() != null) {
				try {
					selectedTask.get().flush();
				} catch (FileNotFoundException e) {
					Logging.err("Failed to save the task, \"" + selectedTask.get().getName() + "\" to the file.");
					Logging.err(e);
				}
			}
		};
		/* ~PROPERTIES */
		editName.focusedProperty().addListener(listener);
		editDescription.focusedProperty().addListener(listener);
		editUrgent.focusedProperty().addListener(listener);
		editComplete.focusedProperty().addListener(listener);

		taskView.setItems(TASK_LIST.get());

	}

	private @FXML void reload() {
		TASK_LIST.regenerate();
	}

	private @FXML void update() {
		if (TASK_LIST.exists())
			for (Task t : TASK_LIST.get())
				try {
					t.update();
				} catch (FileNotFoundException e) {
					Logging.err(
							"Failed to update the task, \"" + t.getName() + "\" from it's file: " + t.getData() + ".");
					Logging.err(e);
				}
	}

	private @FXML void flush() {
		if (TASK_LIST.exists())
			for (Task t : TASK_LIST.get())
				try {
					t.flush();
				} catch (FileNotFoundException e) {
					Logging.err("Failed to write the task, \"" + t.getName() + "\" to its file:" + t.getData());
				}
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
