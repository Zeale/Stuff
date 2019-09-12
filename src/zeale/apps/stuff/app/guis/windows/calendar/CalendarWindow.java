package zeale.apps.stuff.app.guis.windows.calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.api.utilities.Utils;

public class CalendarWindow extends Window {

	/**
	 * This object is used by the {@link CalendarCellBox} class. Whenever a
	 * {@link CalendarCell} gets assigned to a {@link CalendarCellBox}, the cell
	 * gets the box placed in the cell's properties map, with this object as the
	 * key. Whenever the cell is moved to a different box, if ever, it is first
	 * removed from the previous box, if any, by means involving this.
	 */
	private final Object CALENDAR_CELL_BOX_CALCELL_MATCH_KEY = new Object();
	private static final File CALENDAR_EVENT_STORAGE_LOCATION = new File(Stuff.APPLICATION_DATA, "Calendar/Events");
	// TODO Check bounds for years.

	/**
	 * A {@link StackPane} that handles
	 * 
	 * @author Zeale
	 *
	 */
	public final class CalendarCellBox extends StackPane {

		private final int x, y;

		private final ObjectProperty<CalendarCell> calendarCell = new SimpleObjectProperty<>();

		{

			calendarCell.addListener((ChangeListener<CalendarCell>) (observable, oldValue, newValue) -> {
				if (oldValue != null) {
					oldValue.getProperties().remove(CALENDAR_CELL_BOX_CALCELL_MATCH_KEY);
					getChildren().remove(oldValue);
				}
				if (newValue != null) {
					if (newValue.getProperties().containsKey(CALENDAR_CELL_BOX_CALCELL_MATCH_KEY))
						((CalendarCellBox) newValue.getProperties()
								.get(CALENDAR_CELL_BOX_CALCELL_MATCH_KEY)).calendarCell.set(null);
					newValue.getProperties().put(CALENDAR_CELL_BOX_CALCELL_MATCH_KEY, this);
					getChildren().add(newValue);
				}
			});
		}

		public CalendarCellBox(int x, int y) {
			this.x = x;
			this.y = y;
			calendar.add(this, x, y + 1);
			setStyle("-fx-border-width: 0 " + (x < 6 ? "1 " : "0 ") + (y < 5 ? "1" : "0")
					+ " 0;-fx-border-color: -stuff-dark;");
			grid[x][y] = this;
		}

		public ObjectProperty<CalendarCell> calendarCellProperty() {
			return calendarCell;
		}

		public CalendarCell getCalendarCell() {
			return calendarCell.get();
		}

		public GridPane getGridPane() {
			return calendar;
		}

		public int getGridX() {
			return getX();
		}

		public int getGridY() {
			return getY();
		}

		/**
		 * <p>
		 * Returns the position of this {@link CalendarCellBox} in the grid. The
		 * position <code>0,0</code> would be the first column in the
		 * {@link CalendarWindow#calendar} grid pane, but the <em>second</em> row in
		 * that grid pane. This is due to the first row being taken up by the names of
		 * the weekdays. Because of that, <b>this value is <em>not</em> the same as the
		 * position of this node inside the {@link CalendarWindow#calendar} grid
		 * pane.</b>
		 * </p>
		 * 
		 * @return The x position of this {@link CalendarCellBox}.
		 */
		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public void setCalendarCell(CalendarCell calendarCell) {
			this.calendarCell.set(calendarCell);
		}

	}

	/**
	 * <p>
	 * This {@link Map} contains all the events loaded by the Calendar app.
	 * </p>
	 * <h1>Modification</h1>
	 * <p>
	 * Modifications to this map, itself, should take place only under the following
	 * conditions:
	 * <ol>
	 * <li>For an {@link ObservableList} to be put into this {@link Map}, the
	 * {@link ObservableList} should be empty. This assures that any classes
	 * depending on observing this map and this map's {@link ObservableList}s will
	 * not have to handle any new {@link CalendarEvent} elements in their map
	 * listeners, by assuring that all handling of {@link CalendarEvent}s being
	 * added to this map can be safely performed observers of this {@link Map}'s
	 * {@link ObservableList} values.</li>
	 * <li>For an {@link ObservableList} to be removed from this {@link Map}, it
	 * must be empty. This assures that observers will only have to observe this
	 * {@link Map}'s {@link ObservableList} values for removals of
	 * {@link CalendarEvent}s.</li>
	 * </ol>
	 * Rather than putting an {@link ObservableList}, that contains
	 * {@link CalendarEvent}s, into this {@link Map}, an empty
	 * {@link ObservableList} should be put and then populated immediately after.
	 * </p>
	 */
	private final static ObservableMap<LocalDate, ObservableList<CalendarEvent>> CALENDAR_EVENTS = FXCollections
			.observableHashMap();
	private static final ObservableList<CalendarEvent> DIRTY_CALENDAR_EVENTS = FXCollections.observableArrayList();

