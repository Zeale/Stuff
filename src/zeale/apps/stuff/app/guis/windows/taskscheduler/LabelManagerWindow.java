package zeale.apps.stuff.app.guis.windows.taskscheduler;

import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.tools.console.std.BindingConversion;

public class LabelManagerWindow extends Window {

	private @FXML TilePane labelView;
	private @FXML TextField labelSearch, createName, modName, createID, modID;
	private @FXML ColorPicker createColor, modColor;
	private @FXML TextArea createDesc, modDesc;
	
	private @FXML void initialize() {
		BindingConversion.bind(TaskSchedulerWindow.LABEL_LIST.get(), LabelView::new, labelView.getChildren());
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
