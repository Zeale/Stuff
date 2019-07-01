package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.app.guis.windows.taskscheduler.TaskSchedulerWindow.NameNotFoundException;
import zeale.apps.tools.console.std.BindingConversion;

class LabelManagerWindow extends Window {

	private @FXML SplitPane splitPaneWrapper;
	private Divider split;
	private @FXML TabPane manipulationPane;
	private @FXML Tab createTab, modifyTab;
	private @FXML TilePane labelView;
	private @FXML TextField labelSearch, createName, modName, createID, modID;
	private @FXML ColorPicker createColor, modColor;
	private @FXML TextArea createDesc, modDesc;

	private @FXML void initialize() {
		BindingConversion.bind(TaskSchedulerWindow.LABEL_LIST.get(), LabelView::new, labelView.getChildren());
		split = splitPaneWrapper.getDividers().get(0);
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
			lbl.setName(createName.getText());
			TaskSchedulerWindow.LABEL_LIST.get().add(lbl);
			lbl.flush();
		} catch (FileNotFoundException e) {
			Logging.err("Failed to create the Label. An unused file to save the Label to, could not be found.");
		} catch (NameNotFoundException e) {
			Logging.err("Failed to create the Label. An unused unique ID could not be found.");
		}
	}

	public @FXML void showEditMenu() {
		if (split.getPosition() > 0.95)
			split.setPosition(0.5);
		manipulationPane.getSelectionModel().select(modifyTab);
	}

	public @FXML void showCreateMenu() {
		if (split.getPosition() > 0.95)
			split.setPosition(0.5);
		manipulationPane.getSelectionModel().select(createTab);
	}

	private @FXML void modifyLabel() {

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(LabelManagerWindow.class.getResource("LabelManagerGUI.fxml"));
		loader.setController(this);
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			Logging.err("Failed to load the label manipulation window.");
			Logging.err(e);
		}
	}

}