	static void markDirty(CalendarEvent event) {
		if (!DIRTY_CALENDAR_EVENTS.contains(event))
			DIRTY_CALENDAR_EVENTS.add(event);
	}

	static {

		try {
			if (!CALENDAR_EVENT_STORAGE_LOCATION.isDirectory())
				CALENDAR_EVENT_STORAGE_LOCATION.mkdirs();
			else
				for (File f : CALENDAR_EVENT_STORAGE_LOCATION.listFiles()) {
					if (f.isDirectory())
						Logging.wrn("Found a directory in the Calendar app's Event storage directory. (Directory: "
								+ f.getAbsolutePath() + ")");
					else
						try {
							CalendarEvent ev = CalendarEvent.load(f);

							ChangeListener<LocalDate> eventDateListener = (ChangeListener<LocalDate>) (observable,
									oldValue, newValue) -> {
								CALENDAR_EVENTS.get(oldValue).remove(ev);

								ObservableList<CalendarEvent> eventsForTheNewDate;
								if (CALENDAR_EVENTS.containsKey(newValue))
									eventsForTheNewDate = CALENDAR_EVENTS.get(newValue);
								else
									CALENDAR_EVENTS.put(newValue,
											eventsForTheNewDate = FXCollections.observableArrayList());
								eventsForTheNewDate.add(ev);
							};
							ev.dateProperty().addListener(eventDateListener);

							InvalidationListener dirtyMarker = observable -> markDirty(ev);
							ev.dateProperty().addListener(dirtyMarker);
							ev.descriptionProperty().addListener(dirtyMarker);
							ev.nameProperty().addListener(dirtyMarker);
							// ~PROPERTIES

							ObservableList<CalendarEvent> evs;
							if (CALENDAR_EVENTS.containsKey(ev.getDate()))
								evs = CALENDAR_EVENTS.get(ev.getDate());
							else
								CALENDAR_EVENTS.put(ev.getDate(), evs = FXCollections.observableArrayList());
							evs.add(ev);
						} catch (Exception e) {
							Logging.err("An error occurred while trying to load a Calendar Event.");
							Logging.err(e);
						}
				}
		} catch (Exception e) {
			Logging.err("An exception occurred while loading calendar events from the file.");
		}
	}

	// TODO Check bounds for years.

	public static CalendarEvent createCalendarEvent() throws FileNotFoundException {
		return new CalendarEvent(Utils.findFeasibleFile(CALENDAR_EVENT_STORAGE_LOCATION, ".cev"));
	}

	public static void reload() {

	}

	private Stage stage;

	private @FXML GridPane calendar;
	// The first node under Sunday in calendar will be grid[0][0].
	// grid[column][row]
	// This, however, matches with calendar's 0,1 element.
	// grid[col][row] == cal[col][row + 1]
	// This is because of the weekdays at the top of the calendar gridpane.
	private CalendarCellBox[][] grid = new CalendarCellBox[7][6];
	private @FXML Text currMonth;
	private final IntegerProperty year = new SimpleIntegerProperty();

	private final ObjectProperty<Month> month = new SimpleObjectProperty<>();

	private ObjectProperty<CalendarStyle<CalendarWindow>> calendarView = new SimpleObjectProperty<>(
			new DefaultCalendarView());

	{
		calendarView.addListener((a, b, c) -> c.style(this));
	}

	/**
	 * Returns the {@link CalendarCellBox} for the given day of the current month.
	 * 
	 * @param day The day represented by the {@link CalendarCellBox} that will be
	 *            returned.
	 * @return The {@link CalendarCellBox}.
	 */
	CalendarCellBox cellBox(int day) {
		Month month = this.month.get();
		if (day > month.maxLength() || day < 0)
			throw new IllegalArgumentException();

		// Index of first day of this month.
		DayOfWeek dayOfWeek = LocalDate.of(this.year.get(), this.month.get(), 1).getDayOfWeek();
		int weekday = WeekUtils.weekdayToIndex(dayOfWeek.getValue());
		int resCol = (weekday + day - 1) % 7;
		int resRow = (weekday + day - 1) / 7;

		return grid[resCol][resRow];
	}

	@Override
	public void destroy() {
		stage.setMinHeight(0);
		stage.setMinWidth(0);
		for (CalendarEvent e : DIRTY_CALENDAR_EVENTS)
			try {
				e.flush();
			} catch (FileNotFoundException e1) {
				Logging.err("Failed to save a CalendarEvent with the name of " + e.getName()
						+ ". Information about the error has been printed below.");
			}

	}

	public CalendarCellBox[][] getGrid() {
		return grid;
	}

	public Month getMonth() {
		return month.get();
	}

	public int getYear() {
		return year.get();
	}

	private @FXML void goHome() {
		Stuff.displayHome();
	}

	private @FXML void initialize() {
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++)
				new CalendarCellBox(i, j);

