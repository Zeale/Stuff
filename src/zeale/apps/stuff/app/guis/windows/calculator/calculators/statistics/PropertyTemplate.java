package zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

interface PropertyTemplate<E extends Enum<E>> {
	String getName();

	default void set(String val, Property... props) {
		props[ordinal()].valueProperty().set(val);
	}

	default String get(Property... props) {
		return props[ordinal()].valueProperty().get();
	}

	int ordinal();

	@SafeVarargs
	static <E extends Enum<E> & PropertyTemplate<E>> Property[] props(E... enumVals) {
		Property[] props = new Property[enumVals.length];
		for (int i = 0; i < enumVals.length; i++) {
			int j = i;
			props[i] = new Property() {

				private final StringProperty value = new SimpleStringProperty();

				@Override
				public StringProperty valueProperty() {
					return value;
				}

				@Override
				public StringProperty nameProperty() {
					return new SimpleStringProperty(enumVals[j].getName());
				}
			};
		}
		return props;
	}
}