package zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics;

import static zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics.ContProperty.MEAN;
import static zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics.ContProperty.MEDIAN;
import static zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics.ContProperty.N;
import static zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics.ContProperty.SQUARE_OF_SUM;
import static zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics.ContProperty.SUM;
import static zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics.ContProperty.SUM_OF_SQUARES;
import static zeale.apps.stuff.app.guis.windows.calculator.calculators.statistics.ContProperty.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alixia.javalibrary.JavaTools;
import org.alixia.javalibrary.streams.CharacterStream;
import org.alixia.javalibrary.util.Pair;

import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Callback;
import main.alixia.javalibrary.javafx.tools.FXTools;
import zeale.apps.stuff.api.logging.Logging;

public class DataInterpreterController {
	private @FXML TextArea discreteDataInput, continuousDataInput;
	private @FXML FlowPane discreteDetectedData, continuousDetectedData;

	private @FXML TableView<Property> discreteTable, continuousTable;
	private @FXML TableColumn<Property, String> discreteProps, continuousProps, discreteVals, continuousVals;

	private final Property[] discProps = PropertyTemplate.props(DiscProperty.values()),
			contProps = PropertyTemplate.props(ContProperty.values());

	private @FXML void initialize() {

		Background color = FXTools.getBackgroundFromColor(new Color(0, 0, 0, 0.2));

		abstract class InputHandler implements EventHandler<KeyEvent> {
			private final TextArea input;
			private final FlowPane output;

			protected final void printOut(String text) {
				Text data = new Text(text);
				StackPane box = new StackPane(data);
				box.setBackground(color);
				data.setFill(Color.GOLD);
				output.getChildren().add(box);
			}

			public InputHandler(TextArea input, FlowPane output) {
				(this.input = input).setOnKeyReleased(this);
				this.output = output;
			}

			protected abstract String handle(String textFragment);

			@Override
			public void handle(KeyEvent event) {
				CharacterStream str = CharacterStream.from(input.getText());
				StringBuilder curr = new StringBuilder();
				int n;
				output.getChildren().clear();
				try {
					while ((n = str.next()) != -1)
						if (Character.isWhitespace(n)) {
							while (Character.isWhitespace(n = str.next()))
								;

							printOut(handle(curr.toString()));

							if (n == -1)
								return;
							else
								curr = new StringBuilder().append((char) n);
						} else
							curr.append((char) n);
					if (curr.length() != 0) {
						printOut(handle(curr.toString()));
					}
				} catch (Exception e) {
				}

			}

		}

		new InputHandler(discreteDataInput, discreteDetectedData) {

			@Override
			protected String handle(String textFragment) {
				return textFragment;
			}
		};
		new InputHandler(continuousDataInput, continuousDetectedData) {

			@Override
			protected String handle(String textFragment) {
				return "" + Double.parseDouble(textFragment);
			}
		};

		discreteTable.getItems().addAll(discProps);
		continuousTable.getItems().addAll(contProps);

		Callback<CellDataFeatures<Property, String>, ObservableValue<String>> cellValueNameFactory = param -> param
				.getValue().nameProperty().concat(":"),
				cellValueValueFactory = param -> param.getValue().valueProperty();
		class Cell extends TableCell<Property, String> {

			{

				tableRowProperty().addListener((observable, oldValue, newValue) -> {
					if (newValue != null) {
						textFillProperty().unbind();
						textFillProperty().bind(newValue.textFillProperty());
					}
				});
			}

			public Cell(Pos position) {
				setAlignment(position);
			}

			protected void updateItem(String item, boolean empty) {
				setText(empty ? null : item);
			}
		}
		Callback<TableColumn<Property, String>, TableCell<Property, String>> leftCellFactory = param -> new Cell(
				Pos.CENTER_LEFT), rightCellFactory = p -> new Cell(Pos.CENTER_RIGHT);

		Callback<TableView<Property>, TableRow<Property>> rowFactory = param -> new TableRow<Property>() {
			{
				setStyle("-fx-font-size: 1.2em;");

				selectedProperty().addListener((observable, oldValue, newValue) -> setStyle(
						newValue ? "-fx-font-size: 1.2em;-fx-font-weight: bold;" : "-fx-font-size: 1.2em;"));
				setTextFill(Color.GOLD);
				hoverProperty().addListener((x, y, z) -> setTextFill(z ? Color.RED : Color.GOLD));
				setOnMouseClicked(x -> {
					if (!isEmpty())
						getTableView().getSelectionModel().select(getIndex());
					else
						getTableView().getSelectionModel().clearSelection();
				});
			}

		};

		discreteTable.setRowFactory(rowFactory);
		discreteProps.setCellValueFactory(cellValueNameFactory);
		discreteProps.setCellFactory(rightCellFactory);
		discreteVals.setCellValueFactory(cellValueValueFactory);
		discreteVals.setCellFactory(leftCellFactory);

		continuousTable.setRowFactory(rowFactory);
		continuousProps.setCellValueFactory(cellValueNameFactory);
		continuousProps.setCellFactory(rightCellFactory);
		continuousVals.setCellValueFactory(cellValueValueFactory);
		continuousVals.setCellFactory(leftCellFactory);
	}

