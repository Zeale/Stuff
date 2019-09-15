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

	public static final double DEFAULT_CELL_BACKGROUND_OPACITY = 0.2;
	private static final Color DEFAULT_DISABLED_CELL_BACKGROUND_COLOR = new Color(0.6, 0, 0,
			DEFAULT_CELL_BACKGROUND_OPACITY);
	private static final Background DEFAULT_DISABLED_CELL_BACKGROUND = Utilities
			.getBackgroundFromColor(DEFAULT_DISABLED_CELL_BACKGROUND_COLOR);

	private final IntegerProperty number = new SimpleIntegerProperty(), eventCount = new SimpleIntegerProperty(),
			taskCount = new SimpleIntegerProperty();

	private final Text numberText = new Text();
	{
		getChildren().add(numberText);
		setAlignment(numberText, Pos.TOP_RIGHT);
		numberText.setStyle("-fx-font-size: 1.4em;-fx-color: egg;");
		numberText.textProperty().bind(BindingTools.mask(number, Number::toString));
		Font font = Font.font("monospace");
		class NumberBubble extends StackPane {
			private final Text text = new Text();
			private final Rectangle box = new Rectangle();
			{

				text.setBoundsType(TextBoundsType.VISUAL);
				text.setFont(font);

				box.strokeProperty().bind(numberText.fillProperty());
				box.setFill(Color.TRANSPARENT);
				text.fillProperty().bind(box.strokeProperty());
				getChildren().setAll(box, text);

			}

			public NumberBubble(Pos alignment, ObservableValue<? extends Number> observable) {
				setAlignment(this, alignment);
				observable.addListener((ChangeListener<Number>) (obs, oldValue, newValue) -> {
					if (newValue.intValue() == 0)
						CalendarCell.this.getChildren().remove(this);
					else {
						String str = newValue.toString();
						text.setText(str);
						Pair<Double, Double> size = getSize(str, text.getFont());

						box.setWidth(Math.max(size.first + 8, size.second + 6));
						box.setHeight(size.second + 6);

						setMaxSize(box.getWidth() + 2, box.getHeight() + 2);

						box.setArcHeight(5);
						box.setArcWidth(5);
						if (!CalendarCell.this.getChildren().contains(this))
							CalendarCell.this.getChildren().add(this);
					}
				});
			}
		}

		new NumberBubble(Pos.BOTTOM_LEFT, eventCount);
		new NumberBubble(Pos.BOTTOM_RIGHT, taskCount);

		StackPane.setMargin(numberText, new Insets(0, 5, 0, 0));// Match inexplicable downward shift.

		Box<Background> bg = new Box<>();
		setOnMouseEntered(event -> {
			numberText.setFill(Color.RED);
			bg.value = getBackground();
			setBackgroundColor(Color.ORANGE);
		});
		setOnMouseExited(event -> {
			numberText.setFill(Color.GOLD);
			setBackground(bg.value);
		});
		disabledProperty().addListener(
				(observable, oldValue, newValue) -> setBackground(newValue ? DEFAULT_DISABLED_CELL_BACKGROUND : null));
	}

	public final IntegerProperty eventCountProperty() {
		return this.eventCount;
	}

	public final int getEventCount() {
		return this.eventCountProperty().get();
	}

	public final int getNumber() {
		return this.numberProperty().get();
	}

	public final IntegerProperty numberProperty() {
		return this.number;
	}

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

	public final void setEventCount(final int eventCount) {
		this.eventCountProperty().set(eventCount);
	}

	public final void setNumber(final int number) {
		this.numberProperty().set(number);
	}

	private static Pair<Double, Double> getSize(String txt, Font font) {
		Text text = new Text(txt);
		text.setFont(font);
		new Scene(new Group(text));
		text.applyCss();
		Bounds bounds = text.getLayoutBounds();
		return new Pair<>(bounds.getWidth(), bounds.getHeight());
	}

	public final IntegerProperty taskCountProperty() {
		return this.taskCount;
	}
	

	public final int getTaskCount() {
		return this.taskCountProperty().get();
	}
	

	public final void setTaskCount(final int taskCount) {
		this.taskCountProperty().set(taskCount);
	}
	
}
