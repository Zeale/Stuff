package zeale.apps.stuff.app.guis.windows.updates;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import zeale.applicationss.notesss.utilities.Utilities;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.javafx.guis.windows.updates.UpdateListing;
import zeale.apps.stuff.app.guis.windows.HomeWindow;

public class UpdateWindow extends Window {

	private @FXML VBox updateSelectorBox;
	private @FXML Text statusText;
	private @FXML AnchorPane updatePanel;

	private String updateRepository = "http://stuff.dusttoash.org/download/latest.txt";// This will be modifiable by the
																						// user later.

	private String defaultText;

	private @FXML void initialize() {
		defaultText = statusText.getText();// Get text from FXML file.
		refresh();
	}

	Thread queryThread = new Thread(new Runnable() {
		@Override
		public void run() {
			try (Scanner scanner = new Scanner(new URL(updateRepository).openConnection().getInputStream())) {
				Platform.runLater(() -> statusText.setText("Querying repository for updates..."));
				String version = scanner.nextLine(), date = scanner.nextLine(), title = scanner.nextLine();
				List<String> addtions = new LinkedList<>(), changes = new LinkedList<>(), removals = new LinkedList<>(),
						fixes = new LinkedList<>(), information = new LinkedList<>();
				while (scanner.hasNextLine()) {
					String update = scanner.nextLine();
					if (update.isEmpty())
						continue;
					// This can be updated later.
					switch (update.charAt(0)) {
					case '+':
						addtions.add(update.substring(2));
						break;
					case '-':
						removals.add(update.substring(2));
						break;
					case '~':
						changes.add(update.substring(2));
						break;
					case '*':
						fixes.add(update.substring(2));
						break;
					default:
						information.add(update);
					}

				}

				Platform.runLater(() -> {
					UpdateListing listing = new UpdateListing(title, new Image(
							"/zeale/apps/stuff/rsrc/app/guis/windows/updates/Update Icon.png", -1, 128, true, false));
					// TODO Add a small selection API.
					listing.setOnMouseClicked(event -> {
						if (event.getButton() == MouseButton.PRIMARY) {
							TextFlow textBox = new TextFlow();
							Utilities.setAllAnchors(20d, textBox);
							updatePanel.getChildren().setAll(textBox);

							Text titleText = new Text(title + "\n\n"), versionText = new Text(version + " "),
									dateText = new Text(date + "\n\n\n\n");
							titleText.setTextAlignment(TextAlignment.CENTER);
							titleText.setFill(Color.WHITE);
							titleText.setFont(Font.font(24));
							versionText.setFill(Color.LIGHTBLUE);
							versionText.setFont(Font.font(null, FontPosture.ITALIC, 18));
							dateText.setFill(Color.WHITE);

							textBox.getChildren().addAll(titleText, versionText, dateText);

							class Printer {
								void print(List<String> items, Color color, String prependedText) {
									for (String s1 : items) {
										Text text1 = new Text(prependedText + " " + s1 + "\n");
										text1.setFill(color);
										textBox.getChildren().add(text1);
									}
								}
							}

							Printer printer = new Printer();
							printer.print(addtions, Color.GREEN, "+");
							printer.print(fixes, Color.BLUE, "•");
							printer.print(changes, Color.DARKORANGE, "~");
							printer.print(removals, Color.FIREBRICK, "-");
							printer.print(information, Color.BEIGE, "\uD83D\uDEC8");

						}
					});
					updateSelectorBox.getChildren().add(listing);
				});

			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} finally {
				Platform.runLater(() -> statusText.setText(defaultText));
				(queryThread = new Thread(this)).setDaemon(true);
			}
		}
	});
	{
		queryThread.setDaemon(true);
	}

	private void refresh() {
		queryThread.start();
	}

	private @FXML void goHome(ActionEvent e) {
		try {
			Stuff.displayWindow(new HomeWindow());
		} catch (WindowLoadFailureException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateGUI.fxml"));
		loader.setController(this);
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			throw new WindowLoadFailureException("Failed to load the UI for the Updates application's window.", e);
		}
	}

}
