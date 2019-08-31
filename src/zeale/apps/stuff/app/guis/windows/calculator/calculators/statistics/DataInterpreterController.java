package zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class DataInterpreterController {
	private @FXML TextArea discreteDataInput, continuousDataInput;
	private @FXML TilePane discreteDetectedData, continuousDetectedData;

	private @FXML TableView<Property> discreteTable, continuousTable;
	private @FXML TableColumn<Property, String> discreteProps, continuousProps, discreteVals, continuousVals;

	private interface Property {
		StringProperty nameProperty();

		StringProperty valueProperty();
	}

	private interface PropertyTemplate<E extends Enum<E>> {
		String getName();

		void set(String val, Property... props);

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

	private enum DiscProperty implements PropertyTemplate<DiscProperty> {
		;

		public final String name;

		private DiscProperty(String name) {
			this.name = name;
		}

		@Override
		public void set(String val, Property... props) {
			props[ordinal()].valueProperty().set(val);
		}

		@Override
		public String getName() {
			return name;
		}

	}

	private enum ContProperty implements PropertyTemplate<ContProperty> {
		SUM("\u03A3"), SQUARE_OF_SUM("\u03A3\u00B2"), SUM_OF_SQUARES("\u03A3(x\u1D62)\u00B2"), MEAN("Mean (x\u0305)"),
		MEDIAN("Median (x\u0303)");

		public final String name;

		private ContProperty(String name) {
			this.name = name;
		}

		@Override
		public void set(String val, Property... props) {
			props[ordinal()].valueProperty().set(val);
		}

		@Override
		public String getName() {
			return name;
		}

	}

	private final Property[] discProps = PropertyTemplate.props(DiscProperty.values()),
			contProps = PropertyTemplate.props(ContProperty.values());

	private @FXML void initialize() {

		discreteTable.getItems().addAll(discProps);
		continuousTable.getItems().addAll(contProps);

		Callback<CellDataFeatures<Property, String>, ObservableValue<String>> cellValueNameFactory = param -> param
				.getValue().nameProperty(), cellValueValueFactory = param -> param.getValue().valueProperty();
		Callback<TableColumn<Property, String>, TableCell<Property, String>> cellFactory = param -> new TableCell<Property, String>() {

			{
				setStyle("-fx-font-size: 1.2em;");
				setTextFill(Color.GOLD);
				setAlignment(Pos.CENTER_RIGHT);

				ChangeListener<Boolean> selectionListener = (ChangeListener<Boolean>) (observable, oldValue,
						newValue) -> {
					setStyle(newValue ? "-fx-font-size: 1.2em;-fx-font-weight: bold;" : "-fx-font-size: 1.2em;");
				};

				tableRowProperty().addListener((observable, oldValue, newValue) -> {
					if (newValue != null)
						newValue.selectedProperty().addListener(selectionListener);
					if (oldValue != null)
						oldValue.selectedProperty().removeListener(selectionListener);
				});

				hoverProperty().addListener((x, y, z) -> setTextFill(z ? Color.RED : Color.GOLD));

				setOnMouseClicked(x -> {
					if (getTableRow().isEmpty())
						getTableView().getSelectionModel().clearSelection();
				});
			}

			protected void updateItem(String item, boolean empty) {
				setText(empty ? null : item);
			}
		};

		discreteProps.setCellValueFactory(cellValueNameFactory);
		discreteProps.setCellFactory(cellFactory);
		discreteVals.setCellValueFactory(cellValueValueFactory);
		discreteVals.setCellFactory(cellFactory);

		continuousProps.setCellValueFactory(cellValueNameFactory);
		continuousProps.setCellFactory(cellFactory);
		continuousVals.setCellValueFactory(cellValueValueFactory);
		continuousVals.setCellFactory(cellFactory);
	}

	private @FXML void calcDiscStats() {

	}

	private @FXML void calcContStats() {

	}

}
