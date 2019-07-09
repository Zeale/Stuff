package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import org.alixia.javalibrary.javafx.bindings.ListListener;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import zeale.apps.stuff.utilities.java.references.PhoenixReference;

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

	{
		selectedLabel.addListener((ChangeListener<LabelView>) (observable, oldValue, newValue) -> {
			if (oldValue != null)
				oldValue.deselect();
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
				newValue.select();
			}
		});
	}

	private static boolean idTaken(String id) {
		for (Label l : LABEL_LIST.get())
			if (l.getId().equals(id))
				return true;
		return false;
	}

	private static final Label createNewLabel() throws NameNotFoundException, FileNotFoundException {
		String uuid = TaskSchedulerWindow.findFeasibleName(LabelManagerWindow::idTaken);
		Label label = new Label(TaskSchedulerWindow.findFeasibleFile(LABEL_DATA_DIR.get(), ".lbl"), uuid);

		InvalidationListener invalidationListener = __ -> markDirty(label);

		/* ~LABEL.PROPERTIES */
		label.colorProperty().addListener(invalidationListener);
		label.nameProperty().addListener(invalidationListener);
		label.descriptionProperty().addListener(invalidationListener);
		label.opacityProperty().addListener(invalidationListener);

		label.flush();
		return label;
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

	private final static PhoenixReference<File> LABEL_DATA_DIR = PhoenixReference
			.create((Supplier<File>) () -> new File(TaskSchedulerWindow.TASK_SCHEDULER_DATA_DIR.get(), "Labels"));

	final static PhoenixReference<List<Label>> DIRTY_LABELS = new PhoenixReference<List<Label>>() {

		@Override
		protected List<Label> generate() {
			return new ArrayList<Label>() {

				/**
				 * SUID
				 */
				private static final long serialVersionUID = 1L;

				protected void finalize() {
					for (Label l : this) {
						try {
							l.flush();
						} catch (FileNotFoundException e) {
							Logging.err(
									"Failed to write the label, \"" + l.getName() + "\" to its file:" + l.getData());
						}
					}
				}
			};
		}
	};

	final static PhoenixReference<ObservableList<Label>> LABEL_LIST = new PhoenixReference<ObservableList<Label>>() {

		@Override
		protected ObservableList<Label> generate() {
			LABEL_DATA_DIR.get().mkdirs();
			ObservableList<Label> list = FXCollections.observableArrayList();
			File[] files = LABEL_DATA_DIR.get().listFiles();
			if (files == null) {
				Logging.err("Failed to load the Labels from the disk; the label storage directory is not a directory: "
						+ LABEL_DATA_DIR.get().getAbsolutePath());
			} else
				for (File f : files) {
					Label lbl;
					try {
						lbl = Label.load(f);
					} catch (Exception e) {
						Logging.err("Failed to load a Label from the file: " + f.getAbsolutePath());
						continue;
					}
					InvalidationListener dirtyMarker = __ -> markDirty(lbl);

					/* ~LABEL.PROPERTIES */
					lbl.colorProperty().addListener(dirtyMarker);
					lbl.nameProperty().addListener(dirtyMarker);
					lbl.opacityProperty().addListener(dirtyMarker);
					lbl.descriptionProperty().addListener(dirtyMarker);

					list.add(lbl);
				}
			return list;
		}
	};

	private static void markDirty(Label label) {
		if (!DIRTY_LABELS.get().contains(label))
			DIRTY_LABELS.get().add(label);
	}

	private final LabelView getView(Label label) {
		if (views.containsKey(label))
			return views.get(label);
		LabelView view = new LabelView(label);
		views.put(label, view);
		MenuItem item = new MenuItem();
		ContextMenu rightClickMenu = new ContextMenu(item);

		view.setContextMenu(rightClickMenu);

		item.setText("Delete");
		item.setOnAction(e -> {
			for (Task t : TaskSchedulerWindow.TASK_LIST.get())
				t.getLabels().remove(label);
			if (LABEL_LIST.exists())
				LABEL_LIST.get().remove(label);
			label.deleteFile();
		});
		view.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				selectedLabel.set(selectedLabel.get() == view ? null : view);
				event.consume();
			}
		});

		return view;
	}

	private @FXML void initialize() {
		LabelManagerWindow.LABEL_LIST.get().addListener(new ListListener<Label>() {

			@Override
			public void added(List<? extends Label> items, int startpos) {
				for (Label l : items)
					if (l.getName().toLowerCase().contains(labelSearch.getText().toLowerCase()))
						labelView.getChildren().add(getView(l));
			}

			@Override
			public void removed(List<? extends Label> items, int startpos) {
				NEXT_ITEM: for (Label l : items)
					if (l.getName().toLowerCase().contains(labelSearch.getText().toLowerCase()))
						for (Node lv : labelView.getChildren())
							if (lv instanceof LabelView && ((LabelView) lv).getLabel() == l) {
								labelView.getChildren().remove(lv);
								continue NEXT_ITEM;
							}
			}
		});
		Collection<Label> labelList = LABEL_LIST.get();
		labelSearch.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (newValue.isEmpty()) {
				labelView.getChildren().clear();
				for (Label l1 : labelList)
					labelView.getChildren().add(getView(l1));
			} else if (newValue.contains(oldValue))
				for (Iterator<Node> iterator = labelView.getChildren().iterator(); iterator.hasNext();) {
					Node n1 = iterator.next();
					if (n1 instanceof LabelView)
						if (!fits(((LabelView) n1).getLabel().getName(), newValue))
							iterator.remove();
				}
			else
				NEXT_LABEL: for (Label l2 : labelList) {
					boolean fits = fits(l2.getName(), newValue);
					if (fits) {
						for (Node n2 : labelView.getChildren())
							if (n2 instanceof LabelView && ((LabelView) n2).getLabel() == l2)
								continue NEXT_LABEL;
						labelView.getChildren().add(getView(l2));
					} else
						for (Node n3 : labelView.getChildren())
							if (n3 instanceof LabelView && ((LabelView) n3).getLabel() == l2) {
								labelView.getChildren().remove(n3);
								break;
							}
				}
		});
		for(Label l:labelList)
			labelView.getChildren().add(getView(l));
		split = splitPaneWrapper.getDividers().get(0);
	}

	private static boolean fits(String name, String query) {
		return name.toLowerCase().contains(query.toLowerCase());
	}

	private @FXML void createLabel() {
		if (createName.getText().isEmpty()) {
			Logging.err("Cannot create a label with an empty name.");
			return;
		}

		try {
			Label lbl = createNewLabel();
			lbl.setColor(createColor.getValue());
			lbl.setDescription(createDesc.getText());
			lbl.setName(createName.getText());
			LABEL_LIST.get().add(lbl);
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
		if (DIRTY_LABELS.exists())
			for (Label l : DIRTY_LABELS.get())
				try {
					l.flush();
				} catch (FileNotFoundException e) {
					Logging.err("Failed to write the label, \"" + l.getName() + "\" to its file:" + l.getData());
				}
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
