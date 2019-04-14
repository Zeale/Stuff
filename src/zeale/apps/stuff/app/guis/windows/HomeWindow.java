package zeale.apps.stuff.app.guis.windows;

import java.util.LinkedList;
import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import krow.fx.scene.HorizontalScrollBox;
import krow.guis.PopupHelper;
import main.alixia.javalibrary.javafx.tools.FXTools;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

public class HomeWindow extends Window {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

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

		List<Circle> circles = new LinkedList<>();
		for (int i = 0; i < 10; i++) {
			Circle circle = new Circle(64, Color.hsb(Math.random() * 360, 1, 1));
			circles.add(circle);
			PopupHelper.applyInfoPopup(circle, PopupHelper.buildPopup(new Label("Test")).popup);
		}

		horizontalScrollBox.getChildren().addAll(circles);

		stage.setScene(new Scene(anchorPane));
		stage.centerOnScreen();
	}

}
