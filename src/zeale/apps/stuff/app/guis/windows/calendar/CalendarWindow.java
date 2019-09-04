package zeale.apps.stuff.app.guis.windows.calendar;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

public class CalendarWindow extends Window {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	private @FXML GridPane calendar;

	// The first node under Sunday in calendar will be grid[0][1].
	// grid[column][row + 1]
	private StackPane[][] grid = new StackPane[7][6];

	private @FXML void left() {

	}

	private @FXML void right() {

	}

	private String getBorder(int x, int y) {
		return "-fx-border-color: transparent " + (x < 6 ? "-stuff-dark " : "transparent ")
				+ (y < 5 ? "-stuff-dark" : "transparent") + " transparent";
	}

	private @FXML void initialize() {
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++) {
				calendar.add(grid[i][j] = new StackPane(), i, j + 1);
				grid[i][j].setStyle(getBorder(i, j));
			}
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(CalendarWindow.class.getResource("CalendarGUI.fxml"));
		loader.setController(this);

		try {
			Parent root = loader.load();
			root.getStylesheets().addAll(properties.popButtonStylesheet.get(), properties.themeStylesheet.get());
			stage.setScene(new Scene(root));
		} catch (IOException e) {
			throw new WindowLoadFailureException(e);
		}
	}

	private @FXML void goHome() {
		Stuff.displayHome();
	}

}
