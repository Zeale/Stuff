package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.File;
import java.io.FileNotFoundException;

import org.alixia.javalibrary.util.Gateway;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import zeale.apps.stuff.api.files.data.Datapiece;

class Label extends Datapiece {

	private static final Gateway<String, Color> STRING_COLOR_GATEWAY = new Gateway<String, Color>() {

		@Override
		public Color to(String value) {
			return Color.web(value);
		}

		private String pad(double val) {
			String in = Integer.toHexString((int) Math.round(val * 255));
			return in.length() == 1 ? "0" + in : in;
		}

		@Override
		public String from(Color value) {
			return (pad(value.getRed()) + pad(value.getGreen()) + pad(value.getBlue()) + pad(value.getOpacity()))
					.toUpperCase();
		}
	};

	private final StringProperty name = property("name"), id = property("id");
	private final ObjectProperty<Color> color = oprop("color", STRING_COLOR_GATEWAY);

	// Yet again, the burden is on the using class (TaskSchedulerWindow) to assure
	// that no "duplicate" Label objects, (i.e. two different Labels that point to
	// the same file), are being used.

	public static Label load(File data) throws FileNotFoundException {
		Label label = new Label(data);
		label.update();
		return label;
	}

	Label(File data, String id) {
		this(data);
		this.id.set(id);
	}

	Label(File data) {
		super(data);
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

	public final ObjectProperty<Color> colorProperty() {
		return this.color;
	}

	public final Color getColor() {
		return this.colorProperty().get();
	}

	public final void setColor(final Color color) {
		this.colorProperty().set(color);
	}

	private final StringProperty idProperty() {
		return this.id;
	}

	public final String getId() {
		return this.idProperty().get();
	}

}
