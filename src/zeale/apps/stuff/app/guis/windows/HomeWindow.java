package zeale.apps.stuff.app.guis.windows;

import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import krow.fx.scene.HorizontalScrollBox;
import krow.guis.PopupHelper;
import main.alixia.javalibrary.javafx.tools.FXTools;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.app.guis.windows.calculator.CalculatorWindow;
import zeale.apps.stuff.app.guis.windows.webrequests.WebrequestGUI;

public class HomeWindow extends Window {

	@Override
	public void destroy() {

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) {
		AnchorPane anchorPane = new AnchorPane();
		anchorPane.setBackground(FXTools.getBackgroundFromColor(new Color(0.22, 0.22, 0.22, 1)));
		anchorPane.setPrefSize(1760, 860);

		VBox centerer = new VBox();
		anchorPane.getChildren().add(centerer);
		centerer.setFillWidth(true);
		centerer.setAlignment(Pos.CENTER);
		HorizontalScrollBox horizontalScrollBox = new HorizontalScrollBox();
		Glow hoverGlow = new Glow(1);
		horizontalScrollBox.getChildren().addListener((ListChangeListener<Node>) c -> {
			while (c.next())
				if (c.wasAdded())
					for (Node n1 : c.getAddedSubList()) {
						n1.setOnMouseEntered(event1 -> {
							n1.setEffect(hoverGlow);
							n1.setScaleX(1.05);
							n1.setScaleY(1.05);
						});
						n1.setOnMouseExited(event2 -> {
							n1.setScaleX(1);
							n1.setScaleY(1);
							n1.setEffect(null);
						});
					}
				else if (c.wasRemoved())
					for (Node n1 : c.getRemoved()) {
						n1.setOnMouseEntered(null);
						n1.setOnMouseExited(null);
					}

		});
		horizontalScrollBox.setNodeHeight(128);
		horizontalScrollBox.setNodeWidth(128);
		horizontalScrollBox.setAlignment(Pos.CENTER);
		centerer.getChildren().setAll(horizontalScrollBox);
		FXTools.setAllAnchors(0d, centerer);

		// Add items.

		// Calculator
		ImageView calculatorAppIcon = new ImageView(
				new Image("/zeale/apps/stuff/rsrc/app/guis/windows/calculator/Calculator.png", -1, 128, true, false));

		calculatorAppIcon.setPreserveRatio(true);
		calculatorAppIcon.setFitHeight(128);
		calculatorAppIcon.setPickOnBounds(true);
		StackPane calculatorBox = new StackPane(calculatorAppIcon);
		calculatorBox.setMinSize(128, 128);

		PopupHelper.applyInstantInfoPopup(calculatorBox, PopupHelper.buildPopup(new Label("Calculator")).popup);
		calculatorBox.setOnMouseClicked(event -> {
			try {
				new CalculatorWindow().display(stage);
			} catch (WindowLoadFailureException e) {
				Logging.err("Failed to open the Calculator Window...\n");
				Logging.err(e);
			}
		});

		// Web Requests
		ImageView webRequestAppIcon = new ImageView(
				new Image("/zeale/apps/stuff/rsrc/app/guis/windows/webrequests/Browser.png", -1, 128, true, false));
		StackPane webRequestBox = new StackPane(webRequestAppIcon);
		webRequestBox.setMinSize(128, 128);
		webRequestBox.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				try {
					new WebrequestGUI().display(stage);
				} catch (WindowLoadFailureException e) {
					Logging.err("Failed to open the Web Request Window...\n");
					Logging.err(e);
				}
			}
		});

		// Console
		ImageView consoleIcon = new ImageView(
				new Image("/zeale/apps/stuff/rsrc/app/guis/windows/console/Icon.png", -1, 128, true, false));
		consoleIcon.setPreserveRatio(true);
		consoleIcon.setFitHeight(128);
		consoleIcon.setPickOnBounds(true);
		StackPane consoleBox = new StackPane(consoleIcon);
		consoleBox.setMinSize(128, 128);

		PopupHelper.applyInstantInfoPopup(consoleBox, PopupHelper.buildPopup(new Label("Console")).popup);
		consoleBox.setOnMouseClicked(event -> Stuff.displayConsole());

		horizontalScrollBox.getChildren().addAll(calculatorBox, consoleBox, webRequestBox);

		stage.setScene(new Scene(anchorPane));
		stage.centerOnScreen();
	}

}
