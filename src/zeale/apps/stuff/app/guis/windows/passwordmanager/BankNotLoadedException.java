package zeale.apps.stuff.app.guis.windows.passwordmanager;

/**
 * Thrown when an operation is requested for a bank that has not yet been
 * loaded.
 * 
 * @author Zeale
 *
 */
public class BankNotLoadedException extends PasswordBankException {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	BankNotLoadedException(String message, String name) {
		super(message, name);
	}

	BankNotLoadedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
			String name) {
		super(message, cause, enableSuppression, writableStackTrace, name);
	}

	BankNotLoadedException(String message, Throwable cause, String name) {
		super(message, cause, name);
	}

	BankNotLoadedException(String name) {
		super(name);
	}

	BankNotLoadedException(Throwable cause, String name) {
		super(cause, name);
	}

}
