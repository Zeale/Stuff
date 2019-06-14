package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.tools.api.data.files.filesystem.storage.FileStorage;

public class TaskSchedulerWindow extends Window {

	private final static FileStorage TASK_DATA_DIR = new FileStorage(
			new File(new File(Stuff.INSTALLATION_DIRECTORY, "App Data"), "Task Scheduler")).createChild("Tasks");

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(TaskSchedulerWindow.class.getResource("TaskSchedulerMainGUI.fxml"));
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			Logging.err("Failed to load the Task Scheduler window's main GUI.");
			Logging.err(e);
		}
	}

}
