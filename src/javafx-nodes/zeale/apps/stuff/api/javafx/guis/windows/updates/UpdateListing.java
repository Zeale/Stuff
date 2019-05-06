package zeale.apps.stuff.api.javafx.guis.windows.updates;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class UpdateListing extends HBox {

	private ImageView iconView;
	private final ObjectProperty<Image> icon = new SimpleObjectProperty<>();
	private final ReadOnlyBooleanWrapper hasIcon = new ReadOnlyBooleanWrapper();
	private final Text text = new Text();

	public boolean isHasIcon() {
		return hasIcon.get();
	}

	public ReadOnlyBooleanProperty hasIconProperty() {
		return hasIcon.getReadOnlyProperty();
	}

	public void setIcon(Image icon) {
		this.icon.set(icon);
	}

	public Image getIcon() {
		return icon.get();
	}

	public ObjectProperty<Image> iconProperty() {
		return icon;
	}

	{
		hasIcon.bind(icon.isNotNull());

		setStyle("-fx-border-width:0 0 1 0;-fx-border-color:-stuff-dark;");

		icon.addListener(new ChangeListener<Image>() {

			@Override
			public void changed(ObservableValue<? extends Image> observable, Image oldValue, Image newValue) {
				if (newValue == null) {
					getChildren().remove(iconView);
					iconView = null;
				} else if (oldValue == null) {
					(iconView = new ImageView(newValue)).setFitHeight(64);
					iconView.setFitWidth(64);
					getChildren().add(0, iconView);
				} else {
					iconView.setImage(newValue);
				}
			}
		});

		setAlignment(Pos.CENTER);

		getChildren().add(text);
	}

	public UpdateListing(String text, Image icon) {
		setIcon(icon);
		setText(text);
	}

	public UpdateListing(String text) {
		setText(text);
	}

	public UpdateListing(Image icon) {
		setIcon(icon);
	}

	public UpdateListing() {
	}

	public final StringProperty textProperty() {
		return this.text.textProperty();
	}

	public final String getText() {
		return this.textProperty().get();
	}

	public final void setText(final String text) {
		this.textProperty().set(text);
	}

}
