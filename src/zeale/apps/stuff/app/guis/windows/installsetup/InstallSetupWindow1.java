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
import zeale.apps.stuff.api.installation.ProgramArguments;

public class InstallSetupWindow1 extends Window {

	@Override
	public void destroy() {
		// Undo the modifications that this Window did to the Stage. (This doesn't
		// really undo anything, it just sets values to the default. That's ok for now
		// though.)
		stage.setMinHeight(0);
		stage.setMinWidth(0);
		stage.setMaxHeight(Double.MAX_VALUE);
		stage.setMaxWidth(Double.MAX_VALUE);
		stage.setAlwaysOnTop(false);
	}

	private Stage stage;

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		this.stage = stage;
		stage.show();

		Text info = new Text(
				"Please choose a location to install the program to. The default path in the input field is the current installation directory. You can hit continue, if you'd like to, without changing the directory, to keep the installation location as it is.");
		info.setWrappingWidth(200);

		TextField input = new TextField(Stuff.INSTALLATION_DIRECTORY.toString());
		Button fileChooseButton = new Button("..."), defaultButton = new Button(""),
				continueButton = new Button("Continue");

		fileChooseButton.getStyleClass().add("pop-button");
		defaultButton.getStyleClass().add("pop-button");
		continueButton.getStyleClass().add("pop-button");

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
				File currFile = new File(
						InstallSetupWindow1.class.getProtectionDomain().getCodeSource().getLocation().getPath())
								.getAbsoluteFile();
				if (!to.equals(Stuff.INSTALLATION_DIRECTORY)) {
					try {

						File executable = new File(to, currFile.getName());
						Files.copy(currFile.toPath(), executable.toPath(), StandardCopyOption.REPLACE_EXISTING);

						box.getChildren().remove(inputBox);
						info.setText("Finished setting installation directory. Press continue to continue.");

						continueButton.setOnAction(event1 -> {
							try {
								Runtime.getRuntime()
										.exec(executable.getAbsolutePath() + " " + ProgramArguments.INSTALLATION_CLEANUP
												+ "=\"" + currFile.getAbsolutePath() + "\" "
												+ ProgramArguments.INSTALLATION_STAGE_2, null, to);
								Platform.exit();
							} catch (IOException e) {
								// TODO Auto-generated catch block
							}
						});

					} catch (IOException e) {
						// TODO Auto-generated catch block
					}
				} else {
					try {
						Runtime.getRuntime()
								.exec(currFile.getName() + " " + ProgramArguments.INSTALLATION_CLEANUP + "=\""
										+ currFile.getAbsolutePath() + "\" " + ProgramArguments.INSTALLATION_STAGE_2,
										null, to);
						Platform.exit();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				running = false;
			}

		});

		Scene scene = new Scene(box);
		stage.setScene(scene);
		scene.getStylesheets().add("zeale/apps/stuff/api/guis/windows/stylesheets/Pop%20Button.css");
		scene.getStylesheets().add("zeale/apps/stuff/api/guis/windows/stylesheets/BasicStyles.css");

		stage.setMinHeight(300);
		stage.setMinWidth(200);
		stage.setMaxHeight(800);
		stage.setMaxWidth(1000);
		stage.centerOnScreen();
		stage.setAlwaysOnTop(true);

	}

}
