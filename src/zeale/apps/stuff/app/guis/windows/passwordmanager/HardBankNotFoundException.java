package zeale.apps.stuff.app.guis.windows.passwordmanager;

public class HardBankNotFoundException extends PasswordBankException {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	HardBankNotFoundException(String message, String name) {
		super(message, name);
	}

	HardBankNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
			String name) {
		super(message, cause, enableSuppression, writableStackTrace, name);
	}

	HardBankNotFoundException(String message, Throwable cause, String name) {
		super(message, cause, name);
	}

	HardBankNotFoundException(String name) {
		super(name);
	}

	HardBankNotFoundException(Throwable cause, String name) {
		super(cause, name);
	}

}
