package zeale.apps.stuff.app.guis.windows.calendar;

import java.time.LocalDate;
import java.time.LocalTime;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class CalendarEventGUIController {
	private @FXML TableView<CalendarEvent> eventTable;
	private @FXML TableColumn<CalendarEvent, String> nameColumn, descriptionColumn;
	private @FXML TableColumn<CalendarEvent, LocalDate> dateColumn;
	private @FXML TableColumn<CalendarEvent, LocalTime> timeColumn, endTimeColumn;

	private @FXML void initialize() {

	}
}
