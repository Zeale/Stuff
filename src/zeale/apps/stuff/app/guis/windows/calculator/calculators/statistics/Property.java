package zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Tooltip;

interface Property {
	StringProperty nameProperty();

	StringProperty valueProperty();
	
	Tooltip tooltip();
}