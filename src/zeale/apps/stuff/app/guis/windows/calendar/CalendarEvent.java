package zeale.apps.stuff.app.guis.windows.calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.alixia.javalibrary.util.Gateway;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import zeale.apps.stuff.api.files.data.Datapiece;

public class CalendarEvent extends Datapiece {

	public static CalendarEvent load(File file) throws FileNotFoundException {
		CalendarEvent event = new CalendarEvent(file);
		event.update();
		return event;
	}

	public CalendarEvent(File file) {
		super(file);
	}

	private final StringProperty name = property("name"), description = property("description");

	private final ObjectProperty<LocalDate> date = oprop("date", new Gateway<String, LocalDate>() {

		@Override
		public LocalDate to(String value) {
			return LocalDate.parse(value);
		}

		@Override
		public String from(LocalDate value) {
			return value.format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
	});
	private final Gateway<String, LocalTime> timeStringGateway = new Gateway<String, LocalTime>() {

		@Override
		public LocalTime to(String value) {
			return LocalTime.parse(value);
		}

		@Override
		public String from(LocalTime value) {
			return value.format(DateTimeFormatter.ISO_LOCAL_TIME);
		}
	};
	private final ObjectProperty<LocalTime> time = oprop("time", timeStringGateway),
			endTime = oprop("end-time", timeStringGateway);

	public final StringProperty nameProperty() {
		return this.name;
	}

	public final String getName() {
		return this.nameProperty().get();
	}

	public final void setName(final String name) {
		this.nameProperty().set(name);
	}

	public final StringProperty descriptionProperty() {
		return this.description;
	}

	public final String getDescription() {
		return this.descriptionProperty().get();
	}

	public final void setDescription(final String description) {
		this.descriptionProperty().set(description);
	}

	public final ObjectProperty<LocalDate> dateProperty() {
		return this.date;
	}

	public final LocalDate getDate() {
		return this.dateProperty().get();
	}

	public final void setDate(final LocalDate date) {
		this.dateProperty().set(date);
	}

	public final ObjectProperty<LocalTime> timeProperty() {
		return this.time;
	}

	public final LocalTime getTime() {
		return this.timeProperty().get();
	}

	public final void setTime(final LocalTime time) {
		this.timeProperty().set(time);
	}

	public final ObjectProperty<LocalTime> endTimeProperty() {
		return this.endTime;
	}

	public final LocalTime getEndTime() {
		return this.endTimeProperty().get();
	}

	public final void setEndTime(final LocalTime endTime) {
		this.endTimeProperty().set(endTime);
	}

}
