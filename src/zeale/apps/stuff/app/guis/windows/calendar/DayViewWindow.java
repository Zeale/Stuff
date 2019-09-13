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

class DayViewWindow extends Window {

	private final LocalDate date;
	private final ObservableMap<LocalDate, ObservableList<CalendarEvent>> events;
	private final CalendarWindow window;
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
			for (CalendarEvent ce : evs)
				if ((ce.getEndTime() == null ? ce.getTime() : ce.getEndTime()).isBefore(now))
					fin++;
				else
					un++;
		finishedEvents.set(fin);
		unfinishedEvents.set(un);
	}

	private @FXML void refreshTasks() {
		// TODO
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

	public DayViewWindow(CalendarWindow window, LocalDate date, boolean showBackButton,
			ObservableMap<LocalDate, ObservableList<CalendarEvent>> events) {
		this.window = window;
		this.date = date;
		(this.events = events).addListener(mapListener);
		this.showBackButton = showBackButton;
	}

	private @FXML void initialize() {
		if (!showBackButton)
			commandsMenu.setVisible(false);

		Data finEvents = new Data("Finished", 0), unfEvents = new Data("Unfinished", 0);

		ChangeListener<Number> listener = (observable, oldValue, newValue) -> {
			finEvents.setPieValue(finishedEvents.get() == 0 && unfinishedEvents.get() == 0 ? 0
					: finishedEvents.get() / ((double) finishedEvents.get() + unfinishedEvents.get()));
			unfEvents.setPieValue(finishedEvents.get() == 0 && unfinishedEvents.get() == 0 ? 0
					: unfinishedEvents.get() / ((double) unfinishedEvents.get() + finishedEvents.get()));
		};
		finishedEvents.addListener(listener);
		unfinishedEvents.addListener(listener);

		eventBreakdown.getData().setAll(finEvents, unfEvents);
		refreshEvents();
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

	private @FXML void back() {
		try {
			Stuff.displayWindow(window);
		} catch (WindowLoadFailureException e) {
			e.printStackTrace();
		}
	}

}
