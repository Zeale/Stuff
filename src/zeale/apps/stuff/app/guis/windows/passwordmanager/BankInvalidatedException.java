package zeale.apps.stuff.app.guis.windows.passwordmanager;

/**
 * Thrown when an operation is requested on an invalidated {@link PasswordBank}
 * object. {@link PasswordBankException#name} is always <code>null</code> for
 * these exceptions.
 * 
 * @author Zeale
 *
 */
public class BankInvalidatedException extends PasswordBankException {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	BankInvalidatedException(String message) {
		super(message, null);
	}

	BankInvalidatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace, null);
	}

	BankInvalidatedException(String message, Throwable cause) {
		super(message, cause, null);
	}

	BankInvalidatedException() {
		super(null);
	}

	BankInvalidatedException(Throwable cause) {
		super(cause, null);
	}

}
