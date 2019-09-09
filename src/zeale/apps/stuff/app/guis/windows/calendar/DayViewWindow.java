package zeale.apps.stuff.app.guis.windows.calendar;

import java.time.LocalDate;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

class DayViewWindow extends Window {

	private final LocalDate date;
	private final ObservableMap<LocalDate, ObservableList<CalendarEvent>> dates;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public DayViewWindow(ObservableMap<LocalDate, ObservableList<CalendarEvent>> dates, LocalDate date) {
		this.dates = dates;
		this.date = date;
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		// TODO Auto-generated method stub

	}

}
