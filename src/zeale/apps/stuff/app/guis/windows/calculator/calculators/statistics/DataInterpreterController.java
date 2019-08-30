package zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DataInterpreterController {
	private @FXML TextArea discreteDataInput;
	private @FXML TilePane discreteDetectedData;

	private @FXML TableView<Property> discreteTable;
	private @FXML TableColumn<Property, String> discreteProps, discreteVals;

	private interface Property {
		void setValue(String val);

		StringProperty nameProperty();

		StringProperty valueProperty();
	}

	private enum DiscProperty implements Property {
		MEAN("Mean (\u03A3; x\u0305)"), MEDIAN("Median (x\u0303)");

		private final StringProperty name = new SimpleStringProperty(), value = new SimpleStringProperty();

		private DiscProperty(String name) {
			setName(name);
		}

		public final StringProperty nameProperty() {
			return name;
		}

		public final void setName(final String name) {
			nameProperty().set(name);
		}

		public final StringProperty valueProperty() {
			return value;
		}

		public final void setValue(final String value) {
			valueProperty().set(value);
		}

	}

	private @FXML void initialize() {
		discreteTable.getItems().addAll(DiscProperty.values());

		discreteProps.setCellValueFactory(param -> param.getValue().nameProperty());

		discreteProps.setCellFactory(param -> new TableCell<Property, String>() {

			private final Font regularFont = Font.font(20),
					boldFont = Font.font(regularFont.getFamily(), FontWeight.BOLD, regularFont.getSize());
			{
				setStyle("-fx-font: egg;-fx-font-size: 1.2em;-fx-font-weight: egg;");
				setTextFill(Color.GOLD);
				setAlignment(Pos.CENTER_RIGHT);
				selectedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue,
						newValue) -> setFont(newValue ? boldFont : regularFont));
				hoverProperty().addListener((x, y, z) -> setTextFill(z ? Color.RED : Color.GOLD));
				setOnMouseClicked(x -> discreteTable.getSelectionModel().select(getIndex()));
			}

			protected void updateItem(String item, boolean empty) {
				setText(empty ? null : item);
			}
		});
		discreteVals.setCellValueFactory(param -> param.getValue().valueProperty());
	}

	private @FXML void calcStats() {

	}

}
