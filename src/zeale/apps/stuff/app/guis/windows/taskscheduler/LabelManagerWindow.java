package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.app.guis.windows.taskscheduler.TaskSchedulerWindow.NameNotFoundException;
import zeale.apps.tools.console.std.BindingConversion;

class LabelManagerWindow extends Window {

	private ObjectProperty<LabelView> selectedLabel = new SimpleObjectProperty<>();

	private @FXML SplitPane splitPaneWrapper;
	private Divider split;
	private @FXML TabPane manipulationPane;
	private @FXML Tab createTab, modifyTab;
	private @FXML TilePane labelView;
	private @FXML TextField labelSearch, createName, modName, modID;
	private @FXML ColorPicker createColor, modColor;
	private @FXML TextArea createDesc, modDesc;

	private static final Effect SELECTED_EFFECT = new Glow(1);

	{
		selectedLabel.addListener((ChangeListener<LabelView>) (observable, oldValue, newValue) -> {
			if (oldValue != null)
				oldValue.setEffect(null);
			if (newValue == null) {
				modName.setText("");
				modID.setText("");
				modColor.setValue(Color.WHITE);
				modDesc.setText("");
			} else {
				modName.setText(newValue.getLabel().getName());
				modID.setText(newValue.getLabel().getId());
				modColor.setValue(newValue.getLabel().getColor());
				modDesc.setText(newValue.getLabel().getDescription());
				newValue.setEffect(SELECTED_EFFECT);
			}
		});
	}

	private final LabelView getView(Label label) {
		LabelView view = new LabelView(label);
		view.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				selectedLabel.set(selectedLabel.get() == view ? null : view);
				event.consume();
			}
		});

		return view;
	}

	private @FXML void initialize() {
		BindingConversion.bind(TaskSchedulerWindow.LABEL_LIST.get(), this::getView, labelView.getChildren());
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
