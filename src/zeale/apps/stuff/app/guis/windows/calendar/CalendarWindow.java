package zeale.apps.stuff.app.guis.windows.calendar;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import org.alixia.javalibrary.javafx.bindings.BindingTools;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

public class CalendarWindow extends Window {

	private Stage stage;

	@Override
	public void destroy() {
		stage.setMinHeight(0);
		stage.setMinWidth(0);
	}

	private @FXML GridPane calendar;

	// The first node under Sunday in calendar will be grid[0][0].
	// grid[column][row]
	// This, however, matches with calendar's 0,1 element.
	// grid[col][row] == cal[col][row + 1]
	// This is because of the weekdays at the top of the calendar gridpane.
	private CalendarCell[][] grid = new CalendarCell[7][6];
	private @FXML Text currMonth;
	private final IntegerProperty year = new SimpleIntegerProperty();
	private final ObjectProperty<Month> month = new SimpleObjectProperty<>();

	private @FXML void left() {
		Month newMonth = month.get().minus(1);
		if (newMonth == Month.DECEMBER)
			year.set(year.get() - 1);
		month.set(newMonth);
		recalcGrid();
	}

	private @FXML void right() {
		Month newMonth = month.get().plus(1);
		if (newMonth == Month.JANUARY)
			year.set(year.get() + 1);
		month.set(newMonth);
		recalcGrid();
	}

	void disable(int x, int y) {
		grid[x][y].setDisable(true);
	}

	void enable(int x, int y) {
		grid[x][y].setDisable(false);
	}

	void set(int x, int y, CalendarCell cell) {
		if (cell == null)
			throw null;
		calendar.getChildren().remove(grid[x][y]);
		grid[x][y] = cell;
		calendar.add(cell, x, y + 1);
	}

	void set(int gridx, int gridy, CalendarCell cell, int gridPaneChildPos) {
		if (cell == null)
			throw null;
		calendar.getChildren().remove(grid[gridx][gridy]);
		grid[gridx][gridy] = cell;
		calendar.getChildren().add(gridPaneChildPos, grid[gridx][gridy] = cell);
		GridPane.setColumnIndex(cell, gridx);
		GridPane.setRowIndex(cell, gridy + 1);
	}

	/**
	 * Creates a {@link CalendarCell} and gives it its necessary borders via
	 * {@link CalendarCell#setStyle(String)}.
	 * 
	 * @param x The x position. This is used for border calculation.
	 * @param y The y position. This is also used for border calculation.
	 * @return The {@link CalendarCell}.
	 */
	CalendarCell createCell(int x, int y) {
		CalendarCell cell = new CalendarCell();
		cell.setStyle("-fx-border-color: transparent " + (x < 6 ? "-stuff-dark " : "transparent ")
				+ (y < 5 ? "-stuff-dark" : "transparent") + " transparent");
		return cell;
	}

	/**
	 * Creates a new {@link CalendarCell} with the specified coordinates and calls
	 * {@link #set(int, int, CalendarCell)} on it with the specified coordinates.
	 * This places the new cell in the specified position, both in the grid and in
	 * the gridpane.
	 * 
	 * @param x The x position of the cell.
	 * @param y The y position of the cell.
	 * @return The newly created {@link CalendarCell}.
	 */
	CalendarCell setNewCell(int x, int y) {
		CalendarCell cell = createCell(x, y);
		set(x, y, cell);
		return cell;
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
				setNewCell(g, j).setNumber(maxDaysOfLastMonth--);
				disable(g, j);
			}
		}

		ROWITR: for (; j < 6; j++, i = 0)
			for (; i < 7; i++, day++) {
				if (day > maxDaysThisMonth)
					break ROWITR;
				setNewCell(i, j).setNumber(day);
				enable(i, j);
			}
		if (day > maxDaysThisMonth && j < 6) {
			day = 1;
			for (; j < 6; j++, i = 0)
				for (; i < 7; i++, day++) {
					setNewCell(i, j).setNumber(day);
					disable(i, j);
				}
		}
		if (calendarView.get() != null)
			calendarView.get().style(this);

	}

	/**
	 * Returns the {@link CalendarCell} for the given day of the current month.
	 * 
	 * @param day The day represented by the {@link CalendarCell} that will be
	 *            returned.
	 * @return The {@link CalendarCell}.
	 */
	CalendarCell cell(int day) {
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

	private ObjectProperty<CalendarView<CalendarWindow>> calendarView = new SimpleObjectProperty<>(
			new DefaultCalendarView());
	{
		calendarView.addListener((a, b, c) -> c.style(this));
	}

	private @FXML void initialize() {
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++) {
				calendar.add(grid[i][j] = new CalendarCell(), i, j + 1);
				grid[i][j].setStyle("-fx-border-color: transparent " + (i < 6 ? "-stuff-dark " : "transparent ")
						+ (j < 5 ? "-stuff-dark" : "transparent") + " transparent");
			}

		LocalDate now = LocalDate.now();
		month.set(now.getMonth());
		year.set(now.getYear());

		currMonth.textProperty()
				.bind(BindingTools.mask(month, t -> t.getDisplayName(TextStyle.FULL, Locale.getDefault())));

		recalcGrid();

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

	private @FXML void goHome() {
		Stuff.displayHome();
	}

}
