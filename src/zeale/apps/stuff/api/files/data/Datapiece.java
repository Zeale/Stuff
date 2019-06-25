package zeale.apps.stuff.api.files.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

import org.alixia.javalibrary.util.Gateway;
import org.alixia.javalibrary.util.StringGateway;

import branch.alixia.unnamed.Datamap;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import zeale.apps.tools.api.data.files.filesystem.storage.FileStorage.Data;

public class Datapiece {

	protected final Datamap datamap;
	protected final File data;
	private static final StringGateway<Boolean> BOOLEAN_STRING_GATEWAY = Boolean::valueOf;

	protected StringProperty property(String name) {
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

	protected BooleanProperty bprop(String name, Gateway<String, Boolean> gateway) {
		BooleanProperty prop = new SimpleBooleanProperty(this, name);
		prop.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (newValue == null)
				rem(name);
			else
				put(name, gateway.from(newValue));
		});
		if (datamap.containsKey(name))
			prop.set(gateway.to(datamap.get(name)));
		return prop;
	}

	protected <T> ObjectProperty<T> oprop(String name, Gateway<String, T> gateway) {
		ObjectProperty<T> prop = new SimpleObjectProperty<>(this, name);
		prop.addListener((ChangeListener<T>) (observable, oldValue, newValue) -> {
			if (newValue == null)
				rem(name);
			else
				put(name, gateway.from(newValue));
		});
		if (datamap.containsKey(name))
			prop.set(gateway.to(datamap.get(name)));
		return prop;
	}

	private static <T> String toString(Collection<T> items, Function<? super T, String> converter) {
		String result = "[";
		Iterator<T> itr = items.iterator();
		if (itr.hasNext()) {
			result += converter.apply(itr.next()).replace("\\", "\\\\").replace(",", "\\,").replace("]", "\\]");
			while (itr.hasNext())
				result += ','
						+ converter.apply(itr.next()).replace("\\", "\\\\").replace(",", "\\,").replace("]", "\\]");
		}
		return result + ']';
	}

	protected <T, LT extends ObservableList<T>> LT lprop(String name, Gateway<String, ? super T> gateway, LT list) {
		list.addListener((InvalidationListener) observable -> put(name, Datapiece.toString(list, gateway.to())));
		if (datamap.containsKey(name)) {
			// TODO Load map from String.
		}
		return list;
	}

	protected BooleanProperty bprop(String name) {
		return bprop(name, BOOLEAN_STRING_GATEWAY);
	}

	private void rem(String key) {
		datamap.remove(key);
	}

	private void put(String key, String value) {
		datamap.put(key, value);
	}

	public Datapiece(Datamap map, File data) {
		datamap = map;
		this.data = data;
	}

	public File getData() {
		return data;
	}

	protected static final Datamap readDatamap(File file) throws FileNotFoundException {
		return Datamap.readLax(new FileInputStream(file));
	}

	/**
	 * Clears this {@link Datapiece}'s data and then calls {@link #update()}. This
	 * effectively discards any data in this {@link Datapiece} and then loads in any
	 * data from the file.
	 * 
	 * @throws FileNotFoundException In case {@link #update()} throws a
	 *                               {@link FileNotFoundException}.
	 */
	public void reload() throws FileNotFoundException {
		datamap.clear();
		update();
	}

	/**
	 * Loads any new key-value pairs of data in this {@link Datapiece}'s
	 * {@link #getData() Data object} but not contained by this {@link Datapiece}'s
	 * {@link #datamap} already, into this {@link Datapiece}'s {@link #datamap}.
	 * 
	 * @throws FileNotFoundException In case reading from the {@link #getData()
	 *                               data} object fails.
	 */
	public void update() throws FileNotFoundException {
		datamap.update(new FileInputStream(data));
	}

	/**
	 * Writes this {@link Datapiece} out to its {@link Data} file.
	 * 
	 * @throws FileNotFoundException In case the {@link Data} for this
	 *                               {@link Datapiece} could not be written to.
	 */
	public void flush() throws FileNotFoundException {
		Datamap.save(datamap, new FileOutputStream(data));
	}

}