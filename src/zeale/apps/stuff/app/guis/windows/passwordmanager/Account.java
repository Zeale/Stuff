package zeale.apps.stuff.app.guis.windows.passwordmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import branch.alixia.unnamed.Datamap;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

class Account {

	private final StringProperty property(String name) {
		SimpleStringProperty prop = new SimpleStringProperty(this, name);
		prop.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
			if (newValue == null)
				rem(name);
			else
				put(name, newValue);
		});
		return prop;
	}

	public static Account load(File file) throws FileNotFoundException {
		return load(new FileInputStream(file));
	}

	public static Account load(InputStream source) {
		return new Account(Datamap.read(source));
	}

	private void rem(String key) {
		map.remove(key);
	}

	private void put(String key, String value) {
		map.put(key, value);
	}

	private final Datamap map;

	private Account(Datamap map) {
		this.map = map;
	}

	public Account() {
		this(new Datamap());
	}

	private final StringProperty username = property("username"), email = property("email"),
			password = property("password");

	public final StringProperty usernameProperty() {
		return this.username;
	}

	public final String getUsername() {
		return this.usernameProperty().get();
	}

	public final void setUsername(final String username) {
		this.usernameProperty().set(username);
	}

	public final StringProperty emailProperty() {
		return this.email;
	}

	public final String getEmail() {
		return this.emailProperty().get();
	}

	public final void setEmail(final String email) {
		this.emailProperty().set(email);
	}

	public final StringProperty passwordProperty() {
		return this.password;
	}

	public final String getPassword() {
		return this.passwordProperty().get();
	}

	public final void setPassword(final String password) {
		this.passwordProperty().set(password);
	}

}
