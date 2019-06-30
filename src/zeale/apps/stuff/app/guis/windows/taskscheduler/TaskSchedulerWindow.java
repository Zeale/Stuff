package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import org.alixia.javalibrary.javafx.bindings.BindingTools;
import org.alixia.javalibrary.javafx.bindings.BindingTools.FilterBinding;
import org.alixia.javalibrary.javafx.bindings.BindingTools.PipewayBinding;
import org.alixia.javalibrary.util.Box;
import org.alixia.javalibrary.util.Gateway;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import zeale.applicationss.notesss.utilities.Utilities;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.files.data.Datapiece;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.app.guis.windows.HomeWindow;
import zeale.apps.stuff.utilities.java.references.PhoenixReference;

public class TaskSchedulerWindow extends Window {

	private final static PhoenixReference<File> TASK_SCHEDULER_DATA_DIR = PhoenixReference
			.create((Supplier<File>) () -> new File(Stuff.INSTALLATION_DIRECTORY, "App Data/Task Scheduler/")),
			TASK_DATA_DIR = PhoenixReference
					.create((Supplier<File>) () -> new File(TASK_SCHEDULER_DATA_DIR.get(), "Tasks")),
			LABEL_DATA_DIR = PhoenixReference
					.create((Supplier<File>) () -> new File(TASK_SCHEDULER_DATA_DIR.get(), "Labels"));

	private final static class FlushList<E extends Datapiece> extends ArrayList<E> {
		/**
		 * SUID
		 */
		private static final long serialVersionUID = 1L;
		private final Function<? super E, String> msgGetter;

		public FlushList(Function<? super E, String> msgGetter) {
			this.msgGetter = msgGetter;
		}

		@Override
		protected void finalize() throws Throwable {
			Throwable exception = null;
			try {
				super.finalize();
			} catch (Throwable e) {
				exception = e;
			} finally {
				try {
					flush();
				} catch (Throwable e) {
					Logging.err("(To Do List) There was an error while saving data to the file...");
					Logging.err(e);
				}
				if (exception != null)
					throw exception;
			}
		}

		public synchronized void flushList() {
			flush();
			clear();
		}

		private void flush() {
			for (E e : this)
				try {
					e.flush();
				} catch (FileNotFoundException err) {
					Logging.err(msgGetter.apply(e));
				}
		}

	}

