package zeale.apps.stuff.app.guis.windows.webrequests;

import java.util.Map;

interface WebRequestMethod {

	class WebRequestException extends Exception {

		/**
		 * SUID
		 */
		private static final long serialVersionUID = 1L;

		public WebRequestException() {
			super();
		}

		public WebRequestException(String message, Throwable cause) {
			super(message, cause);
		}

		public WebRequestException(String message) {
			super(message);
		}

		public WebRequestException(Throwable cause) {
			super(cause);
		}

	}

	String preview(String url, String userAgent, Map<String, String> params, String body) throws WebRequestException;

	String send(String url, String userAgent, Map<String, String> params, String body) throws WebRequestException;
}
