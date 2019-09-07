package zeale.apps.stuff.app.guis.windows.calendar;

import javafx.scene.paint.Color;

class DefaultCalendarView extends CalendarView<CalendarWindow> {

	@Override
	protected void style(CalendarWindow window) {
		window.cell(1).setBackgroundColor(Color.PURPLE);
	}

	@Override
	protected Legend getLegend() {
		// TODO Auto-generated method stub
		return null;
	}

}
