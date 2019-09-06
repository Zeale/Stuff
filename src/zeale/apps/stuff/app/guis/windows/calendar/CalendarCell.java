package zeale.apps.stuff.app.guis.windows.calendar;

import org.alixia.javalibrary.javafx.bindings.BindingTools;
import org.alixia.javalibrary.util.Box;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

class CalendarCell extends StackPane {
	private final IntegerProperty number = new SimpleIntegerProperty();
	private final Text text = new Text();
	private final static DropShadow shadow = new DropShadow();
	{
		getChildren().add(text);
		setAlignment(text, Pos.TOP_RIGHT);
		text.setStyle("-fx-font-size: 1.4em;-fx-color: egg;");

		text.textProperty().bind(BindingTools.mask(number, Number::toString));

		StackPane.setMargin(text, new Insets(0, 5, 0, 0));// Match inexplicable downward shift.

		Box<String> style = new Box<>();
		setOnMouseEntered(event -> {
			text.setFill(Color.RED);
			setScaleX(1.05);
			setScaleY(1.05);
			setScaleZ(1.05);
			setEffect(shadow);
			style.value = getStyle();
			setStyle("-fx-border-color: -stuff-dark;-fx-background-color: -stuff-light;");
		});
		setOnMouseExited(event -> {
			text.setFill(Color.GOLD);
			setScaleX(1);
			setScaleY(1);
			setScaleZ(1);
			setEffect(null);
			setStyle(style.value);
		});
	}

	public final IntegerProperty numberProperty() {
		return this.number;
	}

	public final int getNumber() {
		return this.numberProperty().get();
	}

	public final void setNumber(final int number) {
		this.numberProperty().set(number);
	}

}
