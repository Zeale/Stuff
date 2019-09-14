package zeale.apps.stuff.app.guis.windows.calendar;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Menu;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.app.guis.windows.taskscheduler.Task;
import zeale.apps.stuff.app.guis.windows.taskscheduler.TaskSchedulerWindow;

class DayViewWindow extends Window {

	private final LocalDate date;
	private final ObservableMap<LocalDate, ObservableList<CalendarEvent>> events;
	private final boolean showBackButton;
	private @FXML Menu commandsMenu;

	private @FXML PieChart eventBreakdown, taskBreakdown, dayProgress;
	private final IntegerProperty finishedEvents = new SimpleIntegerProperty(),
			unfinishedEvents = new SimpleIntegerProperty(), finishedTasks = new SimpleIntegerProperty(),
			unfinishedTasks = new SimpleIntegerProperty();

	private @FXML void refreshEvents() {
		int fin = 0, un = 0;

		LocalTime now = LocalTime.now();
		ObservableList<CalendarEvent> evs = events.get(date);
		if (evs != null)
			if (date.isBefore(LocalDate.now()))
				fin = evs.size();
			else if (date.isAfter(LocalDate.now()))
				un = evs.size();
			else
				for (CalendarEvent ce : evs)
					if ((ce.getEndTime() == null ? ce.getTime() : ce.getEndTime()).isBefore(now))
						fin++;
					else
						un++;
		finishedEvents.set(fin);
		unfinishedEvents.set(un);
	}

	private @FXML void refreshTasks() {
		int fin = 0, un = 0;
		LocalDate today = date;
		for (Task task : TaskSchedulerWindow.getTasks()) {
			if (LocalDate.from(task.getDueDate()).equals(today))
				if (task.isCompleted())
					fin++;
				else
					un++;
		}
		finishedTasks.set(fin);
		unfinishedTasks.set(un);
	}

	private final InvalidationListener eventListener = o -> refreshEvents();

	private final MapChangeListener<LocalDate, ObservableList<CalendarEvent>> mapListener = change -> {
		if (change.wasAdded())
			change.getValueAdded().addListener(eventListener);
		else
			change.getValueRemoved().removeListener(eventListener);
	};

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public DayViewWindow(LocalDate date, boolean showBackButton,
			ObservableMap<LocalDate, ObservableList<CalendarEvent>> events) {
		this.date = date;
		(this.events = events).addListener(mapListener);
		this.showBackButton = showBackButton;
	}

	private @FXML void initialize() {
		if (!showBackButton)
			commandsMenu.setVisible(false);

		Data finEvents = new Data("Finished", 1), unfEvents = new Data("Unfinished", 0);

		ChangeListener<Number> listener = (observable, oldValue, newValue) -> {
			finEvents.setPieValue(finishedEvents.get() == 0 && unfinishedEvents.get() == 0 ? 1
					: finishedEvents.get() / ((double) finishedEvents.get() + unfinishedEvents.get()));
			unfEvents.setPieValue(finishedEvents.get() == 0 && unfinishedEvents.get() == 0 ? 0
					: unfinishedEvents.get() / ((double) unfinishedEvents.get() + finishedEvents.get()));
		};
		finishedEvents.addListener(listener);
		unfinishedEvents.addListener(listener);

		eventBreakdown.getData().setAll(finEvents, unfEvents);
		finEvents.getNode().setStyle("-fx-border-color: derive(-fx-pie-color, -60%);");
		unfEvents.getNode().setStyle("-fx-border-color: derive(-fx-pie-color, -60%);");
		refreshEvents();

		//

		Data finTasks = new Data("Finished", 1), unfTasks = new Data("Unfinished", 0);

		listener = (observable, oldValue, newValue) -> {
			finTasks.setPieValue(finishedTasks.get() == 0 && unfinishedTasks.get() == 0 ? 1
					: finishedTasks.get() / ((double) finishedTasks.get() + unfinishedTasks.get()));
			finTasks.setPieValue(finishedTasks.get() == 0 && unfinishedTasks.get() == 0 ? 0
					: unfinishedTasks.get() / ((double) unfinishedTasks.get() + finishedTasks.get()));
		};
		finishedTasks.addListener(listener);
		unfinishedTasks.addListener(listener);

		taskBreakdown.getData().setAll(finTasks, unfTasks);
		finTasks.getNode().setStyle("-fx-border-color: derive(-fx-pie-color, -60%);");
		unfTasks.getNode().setStyle("-fx-border-color: derive(-fx-pie-color, -60%);");
		refreshTasks();
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(DayViewWindow.class.getResource("DayViewGUI.fxml"));
		loader.setController(this);
		try {
			Parent parent = loader.load();
			parent.getStylesheets().addAll(properties.popButtonStylesheet.get(), properties.themeStylesheet.get());
			stage.setScene(new Scene(parent));
		} catch (IOException e) {
			throw new WindowLoadFailureException("Failed to show the Daily View window.", e);
		}

	}

	private @FXML void goBack() {
		try {
			Stuff.displayWindow(new CalendarWindow());
		} catch (WindowLoadFailureException e) {
			e.printStackTrace();
		}
	}

}
