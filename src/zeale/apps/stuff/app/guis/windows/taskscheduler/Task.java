package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.function.Function;

import org.alixia.javalibrary.util.Gateway;
import org.alixia.javalibrary.util.StringGateway;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import zeale.apps.stuff.api.files.data.Datapiece;

class Task extends Datapiece {
	public static Task load(File file, Function<? super String, ? extends Label> labelObtainer)
			throws FileNotFoundException {
		Task task = new Task(file, labelObtainer);
		task.update();
		return task;
	}

	private final StringProperty name = property("name"), description = property("description");
	private final BooleanProperty completed = bprop("completed"), urgent = bprop("urgent");
	private final ObjectProperty<Instant> dueDate = oprop("due-date", (StringGateway<Instant>) Instant::parse);
	private final ObservableList<Label> labels;

	/**
	 * Creates a new {@link Task} in memory. No writing or reading operations occur
	 * when this is called.
	 *
	 * @param data          The location of this {@link Task}.
	 * @param labelObtainer The label obtainer, used to obtain loaded labels that
	 *                      are specified in this {@link Task}'s datafile when this
	 *                      {@link Task} is updated from the disk.
	 */
	Task(File data, Function<? super String, ? extends Label> labelObtainer) {
		super(data);
		labels = lprop("labels", new Gateway<String, Label>() {

			@Override
			public String from(Label value) {
				return value.getId();
			}

			@Override
			public Label to(String value) {
				return labelObtainer.apply(value);
			}
		}, FXCollections.observableArrayList());
	}

	public void addLabel(Label label) {
		labels.add(label);
	}

	public final BooleanProperty completedProperty() {
		return completed;
	}

	public final StringProperty descriptionProperty() {
		return description;
	}

	public final ObjectProperty<Instant> dueDateProperty() {
		return dueDate;
	}

	public final String getDescription() {
		return descriptionProperty().get();
	}

	public final Instant getDueDate() {
		return dueDateProperty().get();
	}

	public ObservableList<Label> getLabels() {
		return labels;
	}

	public final String getName() {
		return nameProperty().get();
	}

	public final boolean isCompleted() {
		return completedProperty().get();
	}

	public final boolean isUrgent() {
		return urgentProperty().get();
	}

	public final StringProperty nameProperty() {
		return name;
	}

	public void removeLabel(Label label) {
		labels.remove(label);
	}

	public final void setCompleted(final boolean completed) {
		completedProperty().set(completed);
	}

	public final void setDescription(final String description) {
		descriptionProperty().set(description);
	}

	public final void setDueDate(final Instant dueDate) {
		dueDateProperty().set(dueDate);
	}

	public final void setName(final String name) {
		nameProperty().set(name);
	}

	public final void setUrgent(final boolean urgent) {
		urgentProperty().set(urgent);
	}

	public final BooleanProperty urgentProperty() {
		return urgent;
	}

}
