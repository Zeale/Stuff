package zeale.apps.stuff.app.guis.windows.installsetup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.filechooser.FileSystemView;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mslinks.ShellLink;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.tools.console.api.texts.Text;

public class InstallSetupWindow2 extends Window {

	public static void main(String[] args) throws FileNotFoundException {
		try (PrintWriter writer = new PrintWriter(new File("Out.txt"))) {
			writer.println("abc");
		}
	}

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

			@Override
			public void handle(ActionEvent event) {

				// We have to use a shortcut to the program, not a symlink or hardlink, as those
				// make the program think it's currently running off of whatever location the
				// link exists (and was called) at. For now we'll use a third party library,
				// until I can painstakingly read the shelllink spec and write shelllinks out by
				// hand.

				START_MENU_BLOCK: if (startMenu.isSelected()) {
					File currProgram = new File(
							InstallSetupWindow1.class.getProtectionDomain().getCodeSource().getLocation().getPath())
									.getAbsoluteFile();

					try {
						link(currProgram,
								new File("C:/ProgramData/Microsoft/Windows/Start Menu/Programs/Zeale/Stuff.lnk"));
						break START_MENU_BLOCK;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						link(currProgram, new File(System.getenv("APPDATA"),
								"Microsoft/Windows/Start Menu/Programs/Zeale/Stuff.lnk"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (desktop.isSelected()) {
					File currProgram = new File(
							InstallSetupWindow1.class.getProtectionDomain().getCodeSource().getLocation().getPath())
									.getAbsoluteFile();

					try {
						link(currProgram, new File(FileSystemView.getFileSystemView().getHomeDirectory(), "Stuff.lnk"));
					} catch (IOException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}

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
