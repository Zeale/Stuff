package zeale.apps.stuff.app.guis.windows.calendar;

import java.time.LocalDate;

import javafx.scene.paint.Color;

class DefaultCalendarView extends CalendarView<CalendarWindow> {

	private static final Color CURRENT_DAY_COLOR = Color.BLUE, FIRST_DAY_OF_MONTH_COLOR = Color.PURPLE;

	@Override
	protected void style(CalendarWindow window) {
		LocalDate currDate = LocalDate.now();
		if (currDate.getYear() == window.getYear() && currDate.getMonth() == window.getMonth())
			window.cell(currDate.getDayOfMonth()).setBackgroundColor(CURRENT_DAY_COLOR);
		window.cell(1).setBackgroundColor(FIRST_DAY_OF_MONTH_COLOR);
	}

	@Override
	protected Legend getLegend() {
		// TODO Auto-generated method stub
		return null;
	}

}
