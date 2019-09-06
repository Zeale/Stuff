package zeale.apps.stuff.app.guis.windows.calendar;

import org.alixia.javalibrary.javafx.bindings.BindingTools;
import org.alixia.javalibrary.util.Box;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import zeale.applicationss.notesss.utilities.Utilities;

class CalendarCell extends StackPane {
	private final IntegerProperty number = new SimpleIntegerProperty();
	private final Text text = new Text();
	private final static DropShadow shadow = new DropShadow();

	public static final double DEFAULT_CELL_BACKGROUND_OPACITY = 0.2;

	private static final Color DEFAULT_DISABLED_CELL_BACKGROUND_COLOR = new Color(0.6, 0, 0,
			DEFAULT_CELL_BACKGROUND_OPACITY);
	private static final Background DEFAULT_DISABLED_CELL_BACKGROUND = Utilities
			.getBackgroundFromColor(DEFAULT_DISABLED_CELL_BACKGROUND_COLOR);

	/**
	 * Sets the background color of this {@link CalendarCell} to be the given color
	 * <em>with an opacity of <code>0.2</code></em>.
	 * 
	 * @param color The {@link Color} to set this {@link CalendarCell}'s background
	 *              to.
	 */
	public void setBackgroundColor(Color color) {
		setBackgroundColorStrict(color.deriveColor(0, 0, 0, 0.2));
	}

	/**
	 * Sets the background of this {@link CalendarCell} to be a {@link Background}
	 * derived from the given {@link Paint}.
	 * 
	 * @param color The {@link Paint} to make the background of this
	 *              {@link CalendarCell}.
	 */
	public void setBackgroundColorStrict(Paint color) {
		setBackground(Utilities.getBackgroundFromColor(color));
	}

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
		disabledProperty().addListener(
				(observable, oldValue, newValue) -> setBackground(newValue ? DEFAULT_DISABLED_CELL_BACKGROUND : null));
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
