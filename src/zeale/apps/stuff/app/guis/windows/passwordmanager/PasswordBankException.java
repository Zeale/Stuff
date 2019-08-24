package zeale.apps.stuff.app.guis.windows.passwordmanager;

/**
 * The superclass of all {@link PasswordBank}, specifically related exceptions.
 * Please note that the {@link #name} field will be <code>null</code> for all
 * {@link BankInvalidatedException}s.
 * 
 * @author Zeale
 *
 */
abstract class PasswordBankException extends RuntimeException {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;
	public final String name;

	PasswordBankException(String name) {
		this.name = name;
	}

	PasswordBankException(String message, String name) {
		super(message);
		this.name = name;
	}

	PasswordBankException(Throwable cause, String name) {
		super(cause);
		this.name = name;
	}

	PasswordBankException(String message, Throwable cause, String name) {
		super(message, cause);
		this.name = name;
	}

	PasswordBankException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
			String name) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.name = name;
	}

}
