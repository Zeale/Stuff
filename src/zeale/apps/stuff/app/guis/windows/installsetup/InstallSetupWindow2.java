package zeale.apps.stuff.app.guis.windows.installsetup;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.swing.filechooser.FileSystemView;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mslinks.ShellLink;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.app.guis.windows.HomeWindow;

public class InstallSetupWindow2 extends Window {

	@Override
	public void destroy() {
		stage.setMinHeight(0);
		stage.setMinWidth(0);
		stage.setMaxHeight(Double.MAX_VALUE);
		stage.setMaxWidth(Double.MAX_VALUE);
		stage.setAlwaysOnTop(false);
	}

	private Stage stage;

	private static void link(File from, File to) throws IOException {
		to.getParentFile().mkdirs();// Make path if it doesn't already exist.
		to.delete();// Delete the file if there's already something there for whatever reason.

		ShellLink link = ShellLink.createLink(from.getAbsolutePath());
		link.saveTo(to.getAbsolutePath());
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		this.stage = stage;

		Text prompt = new Text(
				"Please select the locations where you'd like to include a shortcut to the program, if any.");
		CheckBox startMenu = new CheckBox("Start Menu"), desktop = new CheckBox("Desktop");
		VBox checkBoxBox = new VBox(startMenu, desktop);

		Button continueButton = new Button("Continue");

		continueButton.setOnAction(new EventHandler<ActionEvent>() {

			private volatile boolean running;

			@Override
			public synchronized void handle(ActionEvent event) {
				if (running)
					return;
				running = true;

				// We have to use a shortcut to the program, not a symlink or hardlink, as those
				// make the program think it's currently running off of whatever location the
				// link exists (and was called) at. For now we'll use a third party library,
				// until I can painstakingly read the shelllink spec and write shelllinks out by
				// hand.

				File currProgram1;
				try {
					currProgram1 = new File(URLDecoder.decode(
							InstallSetupWindow1.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
							StandardCharsets.UTF_8.name())).getAbsoluteFile();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return;// This should never happen, since UTF-8 support is required for Java
							// implementations. But more importantly, I haven't thought of what to write for
							// if it does happen, so :p
				}

				START_MENU_BLOCK: if (startMenu.isSelected()) {

					try {
						link(currProgram1,
								new File("C:/ProgramData/Microsoft/Windows/Start Menu/Programs/Zeale/Stuff.lnk"));
						break START_MENU_BLOCK;
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					try {
						link(currProgram1, new File(System.getenv("APPDATA"),
								"Microsoft/Windows/Start Menu/Programs/Zeale/Stuff.lnk"));
					} catch (IOException e2) {
						e2.printStackTrace();
					}
				}

				if (desktop.isSelected()) {

					try {
						link(currProgram1,
								new File(FileSystemView.getFileSystemView().getHomeDirectory(), "Stuff.lnk"));
					} catch (IOException e3) {
						e3.printStackTrace();
					}
				}

				try {
					new HomeWindow().display(stage);
				} catch (WindowLoadFailureException e4) {
					e4.printStackTrace();
				}

				running = false;

			}
		});

		VBox box = new VBox(20, prompt, checkBoxBox, continueButton);

		stage.setScene(new Scene(box));
		stage.show();

		stage.setMinHeight(300);
		stage.setMinWidth(200);
		stage.setMaxHeight(800);
		stage.setMaxWidth(1000);
		stage.centerOnScreen();
		stage.setAlwaysOnTop(true);
	}

}
