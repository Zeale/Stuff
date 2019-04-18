package zeale.apps.stuff.app.guis.windows.installsetup;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.installation.InstallationData;
import zeale.apps.stuff.app.guis.windows.HomeWindow;

public class InstallSetupWindow extends Window {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	private static void copyDirectory(File from, File to) throws IOException {
		to.mkdirs();
		for (File f : from.listFiles())
			if (!f.isDirectory())
				Files.copy(f.toPath(), new File(to, f.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
			else
				copyDirectory(f, new File(to, f.getName()));
	}

	private static void copyInstall(File from, File to) throws IOException {
		copyDirectory(from, to);
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {

		stage.show();

		Text info = new Text(
				"Please choose a location to install the program to. The default path in the input field is the current installation directory. You can hit continue, if you'd like to, without changing the directory, to keep the installation location as it is.");
		info.setWrappingWidth(200);

		TextField input = new TextField(Stuff.INSTALLATION_DIRECTORY.toString());
		Button fileChooseButton = new Button("..."), defaultButton = new Button(""),
				continueButton = new Button("Continue");

		DirectoryChooser saveFileChooser = new DirectoryChooser();
		saveFileChooser.initialDirectoryProperty()
				.bind(Bindings.createObjectBinding(() -> new File(input.getText()), input.textProperty()));
		fileChooseButton.setOnAction(event -> {
			File file = saveFileChooser.showDialog(stage);
			if (file != null)
				input.setText(file.getAbsolutePath());
		});

		defaultButton.setOnAction(event -> input.setText(Stuff.INSTALLATION_DIRECTORY.toString()));

		HBox inputBox = new HBox(10, input, fileChooseButton, defaultButton);
		inputBox.setPadding(new Insets(0, 80, 0, 80));
		inputBox.setAlignment(Pos.CENTER);

		VBox box = new VBox(20, info, inputBox, continueButton);
		box.setFillWidth(true);
		box.setAlignment(Pos.CENTER);

		continueButton.setOnAction(new EventHandler<ActionEvent>() {

			private volatile boolean running;

			@Override
			public synchronized void handle(ActionEvent event) {
				if (running == true)
					return;
				running = true;
				File to = new File(input.getText());
				to.mkdirs();
				if (!to.isDirectory())
					throw new RuntimeException("The destination folder could not be created.");
				if (!to.equals(Stuff.INSTALLATION_DIRECTORY)) {
					try {
						File currFile = new File(
								InstallSetupWindow.class.getProtectionDomain().getCodeSource().getLocation().getPath())
										.getAbsoluteFile();
						Files.copy(currFile.toPath(), new File(to, currFile.getName()).toPath(),
								StandardCopyOption.REPLACE_EXISTING);

						box.getChildren().remove(inputBox);
						info.setText("Finished setting installation directory. Press continue to continue.");

						continueButton.setOnAction(event1 -> {
							try {
								Runtime.getRuntime()
										.exec(currFile.getName() + " "
												+ InstallationData.INSTALLATION_CLEANUP_DIRECTIVE_ARGUMENT + "=\""
												+ currFile.getAbsolutePath() + '"', null, to);
								Platform.exit();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						});

					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				} else {
					try {
						new HomeWindow().display(stage);
					} catch (WindowLoadFailureException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		});

		stage.setScene(new Scene(box));

		stage.setMinHeight(300);
		stage.setMinWidth(200);
		stage.setMaxHeight(800);
		stage.setMaxWidth(1000);
		stage.centerOnScreen();
		stage.setAlwaysOnTop(true);

	}

}
