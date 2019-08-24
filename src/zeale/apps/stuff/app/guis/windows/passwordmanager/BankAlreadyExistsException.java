package zeale.apps.stuff.app.guis.windows.passwordmanager;

class BankAlreadyExistsException extends PasswordBankException {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	BankAlreadyExistsException(String message, String name) {
		super(message, name);
	}

	BankAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
			String name) {
		super(message, cause, enableSuppression, writableStackTrace, name);
	}

	BankAlreadyExistsException(String message, Throwable cause, String name) {
		super(message, cause, name);
	}

	BankAlreadyExistsException(String name) {
		super(name);
	}

	BankAlreadyExistsException(Throwable cause, String name) {
		super(cause, name);
	}

}
