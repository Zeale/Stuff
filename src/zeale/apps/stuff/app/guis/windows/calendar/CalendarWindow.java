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

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	private @FXML GridPane calendar;

	// The first node under Sunday in calendar will be grid[0][1].
	// grid[column][row + 1]
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

	private void recalcGrid() {
		LocalDate firstDay = LocalDate.of(year.get(), month.get(), 1);
		DayOfWeek dayOfWeek = firstDay.getDayOfWeek();
		int day = 1, i = weekdayToIndex(dayOfWeek), j = 0;
		int maxDaysThisMonth = firstDay.lengthOfMonth();

		if (i != 0) {
			int maxDaysOfLastMonth = firstDay.minusMonths(1).lengthOfMonth();
			for (int g = i - 1; g >= 0; g--) {
				grid[g][j].setNumber(maxDaysOfLastMonth--);
				grid[g][j].setDisable(true);
			}
		}

		ROWITR: for (; j < 6; j++, i = 0)
			for (; i < 7; i++, day++) {
				if (day > maxDaysThisMonth) {
					break ROWITR;
				}
				grid[i][j].setNumber(day);
				grid[i][j].setDisable(false);
			}
		if (day > maxDaysThisMonth && j < 6) {
			day = 1;
			for (; j < 6; j++, i = 0)
				for (; i < 7; i++, day++) {
					grid[i][j].setNumber(day);
					grid[i][j].setDisable(true);
				}
		}
	}

	private int weekdayToIndex(DayOfWeek day) {
		return ordinalToIndex(day.ordinal());
	}

	private int weekdayToIndex(int dayOfWeekIndex) {
		return ordinalToIndex(dayOfWeekIndex - 1);
	}

	private int ordinalToIndex(int ordinal) {
		return (ordinal + 1) % 7;
	}

	private DayOfWeek indexToWeekday(int index) {
		return DayOfWeek.of((index + 6) % 7 + 1);
	}

	private @FXML void initialize() {
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++) {
				calendar.getChildren().add(0, grid[i][j] = new CalendarCell());
				GridPane.setColumnIndex(grid[i][j], i);
				GridPane.setRowIndex(grid[i][j], j + 1);
				grid[i][j].setStyle("-fx-border-color: " + (j == 0 ? "transparent" : "-stuff-dark")
						+ " transparent transparent " + (i == 0 ? "transparent" : "-stuff-dark") + ";");
			}

		LocalDate now = LocalDate.now();
		month.set(now.getMonth());
		year.set(now.getYear());

		currMonth.textProperty()
				.bind(BindingTools.mask(month, t -> t.getDisplayName(TextStyle.FULL, Locale.getDefault())));

		recalcGrid();

		// Methods that change values recalc grid manually.
//		ChangeListener<Object> listener = (observable, oldValue, newValue) -> recalcGrid();
//		year.addListener(listener);
//		month.addListener(listener);

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
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
