package zeale.apps.stuff.app.guis.windows.calendar;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class CalendarEventTracker {
	private final ObservableMap<LocalDate, ObservableList<CalendarEvent>> eventList = FXCollections.observableHashMap();

	/**
	 * Gets the list of events pertaining to a specific day.
	 * 
	 * @param date The {@link LocalDate} to get the list of events for.
	 * @return
	 */
	public List<CalendarEvent> getEvents(LocalDate date) {
		return eventList.containsKey(date) ? eventList.get(date) : Collections.emptyList();
	}

}
