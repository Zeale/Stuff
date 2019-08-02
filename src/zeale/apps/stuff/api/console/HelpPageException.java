package zeale.apps.stuff.api.console;

public final class HelpPageException extends Exception {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	public final int page, maxPage;

	public HelpPageException(int page, int maxPage) {
		this.page = page;
		this.maxPage = maxPage;
	}

}
