package zeale.apps.stuff.app.guis.windows.calculator;

import java.io.IOException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import zeale.apps.stuff.app.guis.windows.calculator.calculators.linear_algebra.CalculatorResourceObtainer;

public class CalculatorMenuButton extends Button {
	/**
	 * The name of the FXML file, without the <code>.fxml</code> extension, to be
	 * loaded as a resource with this class's {@link Class} object.
	 */
	private final StringProperty file = new SimpleStringProperty();
	/**
	 * <p>
	 * Determines whether the extraneous GUI elements, on the left and right of the
	 * calculator window, should be hidden, (i.e. they are removed from the
	 * calculator window's {@link BorderPane}) when the {@link Node} loaded from the
	 * location specified by {@link #file} is placed in the center of the calculator
	 * window's {@link BorderPane}.
	 * </p>
	 * <p>
	 * <code>true</code> designates that the {@link Node} should fill the
	 * {@link BorderPane}'s center, and <code>false</code> designates that the
	 * {@link Node} should become the root.
	 * </p>
	 */
	private final BooleanProperty fillCenter = new SimpleBooleanProperty();
	private CalculatorWindow instance;

	void setInstance(CalculatorWindow instance) {
		this.instance = instance;
	}

	{
		setOnAction(event -> instance.showCalc(this));
	}

	void showCalc(BorderPane pane) throws IOException {
		FXMLLoader loader = new FXMLLoader(CalculatorResourceObtainer.class.getResource(file.get() + ".fxml"));
		Parent root = loader.load();
		pane.setCenter(root);
		instance.showSides(!fillCenter.get());
	}

	public final StringProperty fileProperty() {
		return this.file;
	}

	public final String getFile() {
		return this.fileProperty().get();
	}

	public final void setFile(final String file) {
		this.fileProperty().set(file);
	}

	public final BooleanProperty fillCenterProperty() {
		return this.fillCenter;
	}

	public final boolean isFillCenter() {
		return this.fillCenterProperty().get();
	}

	public final void setFillCenter(final boolean fillCenter) {
		this.fillCenterProperty().set(fillCenter);
	}

}
