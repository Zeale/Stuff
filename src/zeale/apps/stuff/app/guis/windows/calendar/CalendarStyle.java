package zeale.apps.stuff.app.guis.windows.calendar;

abstract class CalendarStyle<W extends CalendarWindow> {
	protected abstract void style(W window);

	protected abstract Legend getLegend();
}
