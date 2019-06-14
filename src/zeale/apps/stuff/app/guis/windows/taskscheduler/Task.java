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

	Task(Data data) {
		this(new Datamap(), data);
	}
}
