package zeale.apps.stuff.api.javafx.guis.windows.calculator;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

public class TaggedCalculatorButton extends Button {

	private final StringProperty tag = new SimpleStringProperty();
	private final BooleanProperty tooltipBound = new SimpleBooleanProperty(true);

	{

		getStyleClass().add("pop-button");
		ChangeListener<String> tagChangeListener = (observable, oldValue,
				newValue) -> setTooltip(new Tooltip(getTag()));

		tooltipBound.addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
			if (newValue)
				tag.addListener(tagChangeListener);
			else
				tag.removeListener(tagChangeListener);
		});
		tag.addListener(tagChangeListener);
		
	}

	public StringProperty tagProperty() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag.set(tag);
	}

	public String getTag() {
		return tag.get();
	}
}
