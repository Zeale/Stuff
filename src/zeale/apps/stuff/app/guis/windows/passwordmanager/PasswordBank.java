package zeale.apps.stuff.app.guis.windows.passwordmanager;

import java.io.File;

import org.alixia.javalibrary.files.FileTools;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import zeale.apps.stuff.Stuff;
import zeale.apps.stuff.utilities.java.references.PhoenixReference;
import zeale.apps.stuff.utilities.java.references.SporadicPhoenixReference;

public class PasswordBank {

	private boolean valid = true;

	public static final SporadicPhoenixReference<File> PASSWORD_BANK_DIRECTORY = new SporadicPhoenixReference<File>(
			true) {

		@Override
		protected File generate() throws Exception {
			File file = new File(Stuff.APP_DATA_DIRECTORY, "Password Manager/Password Banks");
			if (!(file.isDirectory() || file.mkdirs()))
				throw new Exception(
						"Failed to locate/create the Password Bank folder: " + file.getAbsolutePath() + ".");
			return file;
		}
	};

	public static final PhoenixReference<ObservableMap<String, PasswordBank>> LOADED_BANK_LIST = PhoenixReference
			.create(true, FXCollections::observableHashMap);

	/**
	 * Returns <code>true</code> if there is a {@link PasswordBank} on the hard
	 * drive (i.e., a folder on the hard drive in the password bank directory) with
	 * this name. This can be used to check if creating a new {@link PasswordBank}
	 * will fail due to a bank with the same name already existing, or to verify if
	 * loading a bank by a specific name will be possible.
	 * 
	 * @param name The name of the {@link PasswordBank}.
	 * @return <code>true</code> if the bank exists already on the hard drive,
	 *         <code>false</code> otherwise.
	 */
	public static boolean doesHardBankExist(String name) {
		return getFile(name).isDirectory();
	}

	/**
	 * Loads the {@link PasswordBank} by the specified name into memory and returns
	 * it.
	 * 
	 * @param name The name of the bank.
	 * @return The newly created {@link PasswordBank} object.
	 */
	public static PasswordBank loadBank(String name) {
		if (isBankLoaded(name))
			throw new BankAlreadyLoadedException(name);
		/* TODO */
	}

	/**
	 * Loads the hard bank by the specified name if not already loaded, then returns
	 * the {@link PasswordBank} for the loaded hard bank.
	 * 
	 * @param name The name of the hard bank.
	 * @return The (newly) loaded hard bank's {@link PasswordBank}.
	 */
	public static PasswordBank getBank(String name) {
		return isBankLoaded(name) ? getLoadedBank(name) : loadBank(name);
	}

	public static PasswordBank createHardBank(String name) {
		if (doesHardBankExist(name))
			throw new BankAlreadyExistsException(name);
		/* TODO */

	}

	public static boolean isBankLoaded(String name) {
		return LOADED_BANK_LIST.exists() && LOADED_BANK_LIST.get().containsKey(name);
	}

	public static PasswordBank getLoadedBank(String name) {
		if (!isBankLoaded(name))
			throw new BankNotLoadedException(name);
		return LOADED_BANK_LIST.get().get(name);
	}

	public static void deleteHardBank(String name) {
		if (!doesHardBankExist(name))
			throw new HardBankNotFoundException(name);
		File bankFile = getFile(name);
		FileTools.deltree(bankFile);
	}

	/**
	 * Returns the {@link File} that a bank with the specified name will use.
	 * 
	 * @param name The name of the {@link PasswordBank}.
	 * @return The {@link File} that it shall use.
	 */
	private static File getFile(String name) {
		return new File(PASSWORD_BANK_DIRECTORY.get(), name);
	}

	public static void unloadBank(String name) {
		if (!isBankLoaded(name))
			throw new BankNotLoadedException(name);
		LOADED_BANK_LIST.get().remove(name);
		getLoadedBank(name).unload();
	}

	public void unload() {
		if (!valid)
			throw new BankInvalidatedException();
		valid = false;
		/* TODO */
	}
}
