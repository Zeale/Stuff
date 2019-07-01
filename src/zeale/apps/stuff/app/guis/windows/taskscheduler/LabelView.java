package zeale.apps.stuff.app.guis.windows.taskscheduler;

import org.alixia.javalibrary.javafx.bindings.BindingTools;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

class LabelView extends javafx.scene.control.Label {
	private static final BorderWidths DEFAULT_BORDER_WIDTH = new BorderWidths(2);
	private static final CornerRadii DEFAULT_BORDER_RADIUS = new CornerRadii(4);
	private static final double DEFAULT_BRIGHTNESS_THRESHOLD = 0.7, DEFAULT_OPACITY_THRESHOLD = 0.5;

	private final Label label;

	public Label getLabel() {
		return label;
	}

	{
		setPadding(new Insets(10));
	}

	public LabelView(Label label) {
		textProperty().bind((this.label = label).nameProperty());
		textFillProperty()
				.bind(Bindings.createObjectBinding(() -> label.getColor().getBrightness() > DEFAULT_BRIGHTNESS_THRESHOLD
						|| label.getColor().getOpacity() * label.getOpacity() < DEFAULT_OPACITY_THRESHOLD ? Color.BLACK
								: Color.WHITE,
						label.colorProperty(), label.opacityProperty()));
		backgroundProperty().bind(Bindings.createObjectBinding(() -> new Background(new BackgroundFill(
				label.getColor().interpolate(Color.TRANSPARENT, label.getOpacity()), DEFAULT_BORDER_RADIUS, null)),
				label.colorProperty(), label.opacityProperty()));
		BindingTools
				.bind(label.colorProperty(),
						v -> new Border(new BorderStroke(Color.color(v.getRed(), v.getGreen(), v.getBlue(), 1),
								BorderStrokeStyle.SOLID, DEFAULT_BORDER_RADIUS, DEFAULT_BORDER_WIDTH)),
						borderProperty());
	}

}
