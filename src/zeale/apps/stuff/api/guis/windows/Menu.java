package zeale.apps.stuff.api.guis.windows;

import java.util.function.Supplier;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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
import zeale.apps.stuff.api.logging.Logging;

public class Menu extends Window {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void addItem(Node item) {
		scrollBox.getChildren().add(item);
	}

	public void removeItem(Node item) {
		scrollBox.getChildren().remove(item);
	}

	public ObservableList<Node> getChildren() {
		return scrollBox.getChildren();
	}

	public StackPane addImageNode(String imageURL, EventHandler<? super MouseEvent> onClick, String popup) {
		return addImageNode(imageURL, onClick, new Label(popup));
	}

	public StackPane addImageNode(String imageURL, EventHandler<? super MouseEvent> onClick, Label popup) {
		ImageView icon = new ImageView(new Image(imageURL, -1, 128, true, false));
		icon.setPreserveRatio(true);
		icon.setFitHeight(128);
		icon.setPickOnBounds(true);
		StackPane box = new StackPane(icon);
		box.setMinSize(128, 128);

		box.setOnMouseClicked(onClick);

		PopupHelper.applyInstantInfoPopup(box, PopupHelper.buildPopup(popup).popup);

		scrollBox.getChildren().add(box);
		return box;
	}

	public StackPane addImageNode(String imageURL, Runnable onClick, Label popup) {
		return addImageNode(imageURL, a -> {
			if (a.getButton() == MouseButton.PRIMARY)
				onClick.run();
		}, popup);
	}

	public StackPane addImageNode(String imageURL, Runnable onClick, String popup) {
		return addImageNode(imageURL, onClick, new Label(popup));
	}

	public StackPane addImageNode(String imageURL, Supplier<Window> windowSupplier, Label popup) {
		return addImageNode(imageURL, (a) -> {
			if (a.getButton() == MouseButton.PRIMARY)
				try {
					Stuff.displayWindow(windowSupplier.get());
				} catch (WindowLoadFailureException e) {
					Logging.err("Failed to open the window...\n");
					Logging.err(e);
				}
		}, popup);
	}

	public StackPane addImageNode(String imageURL, Supplier<Window> windowSupplier, String popup) {
		return addImageNode(imageURL, windowSupplier, new Label(popup));
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
	protected void show(Stage stage, ApplicationProperties properties) {
		stage.setScene(new Scene(anchorPane));
		stage.centerOnScreen();
	}

}
