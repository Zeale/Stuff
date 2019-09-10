package zeale.apps.stuff.app.guis.windows.calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import org.alixia.javalibrary.javafx.bindings.BindingTools;
import org.alixia.javalibrary.util.Box;
import org.alixia.javalibrary.util.Pair;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import zeale.applicationss.notesss.utilities.Utilities;
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
	public static final double DEFAULT_CELL_BACKGROUND_OPACITY = 0.2;
	// TODO Check bounds for years.

	private static final Color DEFAULT_DISABLED_CELL_BACKGROUND_COLOR = new Color(0.6, 0, 0,
			DEFAULT_CELL_BACKGROUND_OPACITY);
	private static final Background DEFAULT_DISABLED_CELL_BACKGROUND = Utilities
			.getBackgroundFromColor(DEFAULT_DISABLED_CELL_BACKGROUND_COLOR);

	/**
	 * A {@link StackPane} that handles
	 * 
	 * @author Zeale
	 *
	 */
	public final class CalendarCellBox extends StackPane {

		private final IntegerProperty number = new SimpleIntegerProperty(), eventCount = new SimpleIntegerProperty();

		private final Text numberText = new Text(), eventText = new Text();

		private final int x, y;

		private final ObjectProperty<CalendarCell> calendarCell = new SimpleObjectProperty<>();

		{
			getChildren().add(numberText);
			setAlignment(numberText, Pos.TOP_RIGHT);
			numberText.setStyle("-fx-font-size: 1.4em;-fx-color: egg;");

			numberText.textProperty().bind(BindingTools.mask(number, Number::toString));

			eventText.setBoundsType(TextBoundsType.VISUAL);
			eventText.setFont(Font.font("monospace"));
			Rectangle box = new Rectangle();
			box.strokeProperty().bind(numberText.fillProperty());
			box.setFill(Color.TRANSPARENT);
			eventText.fillProperty().bind(box.strokeProperty());

			StackPane eventBox = new StackPane(box, eventText);
			StackPane.setAlignment(eventBox, Pos.BOTTOM_LEFT);
			eventCount.addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					if (newValue.intValue() == 0)
						getChildren().remove(eventBox);
					else {
						String str = newValue.toString();
						eventText.setText(str);
						Pair<Double, Double> size = getSize(str, eventText.getFont());

						box.setWidth(Math.max(size.first + 8, size.second + 6));
						box.setHeight(size.second + 6);

						eventBox.setMaxSize(box.getWidth() + 2, box.getHeight() + 2);

						box.setArcHeight(5);
						box.setArcWidth(5);
						if (!getChildren().contains(eventBox))
							getChildren().add(eventBox);
					}
				}
			});

			StackPane.setMargin(numberText, new Insets(0, 5, 0, 0));// Match inexplicable downward shift.

			Box<Background> bg = new Box<>();
			setOnMouseEntered(event -> {
				numberText.setFill(Color.RED);
				bg.value = getBackground();
				setBackgroundColor(Color.ORANGE);
			});
			setOnMouseExited(event -> {
				numberText.setFill(Color.GOLD);
				setBackground(bg.value);
			});
			disabledProperty().addListener((observable, oldValue,
					newValue) -> setBackground(newValue ? DEFAULT_DISABLED_CELL_BACKGROUND : null));

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
			setStyle("-fx-border-color: transparent " + (x < 6 ? "-stuff-dark " : "transparent ")
					+ (y < 5 ? "-stuff-dark" : "transparent") + " transparent");
			grid[x][y] = this;
		}

		public ObjectProperty<CalendarCell> calendarCellProperty() {
			return calendarCell;
		}

		public final IntegerProperty eventCountProperty() {
			return this.eventCount;
		}

		public CalendarCell getCalendarCell() {
			return calendarCell.get();
		}

		public final int getEventCount() {
			return this.eventCountProperty().get();
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

		public final int getNumber() {
			return this.numberProperty().get();
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

		public final IntegerProperty numberProperty() {
			return this.number;
		}

		/**
		 * Sets the background color of this {@link CalendarCell} to be the given color
		 * <em>with an opacity of <code>0.2</code></em>.
		 * 
		 * @param color The {@link Color} to set this {@link CalendarCell}'s background
		 *              to.
		 */
		public void setBackgroundColor(Color color) {
			setBackgroundColorStrict(color.deriveColor(0, 1, 1, 0.2));
		}

		/**
		 * Sets the background of this {@link CalendarCell} to be a {@link Background}
		 * derived from the given {@link Paint}.
		 * 
		 * @param color The {@link Paint} to make the background of this
		 *              {@link CalendarCell}.
		 */
		public void setBackgroundColorStrict(Paint color) {
			setBackground(Utilities.getBackgroundFromColor(color));
		}

		public void setCalendarCell(CalendarCell calendarCell) {
			this.calendarCell.set(calendarCell);
		}

		public final void setEventCount(final int eventCount) {
			this.eventCountProperty().set(eventCount);
		}

		public final void setNumber(final int number) {
			this.numberProperty().set(number);
		}

	}

	private final static ObservableMap<LocalDate, ObservableList<CalendarEvent>> calendarEvents = FXCollections
			.observableHashMap();
	private static final ObservableList<CalendarEvent> DIRTY_CALENDAR_EVENTS = FXCollections.observableArrayList();

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

							InvalidationListener dirtyMarker = observable -> {
								if (!DIRTY_CALENDAR_EVENTS.contains(ev))
									DIRTY_CALENDAR_EVENTS.add(ev);
							};
							ev.dateProperty().addListener(dirtyMarker);
							ev.descriptionProperty().addListener(dirtyMarker);
							ev.nameProperty().addListener(dirtyMarker);
							// ~PROPERTIES

							ObservableList<CalendarEvent> evs;
							if (calendarEvents.containsKey(ev.getDate()))
								evs = calendarEvents.get(ev.getDate());
							else
								calendarEvents.put(ev.getDate(), evs = FXCollections.observableArrayList());
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

	private ObjectProperty<CalendarView<CalendarWindow>> calendarView = new SimpleObjectProperty<>(
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
			for (int j = 0; j < grid[i].length; j++) {
				new CalendarCellBox(i, j);
			}

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

	/**
	 * Recalculates and restyles the entire calendar.
	 */
	private void recalcGrid() {
		LocalDate firstDay = LocalDate.of(year.get(), month.get(), 1);
		DayOfWeek dayOfWeek = firstDay.getDayOfWeek();
		int day = 1, i = WeekUtils.weekdayToIndex(dayOfWeek), j = 0;
		int maxDaysThisMonth = firstDay.lengthOfMonth();

		if (i != 0) {
			int maxDaysOfLastMonth = firstDay.minusMonths(1).lengthOfMonth();
			for (int g = i - 1; g >= 0; g--) {
				CalendarCell cell = new CalendarCell();
				cell.setNumber(maxDaysOfLastMonth--);
				grid[g][j].setCalendarCell(cell);
				LocalDate cellDate = LocalDate.of(year.get(), month.get(), day);
				if (calendarEvents.containsKey(cellDate))
					cell.setEventCount(calendarEvents.get(cellDate).size());
				cell.setDisable(true);
			}
		}

		ROWITR: for (; j < 6; j++, i = 0)
			for (; i < 7; i++, day++) {
				if (day > maxDaysThisMonth)
					break ROWITR;
				CalendarCell cell = new CalendarCell();
				grid[i][j].setCalendarCell(cell);
				LocalDate cellDate = LocalDate.of(year.get(), month.get(), day);
				if (calendarEvents.containsKey(cellDate))
					cell.setEventCount(calendarEvents.get(cellDate).size());
				cell.setNumber(day);
			}
		if (day > maxDaysThisMonth && j < 6) {
			day = 1;
			for (; j < 6; j++, i = 0)
				for (; i < 7; i++, day++) {
					CalendarCell cell = new CalendarCell();
					cell.setNumber(day);
					grid[i][j].setCalendarCell(cell);
					LocalDate cellDate = LocalDate.of(year.get(), month.get(), day);
					if (calendarEvents.containsKey(cellDate))
						cell.setEventCount(calendarEvents.get(cellDate).size());
					cell.setDisable(true);
				}
		}
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

	private static Pair<Double, Double> getSize(String txt, Font font) {
		Text text = new Text(txt);
		text.setFont(font);
		new Scene(new Group(text));
		text.applyCss();
		Bounds bounds = text.getLayoutBounds();
		return new Pair<>(bounds.getWidth(), bounds.getHeight());
	}

}
