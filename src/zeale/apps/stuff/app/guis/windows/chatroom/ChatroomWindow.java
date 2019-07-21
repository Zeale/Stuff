package zeale.apps.stuff.app.guis.windows.chatroom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import branch.alixia.unnamed.Datamap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.api.appprops.ApplicationProperties;
import zeale.apps.stuff.api.guis.windows.Window;
import zeale.apps.stuff.api.logging.Logging;
import zeale.apps.stuff.app.guis.windows.HomeWindow;
import zeale.apps.stuff.utilities.java.references.PhoenixReference;

public class ChatroomWindow extends Window {

	private final PhoenixReference<File> CHATROOM_STORAGE_DIRECTORY = new PhoenixReference<File>() {

		@Override
		protected File generate() {
			File file = new File(Stuff.APPLICATION_DATA, "Chatroom");
			file.mkdirs();
			return file;
		}
	};
	private final PhoenixReference<File> CACHED_LOGIN_FILE = new PhoenixReference<File>() {

		@Override
		protected File generate() {
			File file = new File(CHATROOM_STORAGE_DIRECTORY.get(), "cached-login.dat");
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return file;
		}
	};
	private final PhoenixReference<Datamap> CACHED_LOGIN_CREDENTIALS = new PhoenixReference<Datamap>() {

		@Override
		protected Datamap generate() {
			try {
				return Datamap.readLax(new FileInputStream(CACHED_LOGIN_FILE.get()));
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	};

	public static void launch() {
		System.out.println("Called");
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	private @FXML void initialize() {
		// TODO
	}

	private @FXML void goHome() {
		try {
			Stuff.displayWindow(new HomeWindow());
		} catch (WindowLoadFailureException e) {
			Logging.err("Failed to display the home window.");
			Logging.err(e);
		}
	}

	@Override
	protected void show(Stage stage, ApplicationProperties properties) throws WindowLoadFailureException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatroomGUI.fxml"));
		loader.setController(this);
		try {
			stage.setScene(new Scene(loader.load()));
		} catch (IOException e) {
			throw new WindowLoadFailureException("Failed to load Chatroom's UI.");
		}
	}

}
