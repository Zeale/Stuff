package zeale.apps.stuff.app.guis.windows.calculator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.alixia.libs.evaluator.Evaluator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.javafx.guis.windows.calculator.TaggedCalculatorButton;
import zeale.apps.stuff.api.logging.Logging;

public final class CalculatorWindow extends Window {

	private @FXML BorderPane root;
	private @FXML TextField inputField;

	private @FXML VBox leftSide;
	private @FXML Accordion calcSelectorAccordion;// rightSide

	private @FXML TextField extendedFunctionalitySearch;
	private @FXML Pane extendedFunctionalityFlowPane;

	private @FXML void buttonPushed(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof Button)
			inputField.appendText(((Button) source).getText());
		else
			Logging.wrn("A button was misconfigured. Please report this error to the author. (Button Error: " + source
					+ "   " + source.getClass() + ".)");

		int cp = inputField.getCaretPosition();
		inputField.requestFocus();
		inputField.positionCaret(cp);
	}

	void showSides(boolean show) {
		if (show) {
			root.setLeft(leftSide);
			root.setRight(calcSelectorAccordion);
		} else {
			root.setLeft(null);
			root.setRight(null);
		}
	}

	void showCalc(CalculatorMenuButton calculator) {
		try {
			calculator.showCalc(root);
		} catch (IOException e) {
			Logging.err("Failed to show the calculator menu.");
			Logging.err(e);
		}
	}

	private @FXML void clearInputField(ActionEvent event) {
		inputField.clear();
	}

	@Override
	public void destroy() {
	}

	private @FXML void functionPushed(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof Button)
			inputField.appendText(((Button) source).getText() + "(");
		else
			Logging.wrn("A function button was misconfigured. Please report this error to the author. (Button Error: "
					+ source + "   " + source.getClass() + ".)");
	}

	private @FXML void goHome(ActionEvent event) {
		Stuff.displayHome();
	}

	private @FXML void initialize() {

		List<TaggedCalculatorButton> buttons = new ArrayList<>(extendedFunctionalityFlowPane.getChildren().size());
		for (Node n : extendedFunctionalityFlowPane.getChildren())
			if (n instanceof TaggedCalculatorButton)
				buttons.add((TaggedCalculatorButton) n);

		extendedFunctionalitySearch.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public synchronized void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				if (newValue.contains(oldValue) && newValue.length() > oldValue.length())
					for (Iterator<Node> iterator = extendedFunctionalityFlowPane.getChildren().iterator(); iterator
							.hasNext();) {
						Node n = iterator.next();
						if (n instanceof TaggedCalculatorButton
								&& !matches(newValue, ((TaggedCalculatorButton) n).getTag()))
							iterator.remove();
					}
				else {
					for (Iterator<Node> iterator = extendedFunctionalityFlowPane.getChildren().iterator(); iterator
							.hasNext();)
						if (iterator.next() instanceof TaggedCalculatorButton)
							iterator.remove();
					for (TaggedCalculatorButton tcb : buttons)
						if (matches(newValue, tcb.getTag()))
							extendedFunctionalityFlowPane.getChildren().add(tcb);
				}

			}

			private boolean matches(String searchTerm, String itemName) {
				return itemName.toLowerCase().contains(searchTerm.toLowerCase());
			}
		});

		for (TitledPane tp : calcSelectorAccordion.getPanes())
			for (Node n : ((Parent) tp.getContent()).getChildrenUnmodifiable())
				if (n instanceof CalculatorMenuButton)
					((CalculatorMenuButton) n).setInstance(this, tp.getText());

	}

	private @FXML void inputFieldKeyEvent(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER && inputField.getLength() != 0) {
			solve();
			inputField.positionCaret(inputField.getLength());
		}
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("CalculatorGUI.fxml"));
		loader.setController(this);
		try {
			Parent root = loader.load();
			root.getStylesheets().addAll(properties.popButtonStylesheet.get(), properties.themeStylesheet.get(),
					"/zeale/apps/stuff/app/guis/windows/calculator/CalculatorGUI.css");
			stage.setScene(new Scene(root));
		} catch (IOException e) {
			throw new WindowLoadFailureException("Failed to load the UI for the Calculator Window.", e);
		}
	}

	private void solve() {
		try {
			inputField.setText(Evaluator.solveToString(inputField.getText()));
		} catch (Exception e) {
			Logging.err(e.getMessage());
		}
	}

	private @FXML void solve(ActionEvent event) {
		solve();
	}

}
