package zeale.apps.stuff.api.files.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.alixia.javalibrary.util.Gateway;
import org.alixia.javalibrary.util.StringGateway;

import branch.alixia.unnamed.Datamap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
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
			super.write(writer);
			for (Entry<String, Supplier<String>> e : converters.entrySet())
				write(writer, e.getKey(), e.getValue().get());
		}

	}

	protected final DatapieceMap datamap = new DatapieceMap();
	private final Map<String, Supplier<String>> converters = new HashMap<>();
	protected final File data;
	protected static final StringGateway<Boolean> BOOLEAN_STRING_GATEWAY = Boolean::valueOf;

	private final Map<String, Consumer<String>> updateHandlers = new HashMap<>();

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

//	private static <T> String toString(Collection<T> items, Function<? super T, String> converter) {
//		String result = "[";
//		Iterator<T> itr = items.iterator();
//		if (itr.hasNext()) {
//			result += converter.apply(itr.next()).replace("\\", "\\\\").replace(",", "\\,").replace("]", "\\]");
//			while (itr.hasNext())
//				result += ','
//						+ converter.apply(itr.next()).replace("\\", "\\\\").replace(",", "\\,").replace("]", "\\]");
//		}
//		return result + ']';
//	}
//
//	protected <T, LT extends ObservableList<T>> LT lprop(String name, Gateway<String, ? super T> gateway, LT list) {
//		list.addListener((InvalidationListener) observable -> put(name, Datapiece.toString(list, gateway.to())));
//		if (datamap.containsKey(name)) {
//			String value = name;
//			if (!value.isEmpty()) {
//				StringBuffer item = new StringBuffer();
//				for (int i = 0; i < value.length(); i++) {
//					char c = 
//				}
//			}
//		}
//		return list;
//	}

	protected BooleanProperty bprop(String name) {
		return bprop(name, BOOLEAN_STRING_GATEWAY);
	}

	private void rem(String key) {
		datamap.remove(key);
	}

	private void put(String key, String value) {
		datamap.put(key, value);
	}

	public Datapiece(File file) {
		data = file;
	}

	public File getData() {
		return data;
	}

	protected final DatapieceMap readDatamap(File file) throws FileNotFoundException {
		DatapieceMap map = new DatapieceMap();
		map.update(new FileInputStream(file));
		return map;
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
	 * Updates this {@link Datapiece} from its {@link File}.
	 * 
	 * @throws FileNotFoundException In case reading from the {@link #getData()
	 *                               data} object fails.
	 */
	public void update() throws FileNotFoundException {
		datamap.update(new FileInputStream(data));
		refreshProps();
	}

	protected final void refreshProps() {
		for (Entry<String, String> e : datamap.entrySet())
			if (updateHandlers.containsKey(e.getKey()))
				updateHandlers.get(e.getKey()).accept(e.getValue());
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