package zeale.apps.stuff.app.guis.windows.taskscheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

import org.alixia.javalibrary.util.Gateway;
import org.alixia.javalibrary.util.StringGateway;

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

	private final StringProperty name = property("name");
	private final ObjectProperty<Color> color = oprop("color", STRING_COLOR_GATEWAY);
	private final ObjectProperty<UUID> id = oprop("id", (StringGateway<UUID>) a -> UUID.fromString(a));

	public static Label load(File data) throws FileNotFoundException {
		Label label = new Label(data);
		label.update();
		return label;
	}

	private Label(File data) {
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

	private final ObjectProperty<UUID> idProperty() {
		return this.id;
	}

	public final UUID getId() {
		return this.idProperty().get();
	}

}
