package zeale.apps.stuff.app.guis.windows.taskscheduler;

import org.alixia.javalibrary.javafx.bindings.BindingTools;

import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import zeale.applicationss.notesss.utilities.Utilities;

class LabelView extends javafx.scene.control.Label {

	public LabelView(Label label) {
		textProperty().bind(label.nameProperty());
		textFillProperty()
				.bind(Bindings.createObjectBinding(
						() -> label.getColor().getBrightness() > 0.5
								|| label.getColor().getOpacity() * label.getOpacity() < 0.5 ? Color.BLACK : Color.WHITE,
						label.colorProperty(), label.opacityProperty()));
		backgroundProperty().bind(Bindings.createObjectBinding(
				() -> Utilities
						.getBackgroundFromColor(label.getColor().interpolate(Color.TRANSPARENT, label.getOpacity())),
				label.colorProperty(), label.opacityProperty()));
		BindingTools.bind(label.colorProperty(),
				v -> Utilities.getBorderFromColor(Color.color(v.getRed(), v.getGreen(), v.getBlue(), 1), 2),
				borderProperty());
	}

}
