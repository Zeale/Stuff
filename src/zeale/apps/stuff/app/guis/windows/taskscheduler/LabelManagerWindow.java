package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.FileNotFoundException;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.app.guis.windows.taskscheduler.TaskSchedulerWindow.NameNotFoundException;
import zeale.apps.tools.console.std.BindingConversion;

public class LabelManagerWindow extends Window {

	private @FXML TilePane labelView;
	private @FXML TextField labelSearch, createName, modName, createID, modID;
	private @FXML ColorPicker createColor, modColor;
	private @FXML TextArea createDesc, modDesc;

	private @FXML void initialize() {
		BindingConversion.bind(TaskSchedulerWindow.LABEL_LIST.get(), LabelView::new, labelView.getChildren());
	}

	private @FXML void createLabel() {
		if (createName.getText().isEmpty()) {
			Logging.err("Cannot create a label with an empty name.");
			return;
		}

		try {
			Label lbl = TaskSchedulerWindow.createLabel();
			lbl.setColor(createColor.getValue());
			lbl.setDescription(createDesc.getText());
			TaskSchedulerWindow.LABEL_LIST.get().add(lbl);
			lbl.flush();
		} catch (FileNotFoundException e) {
			Logging.err("Failed to create the Label. An unused file to save the Label to, could not be found.");
		} catch (NameNotFoundException e) {
			Logging.err("Failed to create the Label. An unused unique ID could not be found.");
		}
	}

	private @FXML void modifyLabel() {

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		// TODO Auto-generated method stub

	}

}
