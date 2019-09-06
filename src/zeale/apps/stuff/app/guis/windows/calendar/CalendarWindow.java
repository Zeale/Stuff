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
import javafx.beans.value.ChangeListener;
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
	private final ObjectProperty<Month> month = new SimpleObjectProperty<>();;

	private @FXML void left() {

	}

	private @FXML void right() {

	}

	private void recalcGrid() {
		LocalDate firstDay = LocalDate.of(year.get(), month.get(), 1);
		DayOfWeek dayOfWeek = firstDay.getDayOfWeek();
		int day = 1, i = weekdayToIndex(dayOfWeek), j = 1;
		int maxDaysThisMonth = firstDay.lengthOfMonth();

		System.out.println(maxDaysThisMonth);

		for (; i < 7; i++, day++)
			grid[i][0].setNumber(day);
		ROWITR: for (; j < 6; j++)
			for (i = 0; i < 7; i++, day++) {
				if (day > maxDaysThisMonth) {
					System.out.println("BREAKING AT " + day);
					break ROWITR;
				}
				grid[i][j].setNumber(day);
			}
		if (day > maxDaysThisMonth && j < 6) {
			day = 1;
			for (; i < 7; i++, day++) {
				grid[i][j].setNumber(day);
				grid[i][j].setDisable(true);
			}
			for (j++; j < 6; j++)
				for (i = 0; i < 7; i++, day++) {
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

		ChangeListener<Object> listener = (observable, oldValue, newValue) -> recalcGrid();
		year.addListener(listener);
		month.addListener(listener);// TODO

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
