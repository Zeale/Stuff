package zeale.apps.stuff.app.guis.windows.calendar;

abstract class CalendarView<W extends CalendarWindow> {

	protected abstract void style(W window);

	protected abstract Legend getLegend();
}
