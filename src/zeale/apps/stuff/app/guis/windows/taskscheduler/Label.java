package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.File;
import java.io.FileNotFoundException;

import org.alixia.javalibrary.util.Gateway;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;
import zeale.apps.stuff.api.files.data.Datapiece;

class Label extends Datapiece {

	private static final Gateway<String, Color> STRING_COLOR_GATEWAY = new Gateway<String, Color>() {

		@Override
		public String from(Color value) {
			return (pad(value.getRed()) + pad(value.getGreen()) + pad(value.getBlue()) + pad(value.getOpacity()))
					.toUpperCase();
		}

		private String pad(double val) {
			String in = Integer.toHexString((int) Math.round(val * 255));
			return in.length() == 1 ? "0" + in : in;
		}

		@Override
		public Color to(String value) {
			return Color.web(value);
		}
	};

	public static final Label NULL_LABEL = new Label(null);
	static {
		NULL_LABEL.setName("Unknown...");
	}

	public static Label getNullLabel(String id) {
		Label label = new Label(null, id);
		label.setName("Unknown");
		label.setColor(Color.WHITE);
		label.setDescription(
				"This is a label that replaces other labels, referenced by tasks, which could not be resolved. If a task has a label applied, then that label is deleted externally, the task will still think it has that label applied, but when the program tries to load that task, it won't find the label, so the program will show this label instead. If the label comes back into being later on (i.e., the program finds the label again), then the ghost label will disappear.");
		return label;
	}

	// Yet again, the burden is on the using class (TaskSchedulerWindow) to assure
	// that no "duplicate" Label objects, (i.e. two different Labels that point to
	// the same file), are being used.

	public static Label load(File data) throws FileNotFoundException {
		Label label = new Label(data);
		label.update();
		return label;
	}

	private final StringProperty name = property("name"), id = property("id"), description = property("description");

	private final ObjectProperty<Color> color = oprop("color", STRING_COLOR_GATEWAY);

	private final DoubleProperty opacity = dprop("opacity", 0.5);

	Label(File data) {
		super(data);
	}

	Label(File data, String id) {
		this(data);
		this.id.set(id);
	}

	public final ObjectProperty<Color> colorProperty() {
		return color;
	}

	public final StringProperty descriptionProperty() {
		return description;
	}

	public final Color getColor() {
		return colorProperty().get();
	}

	public final String getDescription() {
		return descriptionProperty().get();
	}

	public final String getId() {
		return idProperty().get();
	}

	public final String getName() {
		return nameProperty().get();
	}

	public final double getOpacity() {
		return opacityProperty().get();
	}

	private final StringProperty idProperty() {
		return id;
	}

	public final StringProperty nameProperty() {
		return name;
	}

	public final DoubleProperty opacityProperty() {
		return opacity;
	}

	public final void setColor(final Color color) {
		colorProperty().set(color);
	}

	public final void setDescription(final String description) {
		descriptionProperty().set(description);
	}

	public final void setName(final String name) {
		nameProperty().set(name);
	}

	public final void setOpacity(final double opacity) {
		opacityProperty().set(opacity);
	}

}
