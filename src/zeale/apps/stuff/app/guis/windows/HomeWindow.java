package zeale.apps.stuff.app.guis.windows;

import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
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
		anchorPane.setBackground(FXTools.getBackgroundFromColor(Color.ORANGE));
		anchorPane.setPrefSize(200, 200);
		System.out.println(anchorPane.getWidth());// 0.0
		System.out.println(anchorPane.getHeight());// 0.0
		System.out.println(anchorPane.computeAreaInScreen());// 0.0
		anchorPane.setOnMouseClicked(event -> {
			System.out.println(anchorPane.getWidth());// 200.0
			System.out.println(anchorPane.getHeight());// 200.0
			System.out.println(anchorPane.computeAreaInScreen());// 40000.0
		});
		stage.setScene(new Scene(anchorPane, Color.RED));
		stage.centerOnScreen();
	}

}
