package zeale.apps.stuff.app.guis.windows.calendar;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

class DayViewWindow extends Window {

	private final LocalDate date;
	private final CalendarWindow window;
	private final boolean showBackButton;
	private @FXML Menu commandsMenu;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	public DayViewWindow(CalendarWindow window, LocalDate date, boolean showBackButton) {
		this.date = date;
		this.window = window;
		this.showBackButton = showBackButton;
	}

	private @FXML void initialize() {
		if (!showBackButton)
			commandsMenu.setVisible(false);
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		// TODO Auto-generated method stub

	}

	private @FXML void back() {
		try {
			Stuff.displayWindow(window);
		} catch (WindowLoadFailureException e) {
			e.printStackTrace();
		}
	}

}
