package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;

import org.alixia.javalibrary.util.StringGateway;

import branch.alixia.unnamed.Datamap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import zeale.apps.stuff.api.files.data.Datapiece;

class Task extends Datapiece {
	public static Task load(File file) throws FileNotFoundException {
		return new Task(readDatamap(file), file);
	}

	private final StringProperty name, description;
	private final BooleanProperty completed, urgent;
	private final ObjectProperty<Instant> dueDate;

	private Task(Datamap datamap, File data) {
		super(datamap, data);
		name = property("name");
		description = property("description");
		completed = bprop("completed");
		urgent = bprop("urgent");
		dueDate = oprop("due-date", (StringGateway<Instant>) Instant::parse);
	}

	Task(File data) {
		this(new Datamap(), data);
	}

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

	public final BooleanProperty completedProperty() {
		return this.completed;
	}

	public final boolean isCompleted() {
		return this.completedProperty().get();
	}

	public final void setCompleted(final boolean completed) {
		this.completedProperty().set(completed);
	}

	public final BooleanProperty urgentProperty() {
		return this.urgent;
	}

	public final boolean isUrgent() {
		return this.urgentProperty().get();
	}

	public final void setUrgent(final boolean urgent) {
		this.urgentProperty().set(urgent);
	}

	public final ObjectProperty<Instant> dueDateProperty() {
		return this.dueDate;
	}

	public final Instant getDueDate() {
		return this.dueDateProperty().get();
	}

	public final void setDueDate(final Instant dueDate) {
		this.dueDateProperty().set(dueDate);
	}

}
