package zeale.apps.stuff.app.guis.windows;

import java.io.File;

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Menu;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.app.guis.windows.calculator.CalculatorWindow;
import zeale.apps.stuff.app.guis.windows.encryption.EncryptionWindow;
import zeale.apps.stuff.app.guis.windows.modules.ModuleWindow;
import zeale.apps.stuff.app.guis.windows.taskscheduler.TaskSchedulerWindow;
import zeale.apps.stuff.app.guis.windows.webrequests.WebrequestWindow;

public class HomeWindow extends Menu {

	private final Transition blurTransition = new Transition() {

		{
			setCycleDuration(Duration.millis(100));
			setInterpolator(Interpolator.EASE_IN);
		}

		@Override
		protected void interpolate(double frac) {
			anchorPane.setEffect(new GaussianBlur(frac * 23));
		}
	};

	{
		anchorPane.setOnDragOver(event -> {
			if (event.getDragboard().hasFiles())
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			event.consume();
		});

		anchorPane.setOnDragEntered(event -> {

			blurTransition.stop();
			blurTransition.setRate(1);
			blurTransition.play();
			if (event.getDragboard().hasFiles()) {
				boolean hasOnlyJars = true, hasNoJars = true;
				for (File f : event.getDragboard().getFiles())
					if (f.getName().endsWith(".jar"))
						hasNoJars = false;
					else
						hasOnlyJars = false;
				if (hasOnlyJars)
					getScene().setFill(Color.GREEN);
				else if (hasNoJars)
					getScene().setFill(Color.RED);
				else
					getScene().setFill(Color.GOLD);
			} else
				getScene().setFill(Color.FIREBRICK);
			event.consume();
		});

		anchorPane.setOnDragExited(event -> {
			blurTransition.stop();
			blurTransition.setRate(-1);
			blurTransition.play();
			getScene().setFill(DEFAULT_BACKGROUND_COLOR);
		});

		anchorPane.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {

				CHECK: if (event.getDragboard().hasFiles()) {
					for (File f : event.getDragboard().getFiles())
						if (f.getName().endsWith(".jar"))
							break CHECK;
					return;
				}

				ModuleWindow.loadModules(event.getDragboard().getFiles());

				try {
					Stuff.displayWindow(new ModuleWindow());
				} catch (WindowLoadFailureException e) {
					Logging.err("Failed to display the ModuleWindow.");
					Logging.err(e);
				}
			}
		});
	}

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
