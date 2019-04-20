package zeale.apps.stuff.api.javafx.guis.windows.calculator;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;

public class TaggedCalculatorButton extends Button {

	{
		getStyleClass().add("pop-button");
	}

	private final StringProperty tag = new SimpleStringProperty();

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