	private @FXML void calcDiscStats() {

	}

	private List<Double> readContinuousStats() {
		List<Double> values = new ArrayList<>();

		CharacterStream str = CharacterStream.from(continuousDataInput.getText());
		StringBuilder curr = new StringBuilder();
		int n;

		try {
			while ((n = str.next()) != -1)
				if (Character.isWhitespace(n)) {
					while (Character.isWhitespace(n = str.next()))
						;

					values.add(Double.parseDouble(curr.toString()));

					if (n == -1)
						break;
					else
						curr = new StringBuilder().append((char) n);
				} else
					curr.append((char) n);
			if (curr.length() != 0)
				values.add(Double.parseDouble(curr.toString()));
		} catch (NumberFormatException e) {
			throw new RuntimeException("The input: " + curr.toString() + " could not be parsed as a number.", e);
		}

		return values;
	}

	private @FXML void calcContStats() {
		List<Double> values;
		try {
			values = readContinuousStats();
		} catch (Exception e) {
			Logging.err("Some of your input is malformed...");
			Logging.err(e);
			return;
		}

		int size = values.size();
		double n = size, sum = 0, sumofsquares = 0;
		N.set(String.valueOf(size), contProps);

		for (double cv : values) {
			sum += cv;
			sumofsquares += cv * cv;
		}
		setProp(SUM, sum);
		setProp(SQUARE_OF_SUM, sum * sum);
		setProp(SUM_OF_SQUARES, sumofsquares);

		double mean = n == 0 ? 0 : sum / n;
		setProp(MEAN, mean);

		Pair<Double, Double> res = JavaTools.findMedian(values);
		setProp(MEDIAN, res == null ? 0 : res.second == null ? res.first : (res.first + res.second) / 2);

		Map<Double, Integer> freqMap = JavaTools.frequencyMap(values);

		List<Double> list = new ArrayList<>(3);
		int curr = 0;
		for (Entry<Double, Integer> e : freqMap.entrySet())
			if (e.getValue() > curr) {
				list.clear();
				curr = e.getValue();
				list.add(e.getKey());
			} else if (e.getValue() == curr)
				list.add(e.getKey());
		if (list.isEmpty())
			MODE.set("N/A", contProps);
		else if (list.size() == 1)
			setProp(MODE, list.get(0));
		else {
			Iterator<Double> itr = list.iterator();
			StringBuilder builder = new StringBuilder(itr.next().toString());
			do
				builder.append(", ").append(itr.next());
			while (itr.hasNext());
			MODE.set(builder.toString(), contProps);
		}
	}

	private void setProp(ContProperty prop, double val) {
		prop.set(val, contProps);
	}

}