	private static final PhoenixReference<FlushList<Datapiece>> DIRTY_OBJECTS = new PhoenixReference<FlushList<Datapiece>>() {

		@Override
		protected FlushList<Datapiece> generate() {
			return new FlushList<>(a -> {
				return a instanceof Task
						? "Failed to write the Task, \"" + ((Task) a).getName() + "\" to its file:" + a.getData()
						: a instanceof Label
								? "Failed to write the label, \"" + ((Task) a).getName() + "\" to its file:"
										+ a.getData()
								: "Failed to write an object to the file: " + a.getData();
			});
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
					InvalidationListener dirtyMarker = __ -> {
						if (!DIRTY_OBJECTS.get().contains(lbl))
							DIRTY_OBJECTS.get().add(lbl);
					};

					/* ~LABEL.PROPERTIES */
					lbl.colorProperty().addListener(dirtyMarker);
					lbl.nameProperty().addListener(dirtyMarker);

					list.add(lbl);
				}
			return list;
		}
	};

	private final static PhoenixReference<ObservableList<Task>> TASK_LIST = new PhoenixReference<ObservableList<Task>>(
			true) {

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
					Task task;
					try {
						task = Task.load(f, LABEL_OBTAINER.get());
					} catch (Exception e) {
						Logging.err("Failed to load a Task from the file: " + f.getAbsolutePath());
						continue;
					}
					InvalidationListener invalidationListener = __ -> {
						if (!DIRTY_OBJECTS.get().contains(task))
							DIRTY_OBJECTS.get().add(task);
					};

					/* ~PROPERTIES */
					task.completedProperty().addListener(invalidationListener);
					task.urgentProperty().addListener(invalidationListener);
					task.nameProperty().addListener(invalidationListener);
					task.descriptionProperty().addListener(invalidationListener);
					task.dueDateProperty().addListener(invalidationListener);
					task.getLabels().addListener(invalidationListener);

					list.add(task);
				}
			return list;
		}
	};

	private static final PhoenixReference<Function<String, Label>> LABEL_OBTAINER = new PhoenixReference<Function<String, Label>>() {

		@Override
		protected Function<String, Label> generate() {
			return t -> {
				Label label = getLabel(t);
				if (label == null)
					try {
						label = new Label(findFeasibleFile(LABEL_DATA_DIR.get(), ".lbl"), t);
					} catch (FileNotFoundException e) {
						Logging.err("Failed to find a feasible file for a Label.");
						Logging.err(e);
						throw new RuntimeException("Failed to create a file for a label with the ID, \"" + t + "\".",
								e);
					}
				return label;
			};
		}
	};

	static final Label createLabel() throws NameNotFoundException, FileNotFoundException {
		String uuid = findFeasibleName(s -> getLabel(s) != null);
		Label label = new Label(findFeasibleFile(LABEL_DATA_DIR.get(), ".lbl"), uuid);
		label.flush();
		return label;
	}

	static final Label getLabel(String id) {
		if (LABEL_LIST.exists())
			for (Label l : LABEL_LIST.get()) {
				if (l.getId().equals(id))
					return l;
			}
		return null;
	}

	private static final Border SELECTED_ROW_DEFAULT_BORDER = Utilities.getBorderFromColor(Color.GOLD, 1),
			SELECTED_ROW_HOVER_BORDER = Utilities.getBorderFromColor(Color.RED, 1);

	private @FXML TextField createName, editName;
	private @FXML TextArea createDescription, editDescription;
	private @FXML DatePicker createDueDate, editDueDate;
	private @FXML CheckBox createComplete, editComplete, createUrgent, editUrgent, editSync1, editSync2, editSync3;
	private @FXML Button editFlush;

	private @FXML TableView<Task> taskView;

	private @FXML TableColumn<Task, String> nameColumn, descriptionColumn;
	private @FXML TableColumn<Task, Boolean> urgentColumn, completeColumn;
	private @FXML TableColumn<Task, LocalDate> dueDateColumn;

	private @FXML Tab viewTasksTab;
	private @FXML TabPane layoutTabPane;

	private @FXML CheckMenuItem filterComplete, filterUrgent;

	private ReadOnlyObjectProperty<Task> selectedTask;

	private @FXML void goHome(ActionEvent event) {
		try {
			Stuff.displayWindow(new HomeWindow());
		} catch (WindowLoadFailureException e) {
			Logging.err("Encountered an error while attempting to display the home window.");
			Logging.err(e);
		}
	}

	private static final Gateway<Instant, LocalDate> INSTANT_TO_LOCALDATE_GATEWAY = new Gateway<Instant, LocalDate>() {

		@Override
		public LocalDate to(Instant value) {
			return value == null ? null : value.atZone(ZoneId.systemDefault()).toLocalDate();
		}

		@Override
		public Instant from(LocalDate value) {
			return value == null ? null : value.atStartOfDay(ZoneId.systemDefault()).toInstant();
		}
	};

	private @FXML Button test;

	private @FXML void initialize() {
		selectedTask = taskView.getSelectionModel().selectedItemProperty();

		taskView.setRowFactory(param -> new TableRow<Task>() {
			{
				hoverProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
					if (isSelected())
						setBorder(newValue ? SELECTED_ROW_HOVER_BORDER : SELECTED_ROW_DEFAULT_BORDER);
				});

				final Insets borderCounteringInsets = new Insets(0, -1, 0, -1);
				selectedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
					if (newValue) {
						setBorder(isHover() ? SELECTED_ROW_HOVER_BORDER : SELECTED_ROW_DEFAULT_BORDER);
						setPadding(borderCounteringInsets);
					} else {
						setBorder(null);
						setPadding(Insets.EMPTY);
					}
				});

				ContextMenu rightClickMenu = new ContextMenu();
				MenuItem item = new MenuItem();
				item.setText("Delete");
				item.setOnAction(event -> {
					Task tsk = getItem();
					if (tsk == null)
						return;

					tsk.getData().delete();
					TASK_LIST.get().remove(tsk);
				});
				rightClickMenu.getItems().add(item);

				setOnMouseEntered(event -> setTextFill(Color.RED));
				setOnMouseExited(event -> setTextFill(Color.GOLD));
				setOnMouseClicked(event -> {
					if ((isEmpty() || getItem() == null)) {
						if (event.getButton() == MouseButton.PRIMARY)
							taskView.getSelectionModel().clearSelection();
					} else if (event.getButton() == MouseButton.SECONDARY) {
						rightClickMenu.show(this, event.getScreenX(), event.getScreenY());
						event.consume();
					}
				});
				setTextFill(Color.GOLD);
			}
		});

		final Box<PipewayBinding<?, ?, ?, ?>> binding = new Box<>();

		editSync1.selectedProperty().bindBidirectional(editSync2.selectedProperty());
		editSync1.selectedProperty().bindBidirectional(editSync3.selectedProperty());
		editSync1.selectedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (newValue) {
				AnchorPane.setBottomAnchor(editDescription, 50d);
				editFlush.setVisible(false);
				Task task = selectedTask.get();
				if (task != null) {/* ~PROPERTIES */
					task.completedProperty().bindBidirectional(editComplete.selectedProperty());
					task.urgentProperty().bindBidirectional(editUrgent.selectedProperty());
					task.nameProperty().bindBidirectional(editName.textProperty());
					task.descriptionProperty().bindBidirectional(editDescription.textProperty());
					binding.value = new PipewayBinding<>(task.dueDateProperty(), editDueDate.valueProperty(),
							INSTANT_TO_LOCALDATE_GATEWAY, Logging::err);
					// TODO Label setup.
				}
			} else {
				AnchorPane.setBottomAnchor(editDescription, 200d);
				editFlush.setVisible(true);
				Task task = selectedTask.get();
				if (task != null) {/* ~PROPERTIES */
					task.completedProperty().unbindBidirectional(editComplete.selectedProperty());
					task.urgentProperty().unbindBidirectional(editUrgent.selectedProperty());
					task.nameProperty().unbindBidirectional(editName.textProperty());
					task.descriptionProperty().unbindBidirectional(editDescription.textProperty());
					if (binding.value != null)
						binding.value.unbind();
					// TODO Label setup.
				}
			}
		});

		selectedTask.addListener((ChangeListener<Task>) (observable, oldValue, newValue) -> {
			if (newValue != null) {/* ~PROPERTIES */
				editComplete.setSelected(newValue.isCompleted());
				editUrgent.setSelected(newValue.isUrgent());
				editName.setText(newValue.getName());
				editDescription.setText(newValue.getDescription());
				editDueDate.setValue(INSTANT_TO_LOCALDATE_GATEWAY.to(newValue.getDueDate()));
				// TODO Label setup.
			}
			if (!editSync1.isSelected())
				return;
			if (oldValue != null) {/* ~PROPERTIES */
				oldValue.completedProperty().unbindBidirectional(editComplete.selectedProperty());
				oldValue.urgentProperty().unbindBidirectional(editUrgent.selectedProperty());
				oldValue.nameProperty().unbindBidirectional(editName.textProperty());
				oldValue.descriptionProperty().unbindBidirectional(editDescription.textProperty());
				if (binding.value != null)
					binding.value.unbind();
				// TODO Label setup.
			}
			if (newValue != null) {/* ~PROPERTIES */
				newValue.completedProperty().bindBidirectional(editComplete.selectedProperty());
				newValue.urgentProperty().bindBidirectional(editUrgent.selectedProperty());
				newValue.nameProperty().bindBidirectional(editName.textProperty());
				newValue.descriptionProperty().bindBidirectional(editDescription.textProperty());
				binding.value = new PipewayBinding<>(newValue.dueDateProperty(), editDueDate.valueProperty(),
						INSTANT_TO_LOCALDATE_GATEWAY, Logging::err);
				// TODO Label setup.
			}
		});

		editFlush.setOnAction(event -> {
			Task task = selectedTask.get();
			if (task != null) {/* ~PROPERTIES */
				task.setName(editName.getText());
				task.setDescription(editDescription.getText());
				task.setUrgent(editUrgent.isSelected());
				task.setCompleted(editComplete.isSelected());
				task.setDueDate(INSTANT_TO_LOCALDATE_GATEWAY.from(editDueDate.getValue()));
				// TODO Label setup.
				try {
					task.flush();
					if (DIRTY_OBJECTS.exists())
						DIRTY_OBJECTS.get().remove(task);
				} catch (FileNotFoundException e) {
					Logging.err("Failed to save the task, \"" + task.getName() + "\" to the file:"
							+ selectedTask.get().getData().getAbsolutePath());
					Logging.err(e);
				}
			}
		});

		ChangeListener<Boolean> syncListener = (ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (editSync1.isSelected() && !newValue && selectedTask.get() != null) {
				try {
					selectedTask.get().flush();
					if (DIRTY_OBJECTS.exists())
						DIRTY_OBJECTS.get().remove(selectedTask.get());
				} catch (FileNotFoundException e) {
					Logging.err("Failed to save the task, \"" + selectedTask.get().getName() + "\" to the file: "
							+ selectedTask.get().getData().getAbsolutePath());
					Logging.err(e);
				}
			}
		};
		/* ~PROPERTIES */
		editName.focusedProperty().addListener(syncListener);
		editDescription.focusedProperty().addListener(syncListener);
		editUrgent.focusedProperty().addListener(syncListener);
		editComplete.focusedProperty().addListener(syncListener);
		editDueDate.focusedProperty().addListener(syncListener);
		// TODO Label setup.

		/* ~PROPERTIES */
		nameColumn.setCellValueFactory(t -> t.getValue().nameProperty());
		descriptionColumn.setCellValueFactory(t -> t.getValue().descriptionProperty());
		urgentColumn.setCellValueFactory(t -> t.getValue().urgentProperty());
		completeColumn.setCellValueFactory(t -> t.getValue().completedProperty());
		dueDateColumn.setCellValueFactory(
				t -> BindingTools.mask(t.getValue().dueDateProperty(), INSTANT_TO_LOCALDATE_GATEWAY::to));

		/* ~PROPERTIES */
		nameColumn.setCellFactory(__ -> new BasicCell<>());
		descriptionColumn.setCellFactory(__ -> new BasicCell<>());
		urgentColumn.setCellFactory(__ -> new BooleanCheckBoxCell(a -> a.urgentProperty()));
		completeColumn.setCellFactory(__ -> new BooleanCheckBoxCell(a -> a.completedProperty()));
		dueDateColumn.setCellFactory(__ -> new BasicCell<>());

		ObservableList<Task> taskList = FXCollections.observableArrayList();
		FilterBinding<Task> filteredBinding = BindingTools.filterBind(TASK_LIST.get(), taskList);
		class FilterSelectedListener implements ChangeListener<Boolean> {
			private final Function<? super Task, Boolean> filter;

			public FilterSelectedListener(Function<? super Task, Boolean> filter) {
				this.filter = filter;
			}

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue)
					filteredBinding.addFilter(filter);
				else
					filteredBinding.removeFilter(filter);
			}

		}
		filterComplete.selectedProperty().addListener(new FilterSelectedListener(t -> !t.isCompleted()));
		filterUrgent.selectedProperty().addListener(new FilterSelectedListener(t -> !t.isUrgent()));
		taskView.setItems(taskList);

	}

	static class NameNotFoundException extends Exception {

		/**
		 * SUID
		 */
		private static final long serialVersionUID = 1L;

	}

	private static String findFeasibleName(Function<String, Boolean> existenceChecker) throws NameNotFoundException {
		String uuid = UUID.randomUUID().toString();
		if (existenceChecker.apply(uuid)) {
			int val = 0;
			while (existenceChecker.apply(uuid + "-" + val))
				if (++val == 0)
					throw new NameNotFoundException();
			return uuid + "-" + val;
		}
		return uuid;
	}

	private static final File findFeasibleFile(File location, String extension) throws FileNotFoundException {
		String uuid = UUID.randomUUID().toString();
		File file = new File(TASK_DATA_DIR.get(), uuid);
		if (file.exists()) {
			int val = 0;
			while ((file = new File(location, uuid + "-" + val + extension)).exists())
				if (++val == 0)
					// If the user has 2^32 files in this directory each with the same UUID then
					// wtf.
					throw new FileNotFoundException();
		}
		return file;
	}

	private @FXML void createNewTab() {
		Instant instant;
		try {
			instant = INSTANT_TO_LOCALDATE_GATEWAY.from(createDueDate.getValue());
		} catch (Exception e) {
			Logging.err("Could not convert " + createDueDate.getValue() + " to a time stamp.");
			return;
		}
		File file;
		try {
			file = findFeasibleFile(TASK_DATA_DIR.get(), ".tsk");
		} catch (FileNotFoundException e1) {
			Logging.err("Failed to find a feasible file for the Task, \"" + createName.getText() + "\".");
			return;
		}
		Task task = new Task(file, LABEL_OBTAINER.get());
		task.setCompleted(createComplete.isSelected());
		task.setUrgent(createUrgent.isSelected());
		task.setDescription(createDescription.getText());
		task.setName(createName.getText());
		task.setDueDate(instant);

		createComplete.setSelected(false);
		createUrgent.setSelected(false);
		createDescription.setText(null);
		createName.setText(null);
		createDueDate.setValue(null);

		try {
			task.flush();
			TASK_LIST.get().add(task);
			layoutTabPane.getSelectionModel().select(viewTasksTab);
		} catch (FileNotFoundException e) {
			Logging.err("Failed to write the task: \"" + task.getName() + "\" to its file: " + file.getAbsolutePath());
			Logging.err(e);
		}

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
		LABEL_LIST.regenerate();
		if (DIRTY_OBJECTS.exists())
			synchronized (DIRTY_OBJECTS.get()) {
				DIRTY_OBJECTS.get().clear();
			}
	}

	private @FXML void update() {

		if (TASK_LIST.exists())
			for (Task t : TASK_LIST.get())
				try {
					t.update();
				} catch (FileNotFoundException e) {
					Logging.err(
							"Failed to update the Task, \"" + t.getName() + "\" from its file: " + t.getData() + ".");
					Logging.err(e);
				}
		if (LABEL_LIST.exists())
			for (Label l : LABEL_LIST.get())
				try {
					l.update();
				} catch (FileNotFoundException e) {
					Logging.err(
							"Failed to update the Label, \"" + l.getName() + "\" from its file: " + l.getData() + ".");
					Logging.err(e);
				}
		if (DIRTY_OBJECTS.exists())
			DIRTY_OBJECTS.get().clear();
	}

	private @FXML void flush() {
		if (TASK_LIST.exists())
			for (Task t : TASK_LIST.get())
				try {
					t.flush();
				} catch (FileNotFoundException e) {
					Logging.err("Failed to write the Task, \"" + t.getName() + "\" to its file:" + t.getData());
				}
		if (LABEL_LIST.exists())
			for (Label l : LABEL_LIST.get())
				try {
					l.flush();
				} catch (FileNotFoundException e) {
					Logging.err("Failed to write the Label, \"" + l.getName() + "\" to its file:" + l.getData());
				}
		if (DIRTY_OBJECTS.exists())
			DIRTY_OBJECTS.get().clear();
	}

	@Override
	public void destroy() {
		if (selectedTask.get() != null && editSync1.isSelected()) {
			try {
				selectedTask.get().flush();
				if (DIRTY_OBJECTS.exists())
					DIRTY_OBJECTS.get().remove(selectedTask.get());
			} catch (FileNotFoundException e) {
				Logging.err("Failed to write the task: \"" + selectedTask.get().getName() + "\" to the file: "
						+ selectedTask.get().getData().getAbsolutePath());
				Logging.err(e);
			}
		}
		if (DIRTY_OBJECTS.exists())
			DIRTY_OBJECTS.get().flushList();
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
