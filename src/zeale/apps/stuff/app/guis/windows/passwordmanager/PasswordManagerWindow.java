package zeale.apps.stuff.app.guis.windows.passwordmanager;

import java.io.IOException;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;

public class PasswordManagerWindow extends Window {

	private static final ColorAdjust CHEVRON_HOVER_COLOR_SHIFT = new ColorAdjust(-0.3, 0, 0, 0);
	private static final Duration ACCOUNT_COUNT_COLOR_ANIMATION_DURATION = Duration.seconds(1.4);
	private @FXML Text accountCount, accountText;

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
	}

	private final static Image SOLID_WIDE_CHEVRON_ICON = new Image(
			"zeale/apps/stuff/rsrc/app/guis/windows/passwordmanager/SolidWideChevron.png"),
			HOLLOW_WIDE_CHEVRON_ICON = new Image(
					"zeale/apps/stuff/rsrc/app/guis/windows/passwordmanager/HollowWideChevron.png");

	private @FXML void chevronHoverEntered(MouseEvent event) {
		ImageView chevron = (ImageView) event.getSource();
		chevron.setImage(SOLID_WIDE_CHEVRON_ICON);
		chevron.setEffect(CHEVRON_HOVER_COLOR_SHIFT);
	}

	private @FXML void chevronHoverExited(MouseEvent event) {
		ImageView chevron = (ImageView) event.getSource();
		chevron.setImage(HOLLOW_WIDE_CHEVRON_ICON);
		chevron.setEffect(null);
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

}
