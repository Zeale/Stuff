package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

import org.alixia.javalibrary.javafx.bindings.BindingTools;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
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

	private @FXML TableColumn<Task, String> nameColumn, descriptionColumn;
	private @FXML TableColumn<Task, Boolean> urgentColumn, completeColumn;

	private ReadOnlyObjectProperty<Task> selectedTask;

	private @FXML void initialize() {

		selectedTask = taskView.getSelectionModel().selectedItemProperty();

		taskView.setRowFactory(param -> new TableRow<Task>() {
			{
				setOnMouseEntered(event -> setTextFill(Color.RED));
				setOnMouseExited(event -> setTextFill(Color.GOLD));
				setOnMouseClicked(event -> {
					if (event.getButton() == MouseButton.PRIMARY) {
						taskView.getSelectionModel().clearSelection();
						if (!isEmpty())
							taskView.getSelectionModel().select(getIndex());
					}
				});
				setTextFill(Color.GOLD);
			}
		});

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
			if (newValue != null) {/* ~PROPERTIES */
				editComplete.setSelected(newValue.isCompleted());
				editUrgent.setSelected(newValue.isUrgent());
				editName.setText(newValue.getName());
				editDescription.setText(newValue.getDescription());
			}
			if (!editSync1.isSelected())
				return;
			if (oldValue != null) {/* ~PROPERTIES */
				oldValue.completedProperty().unbindBidirectional(editComplete.selectedProperty());
				oldValue.urgentProperty().unbindBidirectional(editUrgent.selectedProperty());
				oldValue.nameProperty().unbindBidirectional(editName.textProperty());
				oldValue.descriptionProperty().unbindBidirectional(editDescription.textProperty());
			}
			if (newValue != null) {/* ~PROPERTIES */
				newValue.completedProperty().bindBidirectional(editComplete.selectedProperty());
				newValue.urgentProperty().bindBidirectional(editUrgent.selectedProperty());
				newValue.nameProperty().bindBidirectional(editName.textProperty());
				newValue.descriptionProperty().bindBidirectional(editDescription.textProperty());
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
					Logging.err("Failed to save the task, \"" + task.getName() + "\" to the file:"
							+ selectedTask.get().getData().getAbsolutePath());
					Logging.err(e);
				}
			}
		});

		ChangeListener<Boolean> listener = (ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (editSync1.isSelected() && !newValue && selectedTask.get() != null) {
				try {
					selectedTask.get().flush();
				} catch (FileNotFoundException e) {
					Logging.err("Failed to save the task, \"" + selectedTask.get().getName() + "\" to the file: "
							+ selectedTask.get().getData().getAbsolutePath());
					Logging.err(e);
				}
			}
		};
		/* ~PROPERTIES */
		editName.focusedProperty().addListener(listener);
		editDescription.focusedProperty().addListener(listener);
		editUrgent.focusedProperty().addListener(listener);
		editComplete.focusedProperty().addListener(listener);

		/* ~PROPERTIES */
		nameColumn.setCellValueFactory(t -> t.getValue().nameProperty());
		descriptionColumn.setCellValueFactory(t -> t.getValue().descriptionProperty());
		urgentColumn.setCellValueFactory(t -> t.getValue().urgentProperty());
		completeColumn.setCellValueFactory(t -> t.getValue().completedProperty());

		/* ~PROPERTIES */
		nameColumn.setCellFactory(__ -> new BasicCell<>());
		descriptionColumn.setCellFactory(__ -> new BasicCell<>());
		urgentColumn.setCellFactory(__ -> new BooleanCheckBoxCell(a -> a.urgentProperty()));
		completeColumn.setCellFactory(__ -> new BooleanCheckBoxCell(a -> a.completedProperty()));

		taskView.setItems(TASK_LIST.get());

	}

	@SuppressWarnings("rawtypes")
	private static void prepareCell(TableCell<?, ?> cell) {
		cell.tableRowProperty().addListener((ChangeListener<TableRow>) (observable, oldValue, newValue) -> {
			if (oldValue != null) {
				cell.textFillProperty().unbind();
				cell.fontProperty().unbind();
			}
			if (newValue != null) {
				BindingTools.bind(newValue.selectedProperty(),
						a -> Font.font(null, a == null || !a ? null : FontWeight.BOLD, -1), cell.fontProperty());
				cell.textFillProperty().bind(newValue.textFillProperty());
			}
		});
		cell.setAlignment(Pos.CENTER);
		cell.setTextAlignment(TextAlignment.CENTER);
	}

	private static class BasicCheckboxCell<T> extends BasicCell<T> {

		protected final CheckBox checkBox = new CheckBox();
		protected final Function<T, Boolean> converter;

		public BasicCheckboxCell(Function<T, Boolean> converter) {
			this.converter = converter;
		}

		protected void update(T item) {
			checkBox.setSelected(converter.apply(item));
		}

		protected void emptied() {
			setGraphic(null);
		}

		protected void populated() {
			setGraphic(checkBox);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static class BooleanCheckBoxCell extends BasicCheckboxCell<Boolean> {

		protected final Function<Task, Property<Boolean>> propertyRetriever;

		public BooleanCheckBoxCell(Function<Task, Property<Boolean>> propertyRetriever) {
			super(null);
			this.propertyRetriever = propertyRetriever;
		}

		{

			tableRowProperty().addListener(new ChangeListener<TableRow>() {

				ChangeListener<Object> taskListener = new ChangeListener<Object>() {
					@Override
					public void changed(ObservableValue<? extends Object> observable1, Object oldValue1,
							Object newValue1) {
						if (oldValue1 != null)
							checkBox.selectedProperty().unbindBidirectional(propertyRetriever.apply((Task) oldValue1));
						if (newValue1 != null)
							checkBox.selectedProperty().bindBidirectional(propertyRetriever.apply((Task) newValue1));
					}
				};

				@Override
				public void changed(ObservableValue<? extends TableRow> observable, TableRow oldValue,
						TableRow newValue) {
					if (oldValue != null)
						oldValue.itemProperty().removeListener(taskListener);
					if (newValue != null) {
						newValue.itemProperty().addListener(taskListener);
						taskListener.changed(newValue.itemProperty(), oldValue == null ? null : oldValue.getItem(),
								newValue.getItem());
					}
				}
			});

		}

		protected void update(Boolean item) {
		}
	}

	private static class BasicCell<T> extends TableCell<Task, T> {

		{
			prepareCell(this);
		}

		@Override
		protected void updateItem(T item, boolean empty) {
			if (empty && isEmpty() || item == getItem())
				return;
			if (item != null && item.equals(getItem()))
				return;
			if (!isEmpty() && getItem() != null && (empty || item == null))
				emptied();
			else {
				if (!(empty || item == null)) {
					if (isEmpty() || getItem() == null)
						populated();
					update(item);
				}
			}
			super.updateItem(item, empty);
		}

		/**
		 * Called when an {@link #updateItem(Object, boolean)} call changed this cell
		 * from being empty to having a value. Being "empty" is defined as this cell
		 * having a {@link #getItem() value} of <code>null</code>, or as this cell being
		 * {@link #isEmpty() empty}. This method is actually called before
		 * {@link #update(Object)} is.
		 */
		protected void populated() {

		}

		protected void emptied() {
			setText(null);
			setGraphic(null);
		}

		protected void update(T item) {
			setText(text(item));
		}

		protected String text(T item) {
			return item.toString();
		}

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