		LocalDate now = LocalDate.now();
		month.set(now.getMonth());
		year.set(now.getYear());

		currMonth.textProperty()
				.bind(Bindings.createStringBinding(() -> month.get().getDisplayName(TextStyle.FULL, Locale.getDefault())
						+ ", " + (year.get() < 0 ? -year.get() + " BCE" : year.get()), year, month));

		recalcGrid();

	}

	private @FXML void left() {
		Month newMonth = month.get().minus(1);
		if (newMonth == Month.DECEMBER)
			year.set(year.get() - 1);
		month.set(newMonth);
		recalcGrid();
	}

	private @FXML void leftX() {
		year.set(year.get() - 1);
		recalcGrid();
	}

	public ObjectProperty<Month> monthProperty() {
		return month;
	}

	private CalendarCell createCalendarCell() {
		CalendarCell cell = new CalendarCell();
		cell.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY) {
				DayViewWindow dayWindow = new DayViewWindow(this,
						LocalDate.of(year.get(), month.get(), cell.getNumber()), true, CALENDAR_EVENTS);
				try {
					Stuff.displayWindow(dayWindow);
				} catch (WindowLoadFailureException e) {
					Logging.err("Failed to load up and show the GUI for that day...");
					Logging.err(e);
				}
			}
		});
		return cell;
	}

	protected void layoutPreviousMonth(int x, int y, int day) {
		CalendarCell cell = createCalendarCell();
		cell.setNumber(day);
		grid[x][y].setCalendarCell(cell);
		LocalDate cellDate = LocalDate.of(year.get(), month.get(), day);
		if (CALENDAR_EVENTS.containsKey(cellDate))
			cell.setEventCount(CALENDAR_EVENTS.get(cellDate).size());
		cell.setDisable(true);
	}

	protected void layoutCurrentMonth(int x, int y, int day) {
		CalendarCell cell = createCalendarCell();
		grid[x][y].setCalendarCell(cell);
		LocalDate cellDate = LocalDate.of(year.get(), month.get(), day);
		if (CALENDAR_EVENTS.containsKey(cellDate))
			cell.setEventCount(CALENDAR_EVENTS.get(cellDate).size());
		cell.setNumber(day);
	}

	protected void layoutNextMonth(int x, int y, int day) {
		CalendarCell cell = createCalendarCell();
		cell.setNumber(day);
		grid[x][y].setCalendarCell(cell);
		LocalDate cellDate = LocalDate.of(year.get(), month.get(), day);
		if (CALENDAR_EVENTS.containsKey(cellDate))
			cell.setEventCount(CALENDAR_EVENTS.get(cellDate).size());
		cell.setDisable(true);
	}

	/**
	 * Recalculates and restyles the entire calendar.
	 */
	protected void recalcGrid() {
		LocalDate firstDay = LocalDate.of(year.get(), month.get(), 1);
		DayOfWeek dayOfWeek = firstDay.getDayOfWeek();
		int day = 1, i = WeekUtils.weekdayToIndex(dayOfWeek), j = 0;
		int maxDaysThisMonth = firstDay.lengthOfMonth();

		if (i != 0)
			for (int maxDaysOfLastMonth = firstDay.minusMonths(1).lengthOfMonth(), g = i - 1; g >= 0; g--)
				layoutPreviousMonth(g, j, maxDaysOfLastMonth--);

		ROWITR: for (; j < 6; j++, i = 0)
			for (; i < 7; i++, day++)
				if (day > maxDaysThisMonth)
					break ROWITR;
				else
					layoutCurrentMonth(i, j, day);

		if (day > maxDaysThisMonth && j < 6)
			for (day = 1; j < 6; j++, i = 0)
				for (; i < 7; i++, day++)
					layoutNextMonth(i, j, day);

		if (calendarView.get() != null)
			calendarView.get().style(this);

	}

	private @FXML void right() {
		Month newMonth = month.get().plus(1);
		if (newMonth == Month.JANUARY)
			year.set(year.get() + 1);
		month.set(newMonth);
		recalcGrid();
	}

	private @FXML void rightX() {
		year.set(year.get() + 1);
		recalcGrid();
	}

	public void setMonth(Month month) {
		this.month.set(month);
	}

	public void setYear(int year) {
		this.year.set(year);
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		stage.setMinHeight(500);
		stage.setMinWidth(900);

		this.stage = stage;

		FXMLLoader loader = new FXMLLoader(CalendarWindow.class.getResource("CalendarGUI.fxml"));
		loader.setController(this);

		try {
			Parent root = loader.load();
			root.getStylesheets().addAll(properties.popButtonStylesheet.get(), properties.themeStylesheet.get());
			stage.setScene(new Scene(root));
		} catch (IOException e) {
			throw new WindowLoadFailureException(e);
		}
	}

	public IntegerProperty yearProperty() {
		return year;
	}

}
