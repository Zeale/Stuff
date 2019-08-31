package zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics;

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
