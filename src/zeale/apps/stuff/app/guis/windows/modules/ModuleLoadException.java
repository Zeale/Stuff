package zeale.apps.stuff.app.guis.windows.modules;

class ModuleLoadException extends Exception {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public ModuleLoadException() {
	}

	protected ModuleLoadException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ModuleLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public ModuleLoadException(String message) {
		super(message);
	}

	public ModuleLoadException(Throwable cause) {
		super(cause);
	}

}
