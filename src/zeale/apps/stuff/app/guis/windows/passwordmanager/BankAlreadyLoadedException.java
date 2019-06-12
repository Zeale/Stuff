package zeale.apps.stuff.app.guis.windows.passwordmanager;

/**
 * Thrown when an attempt is made to load a {@link PasswordBank} but the bank is
 * already loaded.
 * 
 * @author Zeale
 *
 */
public class BankAlreadyLoadedException extends PasswordBankException {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	BankAlreadyLoadedException(String message, String name) {
		super(message, name);
	}

	BankAlreadyLoadedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
			String name) {
		super(message, cause, enableSuppression, writableStackTrace, name);
	}

	BankAlreadyLoadedException(String message, Throwable cause, String name) {
		super(message, cause, name);
	}

	BankAlreadyLoadedException(String name) {
		super(name);
	}

	BankAlreadyLoadedException(Throwable cause, String name) {
		super(cause, name);
	}

}
