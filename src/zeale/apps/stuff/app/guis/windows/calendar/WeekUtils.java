package zeale.apps.stuff.app.guis.windows.calendar;

import java.time.DayOfWeek;

final class WeekUtils {
	private WeekUtils() {}

	static int weekdayToIndex(DayOfWeek day) {
		return ordinalToIndex(day.ordinal());
	}

	static int weekdayToIndex(int dayOfWeekIndex) {
		return ordinalToIndex(dayOfWeekIndex - 1);
	}

	static int ordinalToIndex(int ordinal) {
		return (ordinal + 1) % 7;
	}

	static DayOfWeek indexToWeekday(int index) {
		return DayOfWeek.of((index + 6) % 7 + 1);
	}
	
}
