package zeale.apps.stuff.app.guis.windows.modules;

import java.io.File;
import java.util.Collection;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

public class ModuleWindow extends Window {

	private @FXML FlowPane moduleBox;

	private @FXML void initialize() {
		/* TODO */
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		/* TODO */
	}

	public static void loadModules(Collection<File> modules) {
		/* TODO */
	}

	public static void loadModule(File module) {
		/* TODO */
	}

	public static void loadPackage(File pckge) {
		/* TODO */
	}

}
