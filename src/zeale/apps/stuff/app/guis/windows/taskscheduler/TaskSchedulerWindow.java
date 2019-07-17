package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import org.alixia.javalibrary.javafx.bindings.BindingTools;
import org.alixia.javalibrary.javafx.bindings.BindingTools.FilterBinding;
import org.alixia.javalibrary.javafx.bindings.BindingTools.PipewayBinding;
import org.alixia.javalibrary.javafx.bindings.ListListener;
import org.alixia.javalibrary.util.Box;
import org.alixia.javalibrary.util.Gateway;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import zeale.applicationss.notesss.utilities.Utilities;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.app.guis.windows.HomeWindow;
import zeale.apps.stuff.utilities.java.references.PhoenixReference;

public class TaskSchedulerWindow extends Window {

	final static PhoenixReference<File> TASK_SCHEDULER_DATA_DIR = PhoenixReference
			.create((Supplier<File>) () -> new File(Stuff.APPLICATION_DATA, "Task Scheduler"));

	private final static PhoenixReference<File> TASK_DATA_DIR = PhoenixReference
			.create((Supplier<File>) () -> new File(TASK_SCHEDULER_DATA_DIR.get(), "Tasks"));

	private static final PhoenixReference<List<Task>> DIRTY_TASKS = new PhoenixReference<List<Task>>() {

		@Override
		protected List<Task> generate() {
			return new ArrayList<Task>() {

				/**
				 * SUID
				 */
				private static final long serialVersionUID = 1L;

				protected void finalize() {
					for (Task t : this) {
						try {
							t.flush();
						} catch (FileNotFoundException e) {
							Logging.err("Failed to write the task, \"" + t.getName() + "\" to its file:" + t.getData());
						}
					}
				}
			};
		}
	};

