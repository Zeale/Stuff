package zeale.apps.stuff.api.guis.windows;

import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.Glow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import krow.fx.scene.HorizontalScrollBox;
import main.alixia.javalibrary.javafx.tools.FXTools;
import zeale.apps.stuff.api.appprops.ApplicationProperties;

public class Menu extends Window {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	protected final HorizontalScrollBox scrollBox = new HorizontalScrollBox();
	protected final Glow hoverGlow = new Glow(1);
	protected final VBox centerer = new VBox();
	protected final AnchorPane anchorPane = new AnchorPane(centerer);
	{
		centerer.setFillWidth(true);
		centerer.setAlignment(Pos.CENTER);

		anchorPane.setBackground(FXTools.getBackgroundFromColor(new Color(0.22, 0.22, 0.22, 1)));
		anchorPane.setPrefSize(1760, 860);

		scrollBox.getChildren().addListener((ListChangeListener<Node>) c -> {
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

		scrollBox.setNodeHeight(128);
		scrollBox.setNodeWidth(128);
		scrollBox.setAlignment(Pos.CENTER);
		centerer.getChildren().setAll(scrollBox);
		FXTools.setAllAnchors(0d, centerer);
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {

		stage.setScene(new Scene(anchorPane));
		stage.centerOnScreen();
	}

}
