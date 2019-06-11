package zeale.apps.stuff.app.guis.windows.passwordmanager;

import java.io.IOException;
import java.util.concurrent.Callable;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.app.guis.windows.HomeWindow;
import zeale.apps.stuff.utilities.java.references.SporadicPhoenixReference;
import zeale.apps.stuff.utilities.java.references.SporadicPhoenixReference.RegenerationException;

public class PasswordManagerWindow extends Window {

	private static final Duration ACCOUNT_COUNT_COLOR_ANIMATION_DURATION = Duration.seconds(1.4);
	private @FXML Text accountCount, accountText;
	private @FXML PieChart securityChart;
	private PieChart.Data secureSlice = new PieChart.Data("Secure", 0.8),
			insecureSlice = new PieChart.Data("Insecure", 0.1), repeatedSlice = new PieChart.Data("Repeated", 0.1);

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	private @FXML void initialize() {
		SequentialTransition accountCountAnimation = new SequentialTransition(accountCount,
				new FillTransition(ACCOUNT_COUNT_COLOR_ANIMATION_DURATION, Color.YELLOW, Color.GREEN),
				new FillTransition(ACCOUNT_COUNT_COLOR_ANIMATION_DURATION, Color.GREEN, Color.BLUE),
				new FillTransition(ACCOUNT_COUNT_COLOR_ANIMATION_DURATION, Color.BLUE, Color.RED),
				new FillTransition(ACCOUNT_COUNT_COLOR_ANIMATION_DURATION, Color.RED, Color.YELLOW)),
				accountTextAnimation = new SequentialTransition(accountText,
						new FillTransition(ACCOUNT_COUNT_COLOR_ANIMATION_DURATION, Color.RED, Color.BLUE),
						new FillTransition(ACCOUNT_COUNT_COLOR_ANIMATION_DURATION, Color.BLUE, Color.GREEN),
						new FillTransition(ACCOUNT_COUNT_COLOR_ANIMATION_DURATION, Color.GREEN, Color.YELLOW),
						new FillTransition(ACCOUNT_COUNT_COLOR_ANIMATION_DURATION, Color.YELLOW, Color.RED));
		accountCountAnimation.setCycleCount(Animation.INDEFINITE);
		accountTextAnimation.setCycleCount(Animation.INDEFINITE);
		accountCountAnimation.play();
		accountTextAnimation.play();

		ObservableList<PieChart.Data> slices = FXCollections.observableArrayList(secureSlice, insecureSlice,
				repeatedSlice);
		securityChart.setData(slices);
		securityChart.setLegendVisible(false);
		updatePieChart();

	}

	private void updatePieChart() {
		secureSlice.getNode().setStyle("-fx-pie-color:green;");
		insecureSlice.getNode().setStyle("-fx-pie-color:red;");
		repeatedSlice.getNode().setStyle("-fx-pie-color:gold;");
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("PasswordManagerGUI.fxml"));
		loader.setController(this);
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			throw new WindowLoadFailureException("Failed to load the UI for the Password Manager window.", e);
		}
	}

	private @FXML void goHome(ActionEvent event) {
		try {
			Stuff.displayWindow(new HomeWindow());
		} catch (WindowLoadFailureException e) {
			Logging.err(e);
		}
	}

	private final SporadicPhoenixReference<Stage> createAccountWindow = SporadicPhoenixReference.create(true,
			(Callable<Stage>) () -> {
				Stage stage = Stuff.makeStage();
				new CreateAccountWindow().display(stage);
				return stage;
			}), viewAccountsWindow = SporadicPhoenixReference.create(true, (Callable<Stage>) () -> {
				Stage stage = Stuff.makeStage();
				new ViewAccountsWindow().display(stage);
				return stage;
			});

	private @FXML void createAccount(ActionEvent event) {
		try {
			createAccountWindow.get().show();
		} catch (RegenerationException e) {
			Logging.err(e.getCause());
		}
	}

	private @FXML void viewAccounts(ActionEvent event) {
		try {
			viewAccountsWindow.get().show();
		} catch (RegenerationException e) {
			Logging.err(e.getCause());
		}
	}

}
