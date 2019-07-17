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

	public ObservableList<Label> getLabels() {
		return labels;
	}

	public void addLabel(Label label) {
		labels.add(label);
	}

	public void removeLabel(Label label) {
		labels.remove(label);
	}

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
			public Label to(String value) {
				return labelObtainer.apply(value);
			}

			@Override
			public String from(Label value) {
				return value.getId();
			}
		}, FXCollections.observableArrayList());
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
