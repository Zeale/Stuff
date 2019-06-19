package zeale.apps.stuff.app.guis.windows;

import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Menu;
import zeale.apps.stuff.app.guis.windows.calculator.CalculatorWindow;
import zeale.apps.stuff.app.guis.windows.encryption.EncryptionWindow;
import zeale.apps.stuff.app.guis.windows.taskscheduler.TaskSchedulerWindow;
import zeale.apps.stuff.app.guis.windows.webrequests.WebrequestWindow;

public class HomeWindow extends Menu {

	@Override
	public void destroy() {

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) {
		super.show(stage, properties);
		// Calculator
		addImageNode("/zeale/apps/stuff/rsrc/app/guis/windows/calculator/Calculator.png", () -> new CalculatorWindow(),
				"Calculator");

		// Web Requests
		addImageNode("/zeale/apps/stuff/rsrc/app/guis/windows/webrequests/WorldWeb.png", () -> new WebrequestWindow(),
				"Web Requests");

		// Console
		{
			Label consoleLabel = new Label("Console");
			consoleLabel.setTextFill(Color.FIREBRICK);

			Image consoleIconRaw = loadFormatted("/zeale/apps/stuff/rsrc/app/guis/windows/console/Icon.png", 128);
			WritableImage consoleIcon = new WritableImage(consoleIconRaw.getPixelReader(),
					(int) consoleIconRaw.getWidth(), (int) consoleIconRaw.getHeight());

			PixelWriter writer = consoleIcon.getPixelWriter();
			PixelReader reader = consoleIcon.getPixelReader();

			Color primary = Color.hsb(Math.random() * 360, 1, 1),
					secondary = Color.hsb(primary.getHue() + 180 % 360, 1, 1);

			for (int i = 0; i < consoleIcon.getWidth(); i++)
				for (int j = 0; j < consoleIcon.getHeight(); j++)
					switch (reader.getArgb(i, j)) {
					case -16711423:
						writer.setColor(i, j, primary);
						break;
					case -16645630:
						writer.setColor(i, j, secondary);
					}

			addImageNode(consoleIcon, Stuff::displayConsole, consoleLabel);
		}

		// Encryption
		addImageNode("/zeale/apps/stuff/rsrc/app/guis/windows/encryption/Key.png", () -> new EncryptionWindow(),
				"Encryption");
		{
			ColorAdjust effect = new ColorAdjust(0, 0, -0.2, 0.);
			setCustomEffect(addImageNode("/zeale/apps/stuff/rsrc/app/guis/windows/taskscheduler/TodoList.png",
					() -> new TaskSchedulerWindow(), "To Do List"), effect);
		}
	}

}
