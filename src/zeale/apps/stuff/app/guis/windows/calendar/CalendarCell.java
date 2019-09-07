package zeale.apps.stuff.app.guis.windows.calendar;

import org.alixia.javalibrary.javafx.bindings.BindingTools;
import org.alixia.javalibrary.util.Box;
import org.alixia.javalibrary.util.Pair;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import zeale.applicationss.notesss.utilities.Utilities;

class CalendarCell extends StackPane {
	private final IntegerProperty number = new SimpleIntegerProperty(), eventCount = new SimpleIntegerProperty();
	private final Text numberText = new Text(), eventText = new Text();
	private final static Effect DEFAULT_HOVER_EFFECT = null;

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
		setBackgroundColorStrict(color.deriveColor(0, 1, 1, 0.2));
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

	private static Pair<Double, Double> getSize(String txt, Font font) {
		Text text = new Text(txt);
		text.setFont(font);
		new Scene(new Group(text));
		text.applyCss();
		Bounds bounds = text.getLayoutBounds();
		return new Pair<>(bounds.getWidth(), bounds.getHeight());
	}

	{
		getChildren().add(numberText);
		setAlignment(numberText, Pos.TOP_RIGHT);
		numberText.setStyle("-fx-font-size: 1.4em;-fx-color: egg;");

		numberText.textProperty().bind(BindingTools.mask(number, Number::toString));

		eventText.setBoundsType(TextBoundsType.VISUAL);
		eventText.setFont(Font.font("monospace"));
		Rectangle box = new Rectangle();
		box.strokeProperty().bind(numberText.fillProperty());
		box.setFill(Color.TRANSPARENT);
		eventText.fillProperty().bind(box.strokeProperty());

		StackPane eventBox = new StackPane(box, eventText);
		StackPane.setAlignment(eventBox, Pos.BOTTOM_LEFT);
		eventCount.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue.intValue() == 0)
					getChildren().remove(eventBox);
				else {
					String str = newValue.toString();
					eventText.setText(str);
					Pair<Double, Double> size = getSize(str, eventText.getFont());

					box.setWidth(Math.max(size.first + 8, size.second + 6));
					box.setHeight(size.second + 6);

					eventBox.setMaxSize(box.getWidth() + 2, box.getHeight() + 2);

					box.setArcHeight(5);
					box.setArcWidth(5);
					if (!getChildren().contains(eventBox))
						getChildren().add(eventBox);
				}
			}
		});

		StackPane.setMargin(numberText, new Insets(0, 5, 0, 0));// Match inexplicable downward shift.

		Box<Background> bg = new Box<>();
		setOnMouseEntered(event -> {
			numberText.setFill(Color.RED);
			setEffect(DEFAULT_HOVER_EFFECT);
			bg.value = getBackground();
			setBackgroundColor(Color.ORANGE);
		});
		setOnMouseExited(event -> {
			numberText.setFill(Color.GOLD);
			setEffect(null);
			setBackground(bg.value);
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