	final static PhoenixReference<ObservableList<Task>> TASK_LIST = new PhoenixReference<ObservableList<Task>>(true) {

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
					Collection<Label> loadedLabels = LabelManagerWindow.LABEL_LIST.get();
					try {
						task = Task.load(f, t -> {
							for (Label l : loadedLabels)
								if (l.getId().equals(t))
									return l;
							return Label.getNullLabel(t);
						});
					} catch (Exception e) {
						Logging.err("Failed to load a Task from the file: " + f.getAbsolutePath());
						continue;
					}
					InvalidationListener invalidationListener = __ -> markDirty(task);

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

	static void markDirty(Task dp) {
		if (!(DIRTY_TASKS.exists() && DIRTY_TASKS.get().contains(dp)))
			DIRTY_TASKS.get().add(dp);
	}

	private static final Border SELECTED_ROW_DEFAULT_BORDER = Utilities.getBorderFromColor(Color.GOLD, 1),
			SELECTED_ROW_HOVER_BORDER = Utilities.getBorderFromColor(Color.RED, 1);

	private @FXML TextField createName, editName;
	private @FXML TextArea createDescription, editDescription;
	private @FXML DatePicker createDueDate, editDueDate;
	private @FXML CheckBox createComplete, editComplete, createUrgent, editUrgent, editSync1, editSync2;
	private @FXML Button editFlush;

	private @FXML CheckMenuItem showLabels;

	private @FXML TableView<Task> taskView;

	private @FXML TableColumn<Task, String> nameColumn, descriptionColumn;
	private @FXML TableColumn<Task, Boolean> urgentColumn, completeColumn;
	private @FXML TableColumn<Task, LocalDate> dueDateColumn;
	private @FXML TableColumn<Task, ObservableList<Label>> labelColumn;

	private @FXML Tab viewTasksTab;
	private @FXML TabPane layoutTabPane;

	private @FXML CheckMenuItem filterComplete, filterUrgent;

	private @FXML FlowPane labelSelectionBox;
	private @FXML TextField labelFilter;

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

	private @FXML void showEditLabelGUI() {
		try {
			LabelManagerWindow window = new LabelManagerWindow();
			Stuff.displayWindow(window);
			window.showEditMenu();
		} catch (WindowLoadFailureException e) {
			Logging.err("Failed to show the label editor window.");
			Logging.err(e);
		}
	}

	private @FXML void showCreateLabelGUI() {
		try {
			LabelManagerWindow window = new LabelManagerWindow();
			Stuff.displayWindow(window);
			window.showCreateMenu();
		} catch (WindowLoadFailureException e) {
			Logging.err("Failed to show the label creator window.");
			Logging.err(e);
		}
	}

	private @FXML void showLabelManagerWindow() {
		try {
			Stuff.displayWindow(new LabelManagerWindow());
		} catch (WindowLoadFailureException e) {
			Logging.err("Failed to show the label manager window.");
			Logging.err(e);
		}
	}

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

				setContextMenu(rightClickMenu);

				setOnMouseEntered(event -> setTextFill(Color.RED));
				setOnMouseExited(event -> setTextFill(Color.GOLD));
				setOnMouseClicked(event -> {
					if ((isEmpty() || getItem() == null) && event.getButton() == MouseButton.PRIMARY)
						taskView.getSelectionModel().clearSelection();
				});
				setTextFill(Color.GOLD);
			}
		});

		final Box<PipewayBinding<?, ?, ?, ?>> binding = new Box<>();

		editSync1.selectedProperty().bindBidirectional(editSync2.selectedProperty());
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
				}
			}
		});

		Box<ListListener<? super Label>> taskLabelListListener = new Box<>();
		selectedTask.addListener((ChangeListener<Task>) (observable, oldValue, newValue) -> {
			// TODO Fix IFs.
			if (oldValue != null)
				oldValue.getLabels().removeListener(taskLabelListListener.value);
			if (newValue != null) {/* ~PROPERTIES */
				editComplete.setSelected(newValue.isCompleted());
				editUrgent.setSelected(newValue.isUrgent());
				editName.setText(newValue.getName());
				editDescription.setText(newValue.getDescription());
				editDueDate.setValue(INSTANT_TO_LOCALDATE_GATEWAY.to(newValue.getDueDate()));

				for (Node n : labelSelectionBox.getChildren())
					if (n instanceof LabelView) {
						LabelView view = (LabelView) n;
						if (newValue.getLabels().contains(view.getLabel()))
							view.select();
						else
							view.deselect();
					}

				newValue.getLabels().addListener(taskLabelListListener.value = new ListListener<Label>() {

					@Override
					public void added(List<? extends Label> items, int startpos) {
						NEXT_LABEL: for (Label l : items)
							for (Node n : labelSelectionBox.getChildren())
								if (n instanceof LabelView) {
									LabelView view = (LabelView) n;
									if (view.getLabel() == l) {
										view.select();
										continue NEXT_LABEL;
									}
								}
					}

					@Override
					public void removed(List<? extends Label> items, int startpos) {
						NEXT_LABEL: for (Label l : items)
							for (Node n : labelSelectionBox.getChildren())
								if (n instanceof LabelView) {
									LabelView view = (LabelView) n;
									if (view.getLabel() == l) {
										view.deselect();
										continue NEXT_LABEL;
									}
								}
					}
				});
			} else
				for (Node n : labelSelectionBox.getChildren())
					if (n instanceof LabelView)
						((LabelView) n).deselect();
			if (!editSync1.isSelected())
				return;
			if (oldValue != null) {
				/* ~PROPERTIES */
				oldValue.completedProperty().unbindBidirectional(editComplete.selectedProperty());
				oldValue.urgentProperty().unbindBidirectional(editUrgent.selectedProperty());
				oldValue.nameProperty().unbindBidirectional(editName.textProperty());
				oldValue.descriptionProperty().unbindBidirectional(editDescription.textProperty());
				if (binding.value != null)
					binding.value.unbind();
			} else if (!editSync1.isSelected())
				return;
			if (newValue != null) {/* ~PROPERTIES */
				newValue.completedProperty().bindBidirectional(editComplete.selectedProperty());
				newValue.urgentProperty().bindBidirectional(editUrgent.selectedProperty());
				newValue.nameProperty().bindBidirectional(editName.textProperty());
				newValue.descriptionProperty().bindBidirectional(editDescription.textProperty());
				binding.value = new PipewayBinding<>(newValue.dueDateProperty(), editDueDate.valueProperty(),
						INSTANT_TO_LOCALDATE_GATEWAY, Logging::err);
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
				try {
					task.flush();
					if (DIRTY_TASKS.exists())
						DIRTY_TASKS.get().remove(task);
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
					if (DIRTY_TASKS.exists())
						DIRTY_TASKS.get().remove(selectedTask.get());
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

		/* ~PROPERTIES */
		nameColumn.setCellValueFactory(t -> t.getValue().nameProperty());
		descriptionColumn.setCellValueFactory(t -> t.getValue().descriptionProperty());
		urgentColumn.setCellValueFactory(t -> t.getValue().urgentProperty());
		completeColumn.setCellValueFactory(t -> t.getValue().completedProperty());
		dueDateColumn.setCellValueFactory(
				t -> BindingTools.mask(t.getValue().dueDateProperty(), INSTANT_TO_LOCALDATE_GATEWAY::to));

		/* ~PROPERTIES */
		nameColumn.setCellFactory(__ -> new BasicCell<String>());
		descriptionColumn.setCellFactory(__ -> new BasicCell<>());
		urgentColumn.setCellFactory(__ -> new BooleanCheckBoxCell(a -> a.urgentProperty()));
		completeColumn.setCellFactory(__ -> new BooleanCheckBoxCell(a -> a.completedProperty()));
		dueDateColumn.setCellFactory(__ -> new BasicCell<>());
		// TODO Label Column
		labelColumn.setCellValueFactory(
				param -> new SimpleObjectProperty<ObservableList<Label>>(param.getValue().getLabels()));
		labelColumn.setCellFactory(param -> new BasicCell<ObservableList<Label>>() {
			private final HBox hbox = new HBox();
			private final ListChangeListener<Label> listener = new ListListener<Label>() {

				@Override
				public void added(List<? extends Label> items, int startpos) {
					for (Label l : items)
						hbox.getChildren().add(new LabelView(l));
				}

				@Override
				public void removed(List<? extends Label> items, int startpos) {
					for (Label l : items)
						for (Iterator<Node> iterator = hbox.getChildren().iterator(); iterator.hasNext();) {
							Node n = iterator.next();
							if (n instanceof LabelView && ((LabelView) n).getLabel() == l)
								iterator.remove();
						}
				}
			};

			{
				setGraphic(hbox);
				hbox.setSpacing(5);
				hbox.setAlignment(Pos.CENTER);
				setAlignment(Pos.CENTER);
			}

			public void emptied() {
				getItem().removeListener(listener);
				hbox.getChildren().clear();
			}

			protected void update(ObservableList<Label> item) {
				if (!isEmpty() && getItem() != null) {
					getItem().removeListener(listener);
					hbox.getChildren().clear();
				}
				for (Label l : item)
					hbox.getChildren().add(new LabelView(l));
				item.addListener(listener);
			};

		});

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

		Function<Label, LabelView> labelViewObtainer = l -> {
			LabelView e = new LabelView(l);
			e.setOnMouseClicked(event -> {
				if (event.getButton() == MouseButton.PRIMARY && selectedTask.get() != null)
					if (e.isSelected()) {
						e.deselect();
						selectedTask.get().removeLabel(l);
					} else {
						e.select();
						selectedTask.get().addLabel(l);
					}
			});
			return e;
		};

		Collection<Label> labelList = LabelManagerWindow.LABEL_LIST.get();
		for (Label l : labelList)
			labelSelectionBox.getChildren().add(labelViewObtainer.apply(l));
		LabelManagerWindow.LABEL_LIST.get().addListener(new ListListener<Label>() {

			@Override
			public void added(List<? extends Label> items, int startpos) {
				for (Label l : items)
					if (l.getName().toLowerCase().contains(labelFilter.getText().toLowerCase()))
						labelSelectionBox.getChildren().add(labelViewObtainer.apply(l));
			}

			@Override
			public void removed(List<? extends Label> items, int startpos) {
				NEXT_ITEM: for (Label l : items)
					if (l.getName().toLowerCase().contains(labelFilter.getText().toLowerCase()))
						for (Node lv : labelSelectionBox.getChildren())
							if (lv instanceof LabelView && ((LabelView) lv).getLabel() == l) {
								labelSelectionBox.getChildren().remove(lv);
								continue NEXT_ITEM;
							}
			}
		});

		labelFilter.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (newValue.isEmpty()) {
				labelSelectionBox.getChildren().clear();
				for (Label l1 : labelList)
					labelSelectionBox.getChildren().add(labelViewObtainer.apply(l1));
			} else if (newValue.contains(oldValue))
				for (Iterator<Node> iterator = labelSelectionBox.getChildren().iterator(); iterator.hasNext();) {
					Node n1 = iterator.next();
					if (n1 instanceof LabelView)
						if (!fits(((LabelView) n1).getLabel().getName(), newValue))
							iterator.remove();
				}
			else
				NEXT_LABEL: for (Label l2 : labelList) {
					boolean fits = fits(l2.getName(), newValue);
					if (fits) {
						for (Node n2 : labelSelectionBox.getChildren())
							if (n2 instanceof LabelView && ((LabelView) n2).getLabel() == l2)
								continue NEXT_LABEL;
						labelSelectionBox.getChildren().add(labelViewObtainer.apply(l2));
					} else
						for (Node n3 : labelSelectionBox.getChildren())
							if (n3 instanceof LabelView && ((LabelView) n3).getLabel() == l2) {
								labelSelectionBox.getChildren().remove(n3);
								break;
							}
				}
		});

		taskView.setItems(taskList);

	}

	private static boolean fits(String name, String query) {
		return name.toLowerCase().contains(query.toLowerCase());
	}

	static class NameNotFoundException extends Exception {

		/**
		 * SUID
		 */
		private static final long serialVersionUID = 1L;

	}

	/**
	 * Finds a feasible name using UUIDs and the given {@link Function}.
	 * 
	 * @param existenceChecker This should return <code>true</code> if the name that
	 *                         it's given is taken, and false otherwise.
	 * @return A feasible name, or a {@link NameNotFoundException} if no name was
	 *         available.
	 * @throws NameNotFoundException In case no feasible name could be found.
	 */
	static String findFeasibleName(Function<String, Boolean> existenceChecker) throws NameNotFoundException {
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

	static final File findFeasibleFile(File location, String extension) throws FileNotFoundException {
		String uuid = UUID.randomUUID().toString();
		File file = new File(location, uuid);
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
		Collection<Label> loadedLabels = LabelManagerWindow.LABEL_LIST.get();
		Task task = new Task(file, t -> {
			for (Label l : loadedLabels)
				if (l.getId().equals(t))
					return l;
			return Label.getNullLabel(t);
		});
		task.setCompleted(createComplete.isSelected());
		task.setUrgent(createUrgent.isSelected());
		task.setDescription(createDescription.getText());
		task.setName(createName.getText());
		task.setDueDate(instant);

		InvalidationListener invalidationListener = __ -> markDirty(task);

		/* ~PROPERTIES */
		task.completedProperty().addListener(invalidationListener);
		task.urgentProperty().addListener(invalidationListener);
		task.nameProperty().addListener(invalidationListener);
		task.descriptionProperty().addListener(invalidationListener);
		task.dueDateProperty().addListener(invalidationListener);
		task.getLabels().addListener(invalidationListener);

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
			if (empty && isEmpty() || item == getItem() || item != null && item.equals(getItem()))
				return;
			if (!isEmpty() && getItem() != null && (empty || item == null))
				emptied();
			else if (!(empty || item == null)) {
				if (isEmpty() || getItem() == null)
					populated();
				update(item);
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
		if (DIRTY_TASKS.exists())
			synchronized (DIRTY_TASKS.get()) {
				DIRTY_TASKS.get().clear();
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
		if (DIRTY_TASKS.exists())
			DIRTY_TASKS.get().clear();
	}

	private @FXML void flush() {
		if (TASK_LIST.exists())
			for (Task t : TASK_LIST.get())
				try {
					t.flush();
				} catch (FileNotFoundException e) {
					Logging.err("Failed to write the Task, \"" + t.getName() + "\" to its file:" + t.getData());
				}
		if (DIRTY_TASKS.exists())
			DIRTY_TASKS.get().clear();
	}

	@Override
	public void destroy() {
		if (DIRTY_TASKS.exists())
			for (Task t : DIRTY_TASKS.get())
				try {
					t.flush();
				} catch (FileNotFoundException e1) {
					Logging.err("Failed to write the task: \"" + selectedTask.get().getName() + "\" to the file: "
							+ selectedTask.get().getData().getAbsolutePath());
					Logging.err(e1);
				}
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(TaskSchedulerWindow.class.getResource("TaskSchedulerMainGUI.fxml"));
		loader.setController(this);
		try {
			Parent root = loader.load();
			stage.setScene(new Scene(root));
			root.getStylesheets().addAll(properties.popButtonStylesheet.get(), properties.themeStylesheet.get(),
					"zeale/apps/stuff/app/guis/windows/taskscheduler/TaskSchedulerStyles.css");
		} catch (IOException e) {
			Logging.err("Failed to load the Task Scheduler window's main GUI.");
			Logging.err(e);
		}
	}

}
