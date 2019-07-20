package zeale.apps.stuff.api.files.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.alixia.javalibrary.util.Gateway;
import org.alixia.javalibrary.util.StringGateway;

import branch.alixia.unnamed.Datamap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.tools.api.data.files.filesystem.storage.FileStorage.Data;

public class Datapiece {

	protected class DatapieceMap extends Datamap {

		/**
		 * SUID
		 */
		private static final long serialVersionUID = 1L;

		public DatapieceMap() {
		}

		@Override
		protected void write(PrintWriter writer) {
			Collection<String> coveredKeys = new HashSet<>();
			for (Entry<String, String> e : entrySet())
				if (converters.containsKey(e.getKey())) {
					write(writer, e.getKey(), converters.get(e.getKey()).get());
					coveredKeys.add(e.getKey());
				} else
					write(writer, e.getKey(), e.getValue());
			for (Entry<String, Supplier<String>> e : converters.entrySet())
				if (!coveredKeys.contains(e.getKey()))
					write(writer, e.getKey(), e.getValue().get());
		}

	}

	protected static final StringGateway<Boolean> BOOLEAN_STRING_GATEWAY = Boolean::valueOf;
	protected static final StringGateway<Double> DOUBLE_STRING_GATEWAY = Double::valueOf;

	private static <T> String toString(Collection<? extends T> items, Function<? super T, String> converter) {
		String result = "";
		Iterator<? extends T> itr = items.iterator();
		if (itr.hasNext()) {
			result += converter.apply(itr.next()).replace("\\", "\\\\").replace(",", "\\,");
			while (itr.hasNext())
				result += ',' + converter.apply(itr.next()).replace("\\", "\\\\").replace(",", "\\,");
		}
		return result;
	}

	protected final DatapieceMap datamap = new DatapieceMap();
	private final Map<String, Supplier<String>> converters = new HashMap<>();

	protected final File data;

	private final Map<String, Consumer<String>> updateHandlers = new HashMap<>();

	public Datapiece(File file) {
		data = file;
	}

	protected BooleanProperty bprop(String name) {
		return bprop(name, BOOLEAN_STRING_GATEWAY);
	}

	protected BooleanProperty bprop(String name, Gateway<String, Boolean> gateway) {
		BooleanProperty prop = new SimpleBooleanProperty(this, name);
		prop.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (newValue == null)
				rem(name);
			else
				put(name, gateway.from(newValue));
		});
		updateHandlers.put(name, s -> prop.set(gateway.to(s)));
		return prop;
	}

	public void deleteFile() {
		data.delete();
	}

	protected DoubleProperty dprop(String name) {
		return dprop(name, DOUBLE_STRING_GATEWAY);
	}

	protected DoubleProperty dprop(String name, double defaultValue) {
		DoubleProperty prop = dprop(name);
		prop.set(defaultValue);
		return prop;
	}

	protected DoubleProperty dprop(String name, Gateway<String, Double> gateway) {
		DoubleProperty prop = new SimpleDoubleProperty(this, name);
		prop.addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
			if (newValue == null)
				rem(name);
			else
				put(name, gateway.from(newValue.doubleValue()));
		});
		updateHandlers.put(name, s -> prop.set(gateway.to(s)));
		return prop;
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

	/**
	 * Returns a {@link Map} of the converters for this {@link Datapiece}. Whenever
	 * the {@link Datapiece} is being written, each of these converters is
	 * automatically called after the normal values in the {@link #datamap} are
	 * written, and the values returned by the {@link Supplier}s of these converters
	 * are written out with the key of these converters as the key.
	 *
	 * @return The editable {@link Map} of converters.
	 */
	protected Map<String, Supplier<String>> getConverters() {
		return converters;
	}

	public File getData() {
		return data;
	}

	/**
	 * Returns the {@link Map} of update handlers which are called when an update is
	 * made to the given property by one of either {@link #reload()} or
	 * {@link #update()}.
	 *
	 * @return The editable {@link Map} of update handlers.
	 */
	protected Map<String, Consumer<String>> getUpdateHandlers() {
		return updateHandlers;
	}

	protected <T, LT extends ObservableList<T>> LT lprop(String name, Gateway<String, T> gateway, LT list) {

		updateHandlers.put(name, t -> {
			list.clear();
			if (!t.isEmpty()) {
				StringBuffer item = new StringBuffer();
				boolean escaped = false;
				for (int i = 0; i < t.length(); i++) {
					char c = t.charAt(i);
					if (c == '\\')
						if (escaped)
							item.append('\\');
						else {
							escaped = true;
							continue;
						}
					else if (c == ',')
						if (escaped)
							item.append(',');
						else {
							list.add(gateway.to(item.toString()));
							item = new StringBuffer();
						}
					else
						item.append(c);
					escaped = false;
				}
				if (item.length() != 0)
					list.add(gateway.to(item.toString()));
			}
		});
		converters.put(name, () -> Datapiece.toString(list, gateway.to()));
		return list;
	}

	protected <T> ObjectProperty<T> oprop(String name, Gateway<String, T> gateway) {
		ObjectProperty<T> prop = new SimpleObjectProperty<>(this, name);
		prop.addListener((ChangeListener<T>) (observable, oldValue, newValue) -> {
			if (newValue == null)
				rem(name);
			else
				put(name, gateway.from(newValue));
		});
		updateHandlers.put(name, s -> prop.set(gateway.to(s)));
		return prop;
	}

	protected StringProperty property(String name) {
		SimpleStringProperty prop = new SimpleStringProperty(this, name);
		prop.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (newValue == null)
				rem(name);
			else
				put(name, newValue);
		});
		updateHandlers.put(name, prop::set);
		return prop;
	}

	private void put(String key, String value) {
		datamap.put(key, value);
		update(key, value);
	}

	protected final DatapieceMap readDatamap(File file) throws FileNotFoundException {
		DatapieceMap map = new DatapieceMap();
		map.update(new FileInputStream(file));
		return map;
	}

	protected final void refreshProps() {
		for (Entry<String, String> e : datamap.entrySet())
			update(e.getKey(), e.getValue());
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

	private void rem(String key) {
		datamap.remove(key);
		update(key, null);
	}

	/**
	 * Updates this {@link Datapiece} from its {@link File}.
	 *
	 * @throws FileNotFoundException In case reading from the {@link #getData()
	 *                               data} object fails.
	 */
	public void update() throws FileNotFoundException {
		datamap.update(new FileInputStream(data));
		refreshProps();
	}

	private final void update(String key, String value) {
		if (updateHandlers.containsKey(key))
			try {
				updateHandlers.get(key).accept(value);
			} catch (RuntimeException e) {
				Logging.err(e);
			}
	}

}