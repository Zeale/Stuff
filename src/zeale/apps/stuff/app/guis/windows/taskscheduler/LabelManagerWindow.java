package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
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
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.app.guis.windows.HomeWindow;
import zeale.apps.stuff.app.guis.windows.taskscheduler.TaskSchedulerWindow.NameNotFoundException;
import zeale.apps.tools.console.std.BindingConversion;

class LabelManagerWindow extends Window {

	private final ObjectProperty<LabelView> selectedLabel = new SimpleObjectProperty<>();

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

	private @FXML void goHome() {
		try {
			Stuff.displayWindow(new HomeWindow());
		} catch (WindowLoadFailureException e) {
			Logging.err("Failed to show the home window.");
			Logging.err(e);
		}
	}

	private @FXML void goTasks() {
		try {
			Stuff.displayWindow(new TaskSchedulerWindow());
		} catch (WindowLoadFailureException e) {
			Logging.err("Failed to show the Todo List window.");
			Logging.err(e);
		}
	}

	private final Map<Label, LabelView> views = new WeakHashMap<>();

	private final LabelView getView(Label label) {
		if (views.containsKey(label))
			return views.get(label);
		LabelView view = new LabelView(label);
		views.put(label, view);
		MenuItem item = new MenuItem();
		ContextMenu rightClickMenu = new ContextMenu(item);
		item.setText("Delete");
		item.setOnAction(e -> {
			for (Task t : TaskSchedulerWindow.TASK_LIST.get())
				t.getLabels().remove(label);
			if (TaskSchedulerWindow.LABEL_LIST.exists())
				TaskSchedulerWindow.LABEL_LIST.get().remove(label);
			label.deleteFile();
		});
		view.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				selectedLabel.set(selectedLabel.get() == view ? null : view);
				event.consume();
			} else if (event.getButton() == MouseButton.SECONDARY) {
				rightClickMenu.show(labelView, event.getScreenX(), event.getScreenY());
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
		if (selectedLabel.get() == null)
			Logging.err("Please select a label to modify.");
		else if (modName.getText().equals(""))
			Logging.err("The label name cannot be empty");
		else {
			selectedLabel.get().getLabel().setName(modName.getText());
			selectedLabel.get().getLabel().setColor(modColor.getValue());
			selectedLabel.get().getLabel().setDescription(modDesc.getText());
		}
	}

	@Override
	public void destroy() {
		TaskSchedulerWindow.save();
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
