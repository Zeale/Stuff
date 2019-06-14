package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.alixia.javalibrary.util.StringGateway;

import branch.alixia.unnamed.Datamap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import zeale.apps.tools.api.data.files.filesystem.storage.FileStorage.Data;

class Task {
	private final Datamap datamap;

	private StringProperty property(String name) {
		SimpleStringProperty prop = new SimpleStringProperty(this, name);
		prop.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (newValue == null)
				rem(name);
			else
				put(name, newValue);
		});
		if (datamap.containsKey(name))
			prop.set(datamap.get(name));
		return prop;
	}

	private BooleanProperty bprop(String name, StringGateway<Boolean> gateway) {
		BooleanProperty prop = new SimpleBooleanProperty(this, name);
		prop.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (newValue == null)
				rem(name);
			else
				put(name, gateway.to(newValue));
		});
		if (datamap.containsKey(name))
			prop.set(gateway.from(datamap.get(name)));
		return prop;
	}

	private BooleanProperty bprop(String name) {
		return bprop(name, Boolean::valueOf);
	}

	public static Task load(Data file) throws FileNotFoundException {
		return new Task(Datamap.read(new FileInputStream(file.getFile())), file);
	}

	public static void save(Task task) throws FileNotFoundException {
		Datamap.save(task.datamap, new FileOutputStream(task.data.getFile()));
	}

	private final StringProperty name, description;
	private final BooleanProperty completed, urgent;

	private void rem(String key) {
		datamap.remove(key);
	}

	private void put(String key, String value) {
		datamap.put(key, value);
	}

	private Task(Datamap datamap, Data data) {
		this.datamap = datamap;
		this.data = data;
		name = property("name");
		description = property("description");
		completed = bprop("completed");
		urgent = bprop("urgent");
	}

	private final Data data;

	public Data getData() {
		return data;
	}

	/**
	 * Clears this {@link Task}'s data and then calls {@link #update()}. This
	 * effectively discards any data in this {@link Task} and then loads in any data
	 * from the file.
	 * 
	 * @throws FileNotFoundException In case {@link #update()} throws a
	 *                               {@link FileNotFoundException}.
	 */
	public void reload() throws FileNotFoundException {
		datamap.clear();
		update();
	}

	/**
	 * Loads any new key-value pairs of data in this {@link Task}'s
	 * {@link #getData() Data object} but not contained by this {@link Task}'s
	 * {@link #datamap} already, into this {@link Task}'s {@link #datamap}.
	 * 
	 * @throws FileNotFoundException In case reading from the {@link #getData()
	 *                               data} object fails.
	 */
	public void update() throws FileNotFoundException {
		datamap.update(new FileInputStream(data.getFile()));
	}

	/**
	 * Writes this {@link Task} out to its {@link Data} file.
	 * 
	 * @throws FileNotFoundException In case the {@link Data} for this {@link Task}
	 *                               could not be written to.
	 */
	public void flush() throws FileNotFoundException {
		Datamap.save(datamap, new FileOutputStream(data.getFile()));
	}

	Task(Data data) {
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

}
