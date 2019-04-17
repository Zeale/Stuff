package zeale.apps.stuff.app.guis.windows;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import krow.fx.scene.HorizontalScrollBox;
import krow.guis.PopupHelper;
import main.alixia.javalibrary.javafx.tools.FXTools;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.app.guis.windows.calculator.CalculatorWindow;

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
		horizontalScrollBox.setNodeHeight(128);
		horizontalScrollBox.setNodeWidth(128);
		horizontalScrollBox.setAlignment(Pos.CENTER);
		centerer.getChildren().setAll(horizontalScrollBox);
		FXTools.setAllAnchors(0d, centerer);

		// Add items.
		ImageView calculatorAppIcon = new ImageView(new Image(
				"/zeale/apps/stuff/rsrc/app/guis/windows/calculator/PlusOrMinusSymbol.png", 128, 128, true, false));
		calculatorAppIcon.setPickOnBounds(true);
		PopupHelper.applyInstantInfoPopup(calculatorAppIcon, PopupHelper.buildPopup(new Label("Calculator")).popup);
		calculatorAppIcon.setOnMouseClicked(event -> {
			try {
				new CalculatorWindow().display(stage);
			} catch (WindowLoadFailureException e) {
				// TODO Print to console.
				e.printStackTrace();
			}
		});

		horizontalScrollBox.getChildren().addAll(calculatorAppIcon);

		stage.setScene(new Scene(anchorPane));
		stage.centerOnScreen();
	}

}
